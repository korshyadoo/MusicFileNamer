package com.korshyadoo.musicFileNamer.conf;

import java.io.File;

public class DevConfiguration implements Configuration {
	public int getMainWindowX() {
		return 500;
	}

	public int getMainWindowY() {
		return 150;
	}
	
	public int getMainWindowWidth() {
		return 740;
	}

	public int getMainWindowHeight() {
		return 600;
	}

	@Override
	public File getDefaultDirectory() {
		File file = new File("C:\\Users\\jmatthews\\Google Drive\\MusicFileNamer\\testFolder");
		return file;
	}
	
	
}
