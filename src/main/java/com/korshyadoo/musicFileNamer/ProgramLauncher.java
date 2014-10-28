package com.korshyadoo.musicFileNamer;

import java.awt.EventQueue;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.korshyadoo.musicFileNamer.conf.Configuration;

/**
 * Hello world!
 *
 */
public class ProgramLauncher {
	
	static {
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		config = (Configuration) context.getBean("config");
		((ConfigurableApplicationContext)context).close();
	}
	
	public static final Configuration config;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}
