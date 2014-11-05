package com.korshyadoo.musicFileNamer.controller;

import java.awt.EventQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.korshyadoo.musicFileNamer.conf.Configuration;
import com.korshyadoo.musicFileNamer.view.MainFrame;

/**
 * Hello world!
 *
 */
public class ProgramLauncher {

	static {
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		config = (Configuration) context.getBean("config");
		((ConfigurableApplicationContext) context).close();
	}

	public static final Configuration config;
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

}
