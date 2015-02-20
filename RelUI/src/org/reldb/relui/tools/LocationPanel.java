package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class LocationPanel extends Composite {
	private Text textDatabase;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LocationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 8);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Database:");
		
		textDatabase = new Text(this, SWT.BORDER);
		fd_lblNewLabel.right = new FormAttachment(textDatabase, -6);
		FormData fd_textDatabase = new FormData();
		fd_textDatabase.left = new FormAttachment(0, 136);
		fd_textDatabase.top = new FormAttachment(0, 5);
		textDatabase.setLayoutData(fd_textDatabase);
		
		Button btnChooser = new Button(this, SWT.NONE);
		fd_textDatabase.right = new FormAttachment(btnChooser, -6);
		FormData fd_btnChooser = new FormData();
		fd_btnChooser.right = new FormAttachment(100);
		fd_btnChooser.top = new FormAttachment(0, 1);
		btnChooser.setLayoutData(fd_btnChooser);
		btnChooser.setText("...");
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		fd_lblNewLabel.left = new FormAttachment(toolBar, 6);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0, 5);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		
		ToolItem tltmNew = new ToolItem(toolBar, SWT.NONE);
		tltmNew.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		tltmNew.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		tltmNew.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));

		ToolItem tltmOpenLocal = new ToolItem(toolBar, SWT.NONE);
		tltmOpenLocal.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		tltmOpenLocal.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		tltmOpenLocal.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		
		ToolItem tltmOpenRemote = new ToolItem(toolBar, SWT.NONE);
		tltmOpenRemote.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		tltmOpenRemote.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		tltmOpenRemote.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
	}
}
