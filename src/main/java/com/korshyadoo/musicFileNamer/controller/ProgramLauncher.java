package com.korshyadoo.musicFileNamer.controller;

import java.awt.EventQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.korshyadoo.musicFileNamer.model.PropertiesModel;
import com.korshyadoo.musicFileNamer.view.MainFrame;

/**
 * Hello world!
 *
 */
public class ProgramLauncher {
//
//	static {
//		try {
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(null, "error: " + e.getMessage());
//		}
//	}

	private static PropertiesModel properties;
	private static final Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		getLogger().info("App started");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static Logger getLogger() {
		return logger;
	}

	public static PropertiesModel getProperties() {
		return properties;
	}
}
