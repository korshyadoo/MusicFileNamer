package com.korshyadoo.musicFileNamer.conf;

import java.io.File;

public interface Configuration {
	public int getMainWindowX();
	public int getMainWindowY();
	public int getMainWindowWidth();
	public int getMainWindowHeight();
	public File getDefaultDirectory();
	public String getLoggingLevel();
}
