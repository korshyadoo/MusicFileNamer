package com.korshyadoo.musicFileNamer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import com.korshyadoo.musicFileNamer.conf.Configuration;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 4884885745124721080L;
	private JPanel contentPane;
	private File selectedDirectory = null;
	private JTextField txtDirectory;
	private BufferedImage upArrow;
	private BufferedImage downArrow;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> lstFileList;

	public MainFrame() throws IOException {
		upArrow = ImageIO.read(new File("src\\main\\resources\\upArrow.png"));
		downArrow = ImageIO.read(new File("src\\main\\resources\\downArrow.png"));

		createAndShowGui();
	}

	private void createAndShowGui() {
		Configuration config = ProgramLauncher.config;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(config.getMainWindowX(), config.getMainWindowY(), config.getMainWindowWidth(), config.getMainWindowHeight());
		// setBounds(100, 200, 400, 400);
		contentPane = new JPanel();
		// contentPane.setBorder(BorderFactory.createLineBorder(Color.black));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		createBrowsePanel();
		createListPanel();
		createButtonPanel();
	}

	private void createBrowsePanel() {
		JPanel browsePanel = new JPanel();
		// browsePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		browsePanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(browsePanel, BorderLayout.NORTH);

		txtDirectory = new JTextField("");
		txtDirectory.setEditable(false);
		browsePanel.add(txtDirectory, BorderLayout.CENTER);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Open a file browser to choose a location
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(MainFrame.this, "Select");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					MainFrame.this.selectedDirectory = fc.getSelectedFile();
					changeSelectedDirectory();
				}
			}
		});
		browsePanel.add(btnBrowse, BorderLayout.EAST);
	}

	private void createListPanel() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(listPanel, BorderLayout.CENTER);
		lstFileList = new JList<>(listModel);
		listPanel.add(lstFileList, BorderLayout.CENTER);
	}

	private void createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		// buttonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnUpArrow = createUpArrowButton();
		buttonPanel.add(btnUpArrow);

		JButton btnDownArrow = createDownArrowButton();
		buttonPanel.add(btnDownArrow);
		
		JButton btnSave = new JButton("Save");
		int btnSavetWidth = (int)(btnSave.getPreferredSize().getWidth());
		int btnSaveHeight = (int)(btnUpArrow.getPreferredSize().getHeight());
		Dimension saveDimension = new Dimension(btnSavetWidth, btnSaveHeight);
		btnSave.setPreferredSize(saveDimension);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				
				
			}
		});
		buttonPanel.add(btnSave);
		
		JButton btnRestart = new JButton("Restart");
		int btnRestarttWidth = (int)(btnRestart.getPreferredSize().getWidth());
		int btnRestartHeight = (int)(btnUpArrow.getPreferredSize().getHeight());
		Dimension restartDimension = new Dimension(btnRestarttWidth, btnRestartHeight);
		btnRestart.setPreferredSize(restartDimension);
		btnRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure you want to restart?", "Restart?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION) {
					//Yes was selected from the confirm dialog. Reset the JList contents
					loadListPanelContents();
				}
			}
		});
		buttonPanel.add(btnRestart);
	}
	
	
	private JButton createUpArrowButton() {
		JButton btnUpArrow = new JButton() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(MainFrame.this.upArrow, 0, 0, null);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(MainFrame.this.upArrow.getWidth(), MainFrame.this.upArrow.getHeight());
			}

		};
		
		btnUpArrow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentIndex = lstFileList.getSelectedIndex();
				if(currentIndex > 0) {
					String valueAtCurrentIndex = lstFileList.getSelectedValue();
					lstFileList.setSelectedIndex(currentIndex - 1);
					String valueAtNewIndex = lstFileList.getSelectedValue();
					listModel.setElementAt(valueAtNewIndex, currentIndex);
					listModel.setElementAt(valueAtCurrentIndex, currentIndex - 1);
				}
			}
		});
		
		return btnUpArrow;
	}
	
	private JButton createDownArrowButton() {
		JButton btnDownArrow = new JButton() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(MainFrame.this.downArrow, 0, 0, null);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(MainFrame.this.downArrow.getWidth(), MainFrame.this.downArrow.getHeight());
			}
		};
		
		btnDownArrow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentIndex = lstFileList.getSelectedIndex();
				if(currentIndex < (listModel.getSize() - 1)) {
					final String valueAtCurrentIndex = lstFileList.getSelectedValue();
					lstFileList.setSelectedIndex(currentIndex + 1);
					final String valueAtNewIndex = lstFileList.getSelectedValue();
					listModel.setElementAt(valueAtNewIndex, currentIndex);
					listModel.setElementAt(valueAtCurrentIndex, currentIndex + 1);
				}
			}
		});
		
		return btnDownArrow;
	}

	private void updateBrowseSelection() {
		if (selectedDirectory != null) {
			txtDirectory.setText(selectedDirectory.toString());
		} else {
			txtDirectory.setText("");
		}
	}

	private void loadListPanelContents() {
		// Load the list of files from selectedDirectory into the listPanel
		listModel = new DefaultListModel<>();
		File[] listOfFiles = selectedDirectory.listFiles();
		for(File file : listOfFiles) {
			if(!file.isDirectory()) {
				listModel.addElement(file.getName());
			}
		}
		lstFileList.setModel(listModel);
	}

	private void changeSelectedDirectory() {
		updateBrowseSelection();
		loadListPanelContents();
	}

}
