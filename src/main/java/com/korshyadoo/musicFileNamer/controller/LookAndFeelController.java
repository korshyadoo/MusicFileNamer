package com.korshyadoo.musicFileNamer.controller;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class LookAndFeelController {

	public static final String NIMBUS = "Nimbus";
	public static final String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";

	private LookAndFeelController() {

	}

	public static void setLookAndFeel(String lookAndFeel) {
		try {
			boolean success = false;
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if(info.getName().equals(lookAndFeel)) {
					UIManager.setLookAndFeel(info.getClassName());
					success = true;
					break;
				}
			}
			if(!success) {
				ProgramLauncher.getLogger().error("Unable to change look and feel. lookAndFeel = " + lookAndFeel);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLookAndFeel(LookAndFeel laf) {
		try {
			UIManager.setLookAndFeel(laf);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
