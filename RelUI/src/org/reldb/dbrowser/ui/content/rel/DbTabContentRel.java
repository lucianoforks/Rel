package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.content.rel.RelPanel.DbTreeItem;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class DbTabContentRel extends Composite {

	private ToolItem tlitmBackup;
	private ToolItem tlitmPlay;
	private ToolItem tlitmNew;
	private ToolItem tlitmDrop;
	private ToolItem tlitmDesign;
	private ToolItem tlitmShowSystem;
    
	private RelPanel rel;
	
    private PreferenceChangeListener preferenceChangeListener;
	
	public DbTabContentRel(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		ToolBar toolBar = new ToolBar(this, SWT.None);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);
		
		rel = new RelPanel(parentTab, this, SWT.None);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		rel.setLayoutData(fd_composite);
	
		tlitmBackup = new ToolItem(toolBar, SWT.None);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentTab.makeBackup();
			}
		});
		
		tlitmPlay = new ToolItem(toolBar, SWT.None);
		tlitmPlay.setToolTipText("Activate");
		tlitmPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.playItem();
			}
		});
				
		tlitmNew = new ToolItem(toolBar, SWT.None);
		tlitmNew.setToolTipText("New");
		tlitmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.newItem();
			}
		});
		
		tlitmDrop = new ToolItem(toolBar, SWT.None);
		tlitmDrop.setToolTipText("Drop");
		tlitmDrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.dropItem();
			}
		});
		
		tlitmDesign = new ToolItem(toolBar, SWT.None);
		tlitmDesign.setToolTipText("Design");
		tlitmDesign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.designItem();
			}
		});
		
		tlitmShowSystem = new ToolItem(toolBar, SWT.CHECK);
		tlitmShowSystem.setToolTipText("Show system objects");
		tlitmShowSystem.setSelection(rel.getShowSystemObjects());
		tlitmShowSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.setShowSystemObjects(tlitmShowSystem.getSelection());
			}
		});
		
		setupIcons();
		
		rel.addDbTreeListener(new RelPanel.DbTreeListener() {
			public void select(DbTreeItem item) {
				tlitmPlay.setEnabled(item.canPlay());
				tlitmNew.setEnabled(item.canNew());
				tlitmDrop.setEnabled(item.canDrop());
				tlitmDesign.setEnabled(item.canDesign());
			}
		});
		
		preferenceChangeListener = new PreferenceChangeAdapter("DbTabContentRel") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		
		tlitmPlay.setEnabled(false);
		tlitmNew.setEnabled(false);
		tlitmDrop.setEnabled(false);
		tlitmDesign.setEnabled(false);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}
	
	private void setupIcons() {
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));
		tlitmPlay.setImage(IconLoader.loadIcon("play"));
		tlitmNew.setImage(IconLoader.loadIcon("item_add"));
		tlitmDrop.setImage(IconLoader.loadIcon("item_delete"));
		tlitmDesign.setImage(IconLoader.loadIcon("item_design"));
		tlitmShowSystem.setImage(IconLoader.loadIcon("gears"));
	}

	public void notifyIconSizeChange() {
		setupIcons();
	}

	public void redisplayed() {
		rel.redisplayed();
	}

}
