package com.korshyadoo.musicFileNamer.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.korshyadoo.musicFileNamer.model.Mode;
import com.korshyadoo.musicFileNamer.model.PrefixFormats;
import com.korshyadoo.musicFileNamer.view.MainFrame;

public class FileProcessor {
	final private List<String> fileList;
	private List<String> proposedList;
	private int startingNumber;
	private int numberOfDigits;
	private PrefixFormats pattern;
	private Mode mode;
	private final Logger logger = ProgramLauncher.getLogger();

	public FileProcessor(List<String> fileList, int startingNumber, PrefixFormats pattern, Mode mode) {
		this.fileList = fileList;
		this.startingNumber = startingNumber;
		this.pattern = pattern;
		this.mode = mode;
		numberOfDigits = findNumberOfDigitsIn(startingNumber);
		logger.debug("number of digits: " + numberOfDigits);
		processList();
	}

	private int findNumberOfDigitsIn(int source) {
		int count = 0;
		do {
			source /= 10;
			count++;
		} while (source > 0);

		return count;
	}

	/**
	 * This method examines the list of Files passed at object creation and makes a new list with the proposed name changes.
	 */
	private void processList() {
		proposedList = new ArrayList<>();

		int count = startingNumber;
		for (int index = 0; index < fileList.size(); index++) {
			String fileNameBase;
			String extension;
			if(mode == Mode.RENAME_FILES) {
				List<String> splitName = splitNameAndExtension(index);
				fileNameBase = splitName.get(0);
				extension = splitName.get(1);
			} else {
				fileNameBase = fileList.get(index);
				extension = "";
			}
			int indexOfName = findIndexOfName(fileNameBase);
			String prefix = convertToTwoDigit(count) + pattern.toString();
			String suffix = fileNameBase.substring(indexOfName);
			String newName = prefix + suffix + extension;
			proposedList.add(newName);
			count++;
		}
	}

	private int indexOfFirstAlphaIn(String str) {
		String[] strSplit = str.split("\\p{Alpha}", 2);
		int indexOfFirstAlpha = strSplit[0].length();
		if (indexOfFirstAlpha >= str.length()) {
			indexOfFirstAlpha = -1;
		}
		return indexOfFirstAlpha;
	}

	private int indexOfFirstWhiteSpaceCharacterIn(String str) {
		String[] strSplit = str.split("\\s", 2);
		int indexOfFirstWhitespace = strSplit[0].length();
		if (indexOfFirstWhitespace >= str.length()) {
			indexOfFirstWhitespace = -1;
		}
		return indexOfFirstWhitespace;
	}

	private int indexOfFirstAlphaNumericIn(String str) {
		String[] strSplit = str.split("\\w", 2);
		int indexOfNextAlphaNumeric = strSplit[0].length();
		if (indexOfNextAlphaNumeric >= str.length()) {
			indexOfNextAlphaNumeric = -1;
		}
		return indexOfNextAlphaNumeric;
	}

	/**
	 * Finds the index within the fileName String where the name of the file begins. For example: if the filename is "01 Born To Be Wild", the index of the name
	 * is 3
	 * 
	 * @param fileName
	 *            The passed fileName
	 * @return The index of the beginning of the name
	 */
	private int findIndexOfName(String fileName) {
		// Find the index of the first '-'
		int indexOfFirstHyphen = fileName.indexOf('-');

		// Find the index of the first alpha character
		int indexOfFirstAlpha = indexOfFirstAlphaIn(fileName);

		// Find the index of the first whitespace character
		int indexOfFirstWhitespace = indexOfFirstWhiteSpaceCharacterIn(fileName);

		int nameIndex = -1;

		// 1. Find a hyphen, if it has no words before it, then the first non-whitespace char after the hyphen starts the name
		if (indexOfFirstHyphen != -1 && indexOfFirstHyphen < indexOfFirstAlpha) {
			// nameIndex is the index of the first alpha after the hyphen
			String buffer = fileName.substring(indexOfFirstHyphen + 1);
			int indexOfFirstAlphaNumbericInBuffer = indexOfFirstAlphaNumericIn(buffer);
			if (indexOfFirstAlphaNumbericInBuffer != -1) {
				nameIndex = (indexOfFirstHyphen + 1) + indexOfFirstAlphaNumericIn(buffer);
			} else {
				nameIndex = indexOfFirstHyphen + 1;
			}
		} else {
			// There are words before the hyphen
			// 2. Find a space, if there are no words before the space, the first non-whitespace char after the space starts the name
			if (indexOfFirstWhitespace != -1 && indexOfFirstWhitespace < indexOfFirstAlpha) {
				String buffer = fileName.substring(indexOfFirstWhitespace + 1);
				int indexOfFirstAlphaNumbericInBuffer = indexOfFirstAlphaNumericIn(buffer);
				if (indexOfFirstAlphaNumbericInBuffer != -1) {
					nameIndex = (indexOfFirstWhitespace + 1) + indexOfFirstAlphaNumericIn(buffer);
				} else {
					nameIndex = indexOfFirstWhitespace + 1;
				}
			} else {
				nameIndex = 0;
			}
		}

		return nameIndex;
	}

	/**
	 * Converts the passed int into a String. If the number is between 0 and 9, inclusive, a "0" is concatenated to the beginning.
	 * 
	 * @param num
	 *            The int to convert
	 * @return The passed int with a prefixed "0", if the passed int is a one-digit integer. Otherwise, the int is converted to a String and returned unaltered.
	 */
	private String convertToTwoDigit(int num) {
		String result = Integer.toString(num);
		if (num < 10 && num >= 0) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * Gets the name of the file at the passed index, removing the extension
	 * 
	 * @param index
	 *            The index in the fileList to retrieve the file name from
	 * @return The name of the file at the passed index, without the extension
	 */
	private List<String> splitNameAndExtension(int index) {
		String fileName = fileList.get(index);
		int extensionIndex = fileName.lastIndexOf('.');
		String extension = fileName.substring(extensionIndex);
		if (extensionIndex != -1) {
			fileName = fileName.substring(0, extensionIndex);
		}
		List<String> results = new ArrayList<>();
		results.add(fileName);
		results.add(extension);
		return results;
	}

	public List<String> getProposedList() {
		List<String> results = new ArrayList<>(proposedList);
		return results;
	}

	/**
	 * This method accesses the file system to make the proposed changes to the list of files
	 * 
	 * @return {@code true} if successful; otherwise {@code false}
	 */
	public void upateFiles() {
		logger.debug("Updating files");
		for (int index = 0; index < fileList.size(); index++) {
			File oldFile = new File(MainFrame.getSelectedDirectory().toString() + "\\" + fileList.get(index));
			File newFile = new File(MainFrame.getSelectedDirectory().toString() + "\\" + proposedList.get(index));
			if (newFile.exists()) {
				// Can't rename the file because the new filename already exists
				logger.error("Can't rename the file because the new filename already exists");
			} else {
				boolean success = oldFile.renameTo(newFile);
				if (!success) {
					boolean canWrite = oldFile.canWrite();
					logger.error("Renamed failed: canWrite() = " + canWrite);
				}
			}
		}
	}

}
