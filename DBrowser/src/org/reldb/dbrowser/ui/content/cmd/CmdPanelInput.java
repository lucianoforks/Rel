package org.reldb.dbrowser.ui.content.cmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
import org.reldb.dbrowser.commands.ManagedToolbar;
import org.reldb.dbrowser.ui.Tabs;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageCmd;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class CmdPanelInput extends Composite {

	private CmdPanelBottom cmdPanelBottom;
	private CmdStyledText inputText;

	private CommandActivator tlitmPrevHistory;
	private CommandActivator tlitmNextHistory;
	private CommandActivator tlitmCopyToOutput;
	private CommandActivator tlitmWrap;

	private Vector<String> entryHistory = new Vector<String>();
	private int currentHistoryItem = 0;

	private FileDialog loadDialog = null;
	private FileDialog loadPathDialog = null;
	private FileDialog saveDialog = null;
	private FileDialog saveHistoryDialog = null;

	private PreferenceChangeListener fontPreferenceChangeListener;

	private CmdPanelOutput cmdPanelOutput;

	private boolean copyInputToOutput = true;

	private static final Color backgroundHighlight = SWTResourceManager.getColor(250, 255, 252);

	private SpecialCharacters specialCharacterDisplay;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param cmdPanelOutput
	 * @param style
	 */
	public CmdPanelInput(Composite parent, CmdPanelOutput cmdPanelOutput, int cmdStyle, String[] keywords) {
		super(parent, SWT.NONE);
		setLayout(new FormLayout());

		this.cmdPanelOutput = cmdPanelOutput;
		
		ManagedToolbar toolBar = new ManagedToolbar(this);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);

		inputText = new CmdStyledText(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		FormData fd_inputText = new FormData();
		fd_inputText.right = new FormAttachment(toolBar, 0, SWT.RIGHT);
		fd_inputText.top = new FormAttachment(toolBar);
		fd_inputText.left = new FormAttachment(toolBar, 0, SWT.LEFT);
		inputText.setLayoutData(fd_inputText);

		RelLineStyler lineStyler = new RelLineStyler(keywords);

		inputText.addLineStyleListener(lineStyler);
		inputText.addModifyListener(e -> {
			lineStyler.parseBlockComments(inputText.getText());
			inputText.redraw();
			if (CmdPanelOutput.isLastNonWhitespaceNonCommentCharacter(inputText.getText(), ';')) {
				cmdPanelBottom.setRunButtonPrompt("Execute (F5)");
			} else {
				cmdPanelBottom.setRunButtonPrompt("Evaluate (F5)");
			}
		});

		inputText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (((e.stateMask & SWT.CTRL) != 0 || (e.stateMask & SWT.COMMAND) != 0) && inputText.isEnabled()) {
					switch (e.keyCode) {
					case 'a':
						inputText.selectAll();
						return;
					case 'f':
						inputText.findReplace();
						return;
					case 'y':
						inputText.redo();
						return;
					case 'z':
						if ((e.stateMask & SWT.SHIFT) != 0)
							inputText.redo();
						else
							inputText.undo();
						return;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 0x100000e && inputText.isEnabled())
					run();
			}
		});

		inputText.addCaretListener(event -> {
			int offset = event.caretOffset;
			try {
				int row = inputText.getLineAtOffset(offset);
				int characterIndex = offset - inputText.getOffsetAtLine(row);
				String line = inputText.getLine(row);
				int tabSize = inputText.getTabs();
				int displayColumn = Tabs.characterIndexToDisplayColumn(tabSize, line, characterIndex);
				cmdPanelBottom.setRowColDisplay("" + (row + 1) + ":" + (displayColumn + 1));
			} catch (Exception ble) {
				cmdPanelBottom.setRowColDisplay("?:?");
			}
		});

		inputText.addLineBackgroundListener(event -> {
			int line = inputText.getLineAtOffset(event.lineOffset);
			if (!((line & 1) == 0))
				event.lineBackground = backgroundHighlight;
		});

		cmdPanelBottom = new CmdPanelBottom(this, cmdStyle, SWT.NONE) {
			@Override
			public void go() {
				run();
			}
			@Override
			public void cancel() {
				stop();
			}
			@Override
			public void execute() {
				CmdPanelInput.this.execute();
			}
			@Override
			public void evaluate() {
				CmdPanelInput.this.evaluate();
			}
		};
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom, 191);
		FormData fd_cmdPanelBottom = new FormData();
		fd_cmdPanelBottom.left = new FormAttachment(0);
		fd_cmdPanelBottom.right = new FormAttachment(100);
		fd_cmdPanelBottom.bottom = new FormAttachment(100);
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom);
		cmdPanelBottom.setLayoutData(fd_cmdPanelBottom);
		
		if ((cmdStyle & CmdPanel.NO_INPUT_TOOLBAR) == 0) {
			new CommandActivator(Commands.Do.SpecialCharacters, toolBar, "characters", SWT.NONE, "Special characters", e -> specialCharacters());
			tlitmPrevHistory = new CommandActivator(Commands.Do.PreviousHistory, toolBar, "previousIcon", SWT.NONE, "Load previous historical entry", e -> previousHistory());
			tlitmNextHistory = new CommandActivator(Commands.Do.NextHistory, toolBar, "nextIcon", SWT.NONE, "Load next historical entry", e -> nextHistory());
			new CommandActivator(null, toolBar, "clearIcon", SWT.NONE, "Clear", e -> clear());
			new CommandActivator(null, toolBar, "undo", SWT.NONE, "Undo", e -> inputText.undo());
			new CommandActivator(null, toolBar, "redo", SWT.NONE, "Redo", e -> inputText.redo());
			new CommandActivator(null, toolBar, "cut", SWT.NONE, "Cut", e -> inputText.cut());
			new CommandActivator(null, toolBar, "copy", SWT.NONE, "Copy", e -> inputText.copy());
			new CommandActivator(null, toolBar, "paste", SWT.NONE, "Paste", e -> inputText.paste());
			new CommandActivator(null, toolBar, "selectAll", SWT.NONE, "Select all", e -> inputText.selectAll());
			new CommandActivator(null, toolBar, "delete", SWT.NONE, "Delete", e -> delete());
			new CommandActivator(Commands.Do.FindReplace, toolBar, "edit_find_replace", SWT.NONE, "Find/Replace", e -> inputText.findReplace());
			new CommandActivator(Commands.Do.LoadFile, toolBar, "loadIcon", SWT.NONE, "Load file", e -> loadFile());
			new CommandActivator(Commands.Do.InsertFile, toolBar, "loadInsertIcon", SWT.NONE, "Load and insert file", e -> insertFile());
			new CommandActivator(Commands.Do.InsertFileName, toolBar, "pathIcon", SWT.NONE, "Get file path", e -> insertFileName());
			new CommandActivator(Commands.Do.SaveFile, toolBar, "saveIcon", SWT.NONE, "Save", e -> saveFile());
			new CommandActivator(Commands.Do.SaveHistory, toolBar, "saveHistoryIcon", SWT.NONE, "Save history", e -> saveHistory());
			tlitmCopyToOutput = new CommandActivator(Commands.Do.CopyInputToOutput, toolBar, "copyToOutputIcon", SWT.CHECK, "Copy input to output", e -> setCopyInputToOutput(tlitmCopyToOutput.getSelection()));
			tlitmWrap = new CommandActivator(Commands.Do.WrapText, toolBar, "wrapIcon", SWT.CHECK, "Wrap text", e -> inputText.setWordWrap(tlitmWrap.getSelection()));
			
			tlitmCopyToOutput.setSelection(true);
			tlitmWrap.setSelection(true);

			setupButtons();
		}

		setupFont();
		fontPreferenceChangeListener = new PreferenceChangeAdapter("CmdPanelInput_font") {
			@Override
			public void preferenceChange(PreferenceChangeEvent preferenceChangeEvent) {
				setupFont();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);

		specialCharacterDisplay = new SpecialCharacters(parent.getShell(), inputText);
	}

	public void selectAll() {
		inputText.selectAll();
	}

	public void specialCharacters() {
		specialCharacterDisplay.open();
	}

	public void previousHistory() {
		inputText.setText(getPreviousHistoryItem());
		inputText.setSelection(0, inputText.getText().length());
		inputText.setFocus();
	}

	public void nextHistory() {
		inputText.setText(getNextHistoryItem());
		inputText.setSelection(0, inputText.getText().length());
		inputText.setFocus();
	}

	public void loadFile() {
		ensureLoadDialogExists();
		loadDialog.setFileName("");
		loadDialog.setText("Load File");
		String fname = loadDialog.open();
		if (fname == null)
			return;
		loadFile(fname);
		ensureSaveDialogExists();
		saveDialog.setFileName(loadDialog.getFileName());
		saveDialog.setFilterPath(loadDialog.getFilterPath());
	}

	public void insertFile() {
		ensureLoadDialogExists();
		loadDialog.setFileName("");
		loadDialog.setText("Load and Insert File");
		String fname = loadDialog.open();
		if (fname == null)
			return;
		loadInsertFile(fname);
	}

	public void insertFileName() {
		ensureLoadPathDialogExists();
		loadPathDialog.setFileName("");
		loadPathDialog.setText("Get File Path");
		String fname = loadPathDialog.open();
		if (fname == null)
			return;
		insertInputText('"' + fname.replace("\\", "\\\\") + '"');
	}

	public void saveFile() {
		ensureSaveDialogExists();
		saveDialog.setText("Save Input");
		if (saveDialog.getFileName().length() == 0)
			saveDialog.setFileName(getDefaultSaveFileName());
		String fname = saveDialog.open();
		if (fname == null)
			return;
		try {
			BufferedWriter f = new BufferedWriter(new FileWriter(fname));
			f.write(inputText.getText());
			f.close();
			announcement("Saved " + fname);
		} catch (IOException ioe) {
			announceError(ioe.toString(), ioe);
		}
	}

	public void saveHistory() {
		ensureSaveHistoryDialogExists();
		saveHistoryDialog.setText("Save History");
		if (saveHistoryDialog.getFileName().length() == 0)
			saveHistoryDialog.setFileName("InputHistory");
		String fname = saveHistoryDialog.open();
		if (fname == null)
			return;
		try {
			BufferedWriter f = new BufferedWriter(new FileWriter(fname));
			for (int i = 0; i < getHistorySize(); i++) {
				f.write("// History item #" + (i + 1) + "\n");
				f.write(getHistoryItemAt(i));
				f.write("\n\n");
			}
			f.write("// Current entry" + "\n");
			f.write(inputText.getText());
			f.close();
			announcement("Saved " + fname);
		} catch (IOException ioe) {
			announceError(ioe.toString(), ioe);
		}
	}

	private void clear() {
		inputText.setText("");
		inputText.setFocus();
	}

	protected String getDefaultSaveFileName() {
		return "Untitled";
	}

	public void copyOutputToInput() {
		String selection = cmdPanelOutput.getSelectionText();
		if (selection.length() == 0)
			insertInputText(inputText.getText() + cmdPanelOutput.getText());
		else
			insertInputText(inputText.getText() + selection);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);
		super.dispose();
	}

	/**
	 * Must invoke this after processing invoked by run button has finished, and
	 * we're ready to receive another 'run' notification.
	 */
	public void done() {
		showRunningStop();
	}

	public void delete() {
		inputText.invokeAction(ST.DELETE_NEXT);
	}

	private void setupFont() {
		inputText.setFont(Preferences.getPreferenceFont(getDisplay(), PreferencePageCmd.CMD_FONT));
	}

	/** Override to receive notification that the cancel button has been pressed. */
	private void notifyStop() {
		cmdPanelOutput.notifyStop();
	}

	/**
	 * Override to receive notification that the run button has been pressed. Must
	 * invoke done() when ready to receive another notification.
	 */
	private void notifyGo(String text) {
		cmdPanelOutput.go(text, copyInputToOutput);
	}

	private void notifyEvaluate(String text) {
		cmdPanelOutput.evaluate(text, copyInputToOutput);
	}
	
	private void notifyExecute(String text) {
		cmdPanelOutput.execute(text, copyInputToOutput);
	}
	
	private void replaceInputText(String newText) {
		inputText.setText(newText);
	}

	private void insertInputText(String newText) {
		int insertionStart;
		int insertionEnd;
		Point selectionRange = inputText.getSelectionRange();
		if (selectionRange == null) {
			insertionStart = insertionEnd = inputText.getCaretOffset();
		} else {
			insertionStart = selectionRange.x;
			insertionEnd = selectionRange.y + insertionStart;
		}
		String before = inputText.getText().substring(0, insertionStart);
		String after = inputText.getText().substring(insertionEnd, inputText.getText().length());
		inputText.setText(before + newText + after);
		inputText.setCaretOffset(before.length() + newText.length());
	}

	public boolean setFocused() {
		return (inputText.isDisposed()) ? false : inputText.setFocus();
	}

	private String loadFileImage(String fname) throws Exception {
		BufferedReader f = new BufferedReader(new FileReader(fname));
		StringBuffer fileImage = new StringBuffer();
		String line;
		while ((line = f.readLine()) != null) {
			if (fileImage.length() > 0)
				fileImage.append('\n');
			fileImage.append(line);
		}
		f.close();
		return fileImage.toString();
	}

	public void loadFile(String fname) {
		try {
			String fileImage = loadFileImage(fname);
			replaceInputText(fileImage.toString());
			announcement("Loaded " + fname);
		} catch (Exception ioe) {
			announceError(ioe.toString(), ioe);
		}
	}

	public void loadInsertFile(String fname) {
		try {
			String fileImage = loadFileImage(fname);
			insertInputText(fileImage.toString());
			announcement("Loaded " + fname);
		} catch (Exception ioe) {
			announceError(ioe.toString(), ioe);
		}
	}

	public ErrorInformation handleError(StringBuffer errorBuffer) {
		ErrorInformation eInfo = parseErrorInformationFrom(errorBuffer.toString());
		if (eInfo != null) {
			int offset = 0;
			try {
				if (eInfo.getLine() > 0) {
					int row = eInfo.getLine() - 1;
					offset = inputText.getOffsetAtLine(row);
					if (eInfo.getColumn() > 0) {
						int outputTabSize = 4; // should match parserEngine.setTabSize() in
												// org.reldb.rel.v<n>.interpreter.Interpreter
						String inputLine = inputText.getLine(row);
						int characterIndex = Tabs.displayColumnToCharacterIndex(outputTabSize, inputLine,
								eInfo.getColumn() - 1);
						offset = characterIndex + inputText.getOffsetAtLine(row);
					}
				}
				inputText.setCaretOffset(offset);
				if (eInfo.getBadToken() != null)
					inputText.setSelection(offset, offset + eInfo.getBadToken().length());
			} catch (Exception e) {
				System.out.println("CmdPanelInput: Unable to position to line " + eInfo.getLine() + ", column "
						+ eInfo.getColumn());
			}
		} else
			System.out.println("CmdPanelInput: Unable to locate error in " + errorBuffer.toString());
		errorBuffer = null;
		inputText.setFocus();
		return eInfo;
	}

	public void setText(String text) {
		inputText.setText(text);
	}

	public String getText() {
		return inputText.getText();
	}

	public void setHistory(Vector<String> history) {
		entryHistory = history;
		currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
	}

	private void showRunningStart() {
		cmdPanelBottom.setEnabledRunButton(false);
	}

	private void showRunningStop() {
		cmdPanelBottom.setEnabledRunButton(true);
	}

	/** Override to be notified that copyInputToOutput setting has changed. */
	protected void setCopyInputToOutput(boolean selection) {
		copyInputToOutput = selection;
	}

	/** Override to be notified that an announcement has been raised. */
	protected void announcement(String msg) {
		cmdPanelOutput.systemResponse(msg);
	}

	/** Override to be notified about an error. */
	protected void announceError(String msg, Throwable t) {
		cmdPanelOutput.badResponse(msg);
	}

	/** Get number of items in History. */
	private int getHistorySize() {
		return entryHistory.size();
	}

	/** Get history item. */
	private String getHistoryItemAt(int index) {
		if (index < 0 || index >= entryHistory.size())
			return null;
		return entryHistory.get(index);
	}

	/** Get previous history item. */
	private String getPreviousHistoryItem() {
		if (currentHistoryItem > 0)
			currentHistoryItem--;
		setupButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Get next history item. */
	private String getNextHistoryItem() {
		currentHistoryItem++;
		if (currentHistoryItem >= entryHistory.size())
			currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	protected void notifyHistoryAdded(String historyItem) {
	}

	/** Add a history item. */
	private void addHistoryItem(String s) {
		if (entryHistory.size() > 0 && s.equals(entryHistory.get(entryHistory.size() - 1)))
			return;
		entryHistory.add(s);
		currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
		notifyHistoryAdded(s);
	}

	/** Set up history button status. */
	private void setupButtons() {
		if (tlitmPrevHistory == null)
			return;
		tlitmPrevHistory.setEnabled(currentHistoryItem > 0 && getHistorySize() > 1);
		tlitmNextHistory.setEnabled(currentHistoryItem < getHistorySize() - 1 && getHistorySize() > 1);
	}

	public void run() {
		showRunningStart();
		String text = saveToHistory();
		notifyGo(text);
	}
	
	private void evaluate() {
		showRunningStart();
		String text = saveToHistory();
		notifyEvaluate(text);
	}

	private void execute() {
		showRunningStart();
		String text = saveToHistory();
		notifyExecute(text);
	}

	public String saveToHistory() {
		String text = inputText.getText();
		if (text.trim().length() > 0)
			addHistoryItem(text);
		return text;
	}

	private void stop() {
		notifyStop();
	}

	private void ensureSaveDialogExists() {
		if (saveDialog == null) {
			saveDialog = new FileDialog(getShell(), SWT.SAVE);
			if (loadDialog != null)
				saveDialog.setFilterPath(loadDialog.getFilterPath());
			else
				saveDialog.setFilterPath(System.getProperty("user.home"));
			saveDialog.setFilterExtensions(new String[] { "*.rel", "*.*" });
			saveDialog.setFilterNames(new String[] { "Rel script", "All Files" });
			saveDialog.setText("Save");
			saveDialog.setOverwrite(true);
		}
	}

	private void ensureSaveHistoryDialogExists() {
		if (saveHistoryDialog == null) {
			saveHistoryDialog = new FileDialog(getShell(), SWT.SAVE);
			saveHistoryDialog.setFilterPath(System.getProperty("user.home"));
			saveHistoryDialog.setFilterExtensions(new String[] { "*.rel", "*.*" });
			saveHistoryDialog.setFilterNames(new String[] { "Rel script", "All Files" });
			saveHistoryDialog.setText("Save Input History");
			saveHistoryDialog.setOverwrite(true);
		}
	}

	private void ensureLoadDialogExists() {
		if (loadDialog == null) {
			loadDialog = new FileDialog(getShell(), SWT.OPEN);
			if (saveDialog != null)
				loadDialog.setFilterPath(saveDialog.getFilterPath());
			else
				loadDialog.setFilterPath(System.getProperty("user.home"));
			loadDialog.setFilterExtensions(new String[] { "*.rel", "*.*" });
			loadDialog.setFilterNames(new String[] { "Rel script", "All Files" });
			loadDialog.setText("Load File");
		}
	}

	private void ensureLoadPathDialogExists() {
		if (loadPathDialog == null) {
			loadPathDialog = new FileDialog(getShell(), SWT.OPEN);
			loadPathDialog.setFilterPath(System.getProperty("user.home"));
			loadPathDialog.setFilterExtensions(new String[] { "*.*" });
			loadPathDialog.setFilterNames(new String[] { "All Files" });
			loadPathDialog.setText("Load Path");
		}
	}

	public static class ErrorInformation {
		private int line;
		private int column;
		private String badToken;
		private String errorMessage;

		ErrorInformation(String errorMessage, int line, int column, String badToken) {
			this.errorMessage = errorMessage;
			this.line = line;
			this.column = column;
			this.badToken = badToken;
		}

		int getLine() {
			return line;
		}

		int getColumn() {
			return column;
		}

		String getBadToken() {
			return badToken;
		}

		public String toString() {
			String output = "Error in line " + line;
			if (column >= 0)
				output += ", column " + column;
			if (badToken != null)
				output += " near " + badToken;
			return output;
		}

		public String getMessage() {
			return errorMessage;
		}
	}

	private ErrorInformation parseErrorInformationFrom(String eInfo) {
		String eMsg = eInfo;
		try {
			String badToken = null;
			String[] errorPrefix = { "ERROR: Encountered ", "ERROR: Lexical error " };
			for (String errorEncountered : errorPrefix)
				if (eInfo.startsWith(errorEncountered)) {
					String atLineText = "at line ";
					int atLineTextPosition = eInfo.indexOf(atLineText);
					int lastBadTokenCharPosition = eInfo.lastIndexOf('"', atLineTextPosition);
					if (lastBadTokenCharPosition >= 0)
						badToken = eInfo.substring(errorEncountered.length() + 1, lastBadTokenCharPosition);
				}
			String lineText = "line ";
			int locatorStart = eInfo.toLowerCase().indexOf(lineText);
			if (locatorStart >= 0) {
				int line = 0;
				int column = 0;
				eInfo = eInfo.substring(locatorStart + lineText.length());
				int nonNumberPosition = 0;
				while (nonNumberPosition < eInfo.length() && Character.isDigit(eInfo.charAt(nonNumberPosition)))
					nonNumberPosition++;
				String lineString = eInfo.substring(0, nonNumberPosition);
				try {
					line = Integer.parseInt(lineString);
				} catch (NumberFormatException nfe) {
					return null;
				}
				int commaPosition = eInfo.indexOf(',');
				if (commaPosition > 0) {
					eInfo = eInfo.substring(commaPosition + 2);
					String columnText = "column ";
					if (eInfo.startsWith(columnText)) {
						eInfo = eInfo.substring(columnText.length());
						int endOfNumber = 0;
						while (endOfNumber < eInfo.length() && Character.isDigit(eInfo.charAt(endOfNumber)))
							endOfNumber++;
						String columnString = "";
						if (endOfNumber > 0 && endOfNumber < eInfo.length())
							columnString = eInfo.substring(0, endOfNumber);
						else
							columnString = eInfo;
						try {
							column = Integer.parseInt(columnString);
						} catch (NumberFormatException nfe) {
							return null;
						}
						String nearText = "near ";
						int nearTextPosition = eInfo.indexOf(nearText, endOfNumber);
						if (nearTextPosition > 0) {
							int lastQuotePosition = eInfo.lastIndexOf('\'');
							badToken = eInfo.substring(nearTextPosition + nearText.length() + 1, lastQuotePosition);
						} else {
							String encounteredText = "Encountered: \"";
							int encounteredTextPosition = eInfo.indexOf(encounteredText);
							if (encounteredTextPosition > 0) {
								int afterEncounteredTextPosition = encounteredTextPosition + encounteredText.length()
										+ 1;
								int lastQuotePosition = eInfo.indexOf('"', afterEncounteredTextPosition);
								badToken = eInfo.substring(afterEncounteredTextPosition - 1, lastQuotePosition);
							}
						}
						return new ErrorInformation(eMsg, line, column, badToken);
					} else
						return new ErrorInformation(eMsg, line, -1, badToken);
				} else
					return new ErrorInformation(eMsg, line, -1, badToken);
			}
		} catch (Throwable t) {
			System.out.println("CmdPanelInput: unable to parse " + eInfo + " due to " + t);
			t.printStackTrace();
		}
		return null;
	}
}
