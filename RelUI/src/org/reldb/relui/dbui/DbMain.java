package org.reldb.relui.dbui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relui.tools.MainPanel;

/** Root of RelUI. */
public class DbMain {
	private static MainPanel mainPanel;
	private static DirectoryDialog openDatabaseDialog;
	private static DirectoryDialog newDatabaseDialog;
	private static RemoteDatabaseDialog remoteDatabaseDialog;

	private static boolean noLocalRel = true;

    public static Shell getShell() {
    	return mainPanel.getShell();
    }
    
    public static boolean isNoLocalRel() {
    	return noLocalRel;
    }

	public static void run(Composite parent) {
		mainPanel = new MainPanel(parent, SWT.None);
		initialise();
	}
    
	public static void setStatus(String s) {
		mainPanel.setStatus(s);
	}
	
	private static void initialise() {
		openDatabaseDialog = new DirectoryDialog(getShell());
		openDatabaseDialog.setText("Open Database");
		openDatabaseDialog.setMessage("Select a folder that contains a database.");
		openDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		newDatabaseDialog = new DirectoryDialog(getShell());
		newDatabaseDialog.setText("Create Database");
		newDatabaseDialog.setMessage("Select a folder to hold a new database.");
		newDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		remoteDatabaseDialog = new RemoteDatabaseDialog(getShell());
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private int failureCount = 0;
			public void uncaughtException(Thread t, Throwable e) {
				if (failureCount > 1) {
					System.err.println("SYSTEM ERROR!  It's gotten even worse.  This is a last-ditch attempt to escape.");
					failureCount++;
					Thread.setDefaultUncaughtExceptionHandler(null);
					System.exit(1);
					return;
				} if (failureCount > 0) {
					System.err.println("SYSTEM ERROR!  Things have gone so horribly wrong that we can't recover or even pop up a message.  I hope someone sees this...\nShutting down now, if we can.");
					failureCount++;
					System.exit(1);
					return;
				}
				failureCount++;
				if (e instanceof OutOfMemoryError) {
					System.err.println("Out of memory!");
					e.printStackTrace();
					mainPanel.dispose();
					MessageDialog.openError(getShell(), "OUT OF MEMORY", "Out of memory!  Shutting down NOW!");
				} else {
					System.err.println("Unknown error: " + t);
					e.printStackTrace();
					mainPanel.dispose();
					MessageDialog.openError(getShell(), "Unexpected Error", e.toString());
				}
				System.exit(1);						
			}				
		});
    	try {
    		Class.forName("org.reldb.rel.Rel");
    		noLocalRel = false;
    	} catch (ClassNotFoundException cnfe) {
    		noLocalRel = true;
        }
    	String defaultDatabasePath = System.getProperty("user.home");
		DbTab defaultDb = new DbTab();
		defaultDb.openLocalDatabase(defaultDatabasePath);
		DbMain.setSelection(0);
	}
	
	public static DbTab getCurrentDbTab() {
		return (DbTab)mainPanel.getTabFolder().getSelection();
	}

	public static void setSelection(int i) {
		mainPanel.getTabFolder().setSelection(0);
	}

	public static void newDatabase() {
		Object result = newDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().newDatabase(result.toString());
	}

	public static void openLocalDatabase() {
		Object result = openDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().openLocalDatabase(result.toString());
	}

	public static void openRemoteDatabase() {
		Object result = remoteDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().openRemoteDatabase(result.toString());
	}

	public static void options() {
		// TODO Auto-generated method stub
	}

	public static CTabFolder getTabFolder() {
		return mainPanel.getTabFolder();
	}
	
}