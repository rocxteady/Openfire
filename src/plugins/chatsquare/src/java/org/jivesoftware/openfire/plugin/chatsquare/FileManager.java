package org.jivesoftware.openfire.plugin.chatsquare;

import java.io.File;
import java.io.FilenameFilter;

public class FileManager {

	private static FileManager manager;

	public static FileManager sharedManager() {
		if (manager == null) {
			manager = new FileManager();
		}
		return manager;
	}

	private File defaultDirectory;

	public File getDefaultDirectory() {
		return defaultDirectory;
	}

	public void setDefaultDirectory(File defaultDirectory) {
		this.defaultDirectory = defaultDirectory;
	}

	public static String getApplePushCertificateFilePath() {
		File[] files = FileManager.sharedManager().getDefaultDirectory().
				listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name.equals("classes")) {
					return true;
				}
				return false;
			}
		});
		File classesDirectory = files[0];
		File certFile = classesDirectory.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name.equals("ChatSquarePushCert.p12")) {
					return true;
				}
				return false;
			}
		})[0];
		return certFile.getAbsolutePath();
	}

}
