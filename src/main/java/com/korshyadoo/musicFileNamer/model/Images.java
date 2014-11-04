package com.korshyadoo.musicFileNamer.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.korshyadoo.musicFileNamer.controller.ProgramLauncher;

public class Images {
	private final BufferedImage upArrow;
	private final BufferedImage downArrow;
	private static Images instance;
	
	private Images() throws IOException {
		upArrow = ImageIO.read(getInputStream("upArrow.png"));
		downArrow = ImageIO.read(getInputStream("downArrow.png"));
	}
	
	private InputStream getInputStream(String path) {
		InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
		return resourceAsStream;
	}

	public BufferedImage getUpArrow() {
		return upArrow;
	}

	public BufferedImage getDownArrow() {
		return downArrow;
	}
	
	public static Images getInstance() {
		if(instance == null) {
			try {
				instance = new Images();
			} catch (IOException e) {
				ProgramLauncher.getLogger().catching(e);
			}
		}
		return instance;
	}
}
