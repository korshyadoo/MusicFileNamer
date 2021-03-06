package com.korshyadoo.musicFileNamer.view;

import com.korshyadoo.musicFileNamer.conf.Configuration;
import com.korshyadoo.musicFileNamer.controller.FileProcessor;
import com.korshyadoo.musicFileNamer.controller.LookAndFeelController;
import com.korshyadoo.musicFileNamer.controller.ProgramLauncher;
import com.korshyadoo.musicFileNamer.model.Mode;
import com.korshyadoo.musicFileNamer.model.PrefixFormats;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 4884885745124721080L;

	private Mode mode;
	private JPanel contentPane;
	private static File selectedDirectory = null;
	private JTextField txtDirectory;
	private BufferedImage upArrow;
	private BufferedImage downArrow;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> lstFileList;
	private JButton btnSave;
	private JButton btnRestart;
	private JTextField txtNumberInput;
	private JRadioButton pattern1;
	private JRadioButton pattern2;
	private JRadioButton pattern3;
	private JRadioButton pattern4;
	private JScrollPane scrollPane;
	private JLabel lblClickBrowse;
	private JPanel listPanel;
	private boolean listVisible;
	private final Logger logger  = ProgramLauncher.getLogger();

	public MainFrame() throws IOException {
		upArrow = ImageIO.read(getInputStream("upArrow.png"));
		downArrow = ImageIO.read(getInputStream("downArrow.png"));

		createAndShowGui();
	}

	private InputStream getInputStream(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	private void createAndShowGui() {
		Configuration config = ProgramLauncher.config;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(config.getMainWindowX(), config.getMainWindowY(), config.getMainWindowWidth(), config.getMainWindowHeight());
		// setBounds(100, 200, 400, 400);
		contentPane = new JPanel();
		// contentPane.setBorder(BorderFactory.createLineBorder(Color.black));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		createBrowsePanel();
		createListPanel();
		createBottomPanel();
	}

	private void createBrowsePanel() {
		JPanel browsePanel = new JPanel();
		browsePanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(browsePanel, BorderLayout.NORTH);

		txtDirectory = new JTextField("");
		txtDirectory.setEditable(false);
		browsePanel.add(txtDirectory, BorderLayout.CENTER);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(actionEvent -> {
			// Open a file browser to choose a location
			final LookAndFeel previousLF = UIManager.getLookAndFeel();
			LookAndFeelController.setLookAndFeel(LookAndFeelController.NIMBUS);
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(ProgramLauncher.config.getDefaultDirectory());
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = fc.showDialog(MainFrame.this, "Select");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				logger.debug("FileChooser selected: " + fc.getSelectedFile().toString());

				//If the selected File is a file, use the parent directory instead
				File selectedFile = (fc.getSelectedFile().isDirectory()) ? fc.getSelectedFile() : new File(fc.getSelectedFile().getParent());

				MainFrame.setSelectedDirectory(selectedFile);
				logger.debug("selectedFile converted to: " + selectedFile.toString());

				boolean noFiles = hasOnlyDirectories(selectedDirectory);
				if(noFiles) {
					mode = Mode.RENAME_DIRECTORIES;
				} else {
					mode = Mode.RENAME_FILES;
				}
				updateDirectoryTextField();
				loadListPanelContents();
				MainFrame.this.btnSave.setEnabled(true);
				MainFrame.this.btnRestart.setEnabled(true);
			}
			LookAndFeelController.setLookAndFeel(previousLF);
		});
		browsePanel.add(btnBrowse, BorderLayout.EAST);
	}

	private boolean hasOnlyDirectories(File file) {
		boolean result = true;
		File[] listFiles = file.listFiles();
		for(File f : listFiles) {
			if(!f.isDirectory()) {
				result = false;
				break;
			}
		}
		return result;
	}

	private void createListPanel() {
		listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(listPanel, BorderLayout.CENTER);
		lstFileList = new JList<>(listModel);
		listModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				if(listModel.size() == 0) {
					logger.debug("removing scrollpane");
					listPanel.remove(scrollPane);
					listVisible = false;
					listPanel.add(lblClickBrowse, BorderLayout.CENTER);
					listPanel.validate();
				}
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				if(!listVisible) {
					listPanel.remove(lblClickBrowse);
					logger.debug("adding scrollpane");
					listPanel.add(scrollPane, BorderLayout.CENTER);
					listVisible = true;
					listPanel.validate();
				}
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
			}
		});
		scrollPane = new JScrollPane(lstFileList);
		lblClickBrowse = new JLabel("Click \"Browse\" to choose a folder to view", SwingConstants.CENTER);
		lblClickBrowse.setForeground(Color.RED);

		listPanel.add(lblClickBrowse, BorderLayout.CENTER);
	}

	private void createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		Border bottomPanelComponenetBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);

		JPanel buttonPanel = createButtonPanel();
		buttonPanel.setBorder(bottomPanelComponenetBorder);
		bottomPanel.add(buttonPanel);

		JPanel inputPanel = createInputPanel();
		inputPanel.setBorder(bottomPanelComponenetBorder);
		bottomPanel.add(inputPanel);
	}

	private JPanel createInputPanel() {
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));

		JPanel textPanel = createTextPanel();
		Border border = BorderFactory.createEmptyBorder(5, 0, 10, 0);
		textPanel.setBorder(border);
		inputPanel.add(textPanel);

		JPanel radioPanel = createRadioPanel();
		inputPanel.add(radioPanel);

		return inputPanel;
	}

	private JPanel createTextPanel() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.PAGE_AXIS));

		JLabel label = new JLabel("Enter the starting number:");
		result.add(label);

		txtNumberInput = new JTextField("1");
		Dimension d = new Dimension((int) (label.getPreferredSize().getWidth()), (int) (txtNumberInput.getPreferredSize().getHeight()));
		txtNumberInput.setMaximumSize(d);
		result.add(txtNumberInput);

		return result;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton btnUpArrow = createUpArrowButton();
		buttonPanel.add(btnUpArrow);

		JButton btnDownArrow = createDownArrowButton();
		buttonPanel.add(btnDownArrow);

		btnRestart = new JButton("Restart");
		int restartWidth = (int) (btnRestart.getPreferredSize().getWidth());
		int restartHeight = (int) (btnUpArrow.getPreferredSize().getHeight());
		Dimension restartDimension = new Dimension(restartWidth, restartHeight);
		btnRestart.setPreferredSize(restartDimension);
		btnRestart.addActionListener(actionEvent -> {
			int result = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure you want to restart?", "Restart?", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION) {
				// Yes was selected from the confirm dialog. Reset the JList contents
				loadListPanelContents();
			}
		});
		btnRestart.setEnabled(false);

		btnSave = new JButton("Save");
		int saveWidth = (int) (btnRestart.getPreferredSize().getWidth());
		int saveHeight = (int) (btnUpArrow.getPreferredSize().getHeight());
		Dimension saveDimension = new Dimension(saveWidth, saveHeight);
		btnSave.setPreferredSize(saveDimension);
		btnSave.addActionListener(new SaveButtonActionListener());
		btnSave.setEnabled(false);
		buttonPanel.add(btnSave);

		buttonPanel.add(btnRestart);

		return buttonPanel;
	}

	private JPanel createRadioPanel() {
		JLabel patternLabel = new JLabel("Choose a file prefix pattern:");
		pattern1 = new JRadioButton(PrefixFormats.PATTERN1.getMessage());
		pattern1.setSelected(true);
		pattern2 = new JRadioButton(PrefixFormats.PATTERN2.getMessage());
		pattern3 = new JRadioButton(PrefixFormats.PATTERN3.getMessage());
		pattern4 = new JRadioButton(PrefixFormats.PATTERN4.getMessage());

		// Add the buttons to a group
		ButtonGroup group = new ButtonGroup();
		group.add(pattern1);
		group.add(pattern2);
		group.add(pattern3);
		group.add(pattern4);

		// Create the panel and add the components to it
		JPanel patternChooserPanel = new JPanel();
		patternChooserPanel.setLayout(new BoxLayout(patternChooserPanel, BoxLayout.PAGE_AXIS));
		patternChooserPanel.add(patternLabel);
		patternChooserPanel.add(pattern1);
		patternChooserPanel.add(pattern2);
		patternChooserPanel.add(pattern3);
		patternChooserPanel.add(pattern4);

		return patternChooserPanel;
	}

	private String generateMessageText(List<String> list) {
		String li = "<li>";
		String endLi = "</li>";
		StringBuilder result = new StringBuilder("<html>The new file names will be:<br><ol style=\"list-style-type: disc\">");
		for (String s : list) {
			result.append(li).append(s).append(endLi);
		}
		result.append("</html>");
		return result.toString();
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

		btnUpArrow.addActionListener(actionEvent -> {
			int currentIndex = lstFileList.getSelectedIndex();
			if(currentIndex > 0) {
				String valueAtCurrentIndex = lstFileList.getSelectedValue();
				lstFileList.setSelectedIndex(currentIndex - 1);
				String valueAtNewIndex = lstFileList.getSelectedValue();
				listModel.setElementAt(valueAtNewIndex, currentIndex);
				listModel.setElementAt(valueAtCurrentIndex, currentIndex - 1);
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

		btnDownArrow.addActionListener(actionEvent -> {
			int currentIndex = lstFileList.getSelectedIndex();
			if(currentIndex < (listModel.getSize() - 1)) {
				final String valueAtCurrentIndex = lstFileList.getSelectedValue();
				lstFileList.setSelectedIndex(currentIndex + 1);
				final String valueAtNewIndex = lstFileList.getSelectedValue();
				listModel.setElementAt(valueAtNewIndex, currentIndex);
				listModel.setElementAt(valueAtCurrentIndex, currentIndex + 1);
			}
		});

		return btnDownArrow;
	}

	private void updateDirectoryTextField() {
		if(selectedDirectory != null) {
			txtDirectory.setText(selectedDirectory.toString());
		} else {
			txtDirectory.setText("");
		}
	}

	private void loadListPanelContents() {
		// Load the list of files from selectedDirectory into the listPanel
		listModel.clear();
		File[] listOfFiles = selectedDirectory.listFiles();
		Arrays.sort(listOfFiles, Comparator.comparing(File::getName));
		switch(mode) {
		case RENAME_FILES:
			for(File file : listOfFiles) {
				if(!file.isDirectory() && !file.isHidden()) {
					listModel.addElement(file.getName());
				}
			}
			break;
		case RENAME_DIRECTORIES:
			for(File file : listOfFiles) {
				if(file.isDirectory() && !file.isHidden()) {
					listModel.addElement(file.getName());
				}
			}
			break;
		}
		lstFileList.setModel(listModel);
	}

	private class SaveButtonActionListener implements ActionListener {
		private String startingNumberInput;
		private FileProcessor fileProcessor;

		@Override
		public void actionPerformed(ActionEvent e) {
			// Validate the input
			startingNumberInput = MainFrame.this.txtNumberInput.getText();
			if(isValidNumberInput()) {
				logger.debug("Processing Number: " + startingNumberInput);

				// Display confirmation window
				if(isConfirmed()) {
					// Process the file name changes and refresh the front-end list
					fileProcessor.upateFiles();
					loadListPanelContents();
				}
			}
		}

		private boolean isValidNumberInput() {
			try {
				int startingNumber = Integer.parseInt(startingNumberInput);
				return startingNumber >= 0 && startingNumber <= 999;
			} catch(NumberFormatException ex) {
				return false;
			}
		}

		private boolean isConfirmed() {
			// Get the list of files from the front-end
			List<String> frontEndList = new ArrayList<>();
			for(int index = 0; index < listModel.getSize(); index++) {
				frontEndList.add(listModel.get(index));
			}

			// Retrieve the list of proposed file names
			fileProcessor = new FileProcessor(frontEndList, Integer.parseInt(startingNumberInput), MainFrame.this.getSelectedPattern(), mode);
			List<String> proposedList = fileProcessor.getProposedList();

			// Create a confirm JOptionPane with proposed list
			Object[] options = { "Continue", "Cancel" };
			int result = JOptionPane.showOptionDialog(MainFrame.this, generateMessageText(proposedList), "Confirm", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

			return result == JOptionPane.YES_OPTION;
		}

	}

	private PrefixFormats getSelectedPattern() {
		if(pattern1.isSelected()) {
			return PrefixFormats.PATTERN1;
		} else if(pattern2.isSelected()) {
			return PrefixFormats.PATTERN2;
		} else if(pattern3.isSelected()) {
			return PrefixFormats.PATTERN3;
		} else if(pattern4.isSelected()) {
			return PrefixFormats.PATTERN4;
		} else {
			return null;
		}
	}

	public static File getSelectedDirectory() {
		return selectedDirectory;
	}

	public static void setSelectedDirectory(File selectedDirectory) {
		MainFrame.selectedDirectory = selectedDirectory;
	}

}
