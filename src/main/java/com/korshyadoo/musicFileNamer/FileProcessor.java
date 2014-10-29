package com.korshyadoo.musicFileNamer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
	final private List<String> fileList;
	private List<String> proposedList;
	private State state = State.UNINITIALIZED;
	private int startingNumber;
	private int numberOfDigits;
	
	public FileProcessor(List<String> fileList, int startingNumber) {
		this.fileList = fileList;
		this.startingNumber = startingNumber;
		numberOfDigits = findNumberOfDigitsIn(startingNumber);
		System.out.println("number of digits: " + numberOfDigits);
		processList();
	}
	
	private int findNumberOfDigitsIn(int source) {
		int count = 0;
		do {
			source /= 10;
			count++;
		} while(source > 0);
		
		return count;
	}
	
	/**
	 * This method examines the list of Files passed at object creation and makes a new list with the proposed name changes.
	 */
	private void processList() {
		if(state == State.UNINITIALIZED) {
			proposedList = new ArrayList<>();
			
			for(int index = 0; index < fileList.size(); index++) {
				String fileNameBase = getFileNameBase(index);
				int indexOfName = findIndexOfName(fileNameBase);
				String prefix = convertToTwoDigit(index + 1) + "-";
				String suffix = fileNameBase.substring(indexOfName);
				String newName = prefix + suffix;
				proposedList.add(newName);
			}
			
		}
		if(fileList.size() == proposedList.size()) {
			state = State.READY;
		} else {
			state = State.ERROR1;
		}
	}
	
	private int indexOfFirstAlphaIn(String str) {
		String[] strSplit = str.split("\\p{Alpha}", 2);
		int indexOfFirstAlpha = strSplit[0].length();
		if(indexOfFirstAlpha >= str.length()) {
			indexOfFirstAlpha = -1;
		}
		return indexOfFirstAlpha;
	}
	
	private int indexOfFirstWhiteSpaceCharacterIn(String str) {
		String [] strSplit = str.split("\\s", 2);
		int indexOfFirstWhitespace = strSplit[0].length();
		if(indexOfFirstWhitespace >= str.length()) {
			indexOfFirstWhitespace = -1;
		}
		return indexOfFirstWhitespace;
	}
	
	private int indexOfFirstAlphaNumericIn(String str) {
		String[] strSplit = str.split("\\w", 2);
		int indexOfNextAlphaNumeric = strSplit[0].length();
		if(indexOfNextAlphaNumeric >= str.length()) {
			indexOfNextAlphaNumeric = -1;
		}
		return indexOfNextAlphaNumeric;
	}
	
	private int findIndexOfName(String fileName) {
		//Find the index of the first '-'
		int indexOfFirstHyphen = fileName.indexOf('-');
		
		//Find the index of the first alpha character
		int indexOfFirstAlpha = indexOfFirstAlphaIn(fileName);
		
		//Find the index of the first whitespace character
		int indexOfFirstWhitespace = indexOfFirstWhiteSpaceCharacterIn(fileName);
		
		int nameIndex = -1;
		
		//1. Find a hyphen, if it has no words before it, then the first non-whitespace char after the hyphen starts the name
		if(indexOfFirstHyphen != -1 && indexOfFirstHyphen < indexOfFirstAlpha) {
			//nameIndex is the index of the first alpha after the hyphen
			String buffer = fileName.substring(indexOfFirstHyphen + 1);
			int indexOfFirstAlphaNumbericInBuffer = indexOfFirstAlphaNumericIn(buffer);
			if(indexOfFirstAlphaNumbericInBuffer != -1) {
				nameIndex = (indexOfFirstHyphen + 1) + indexOfFirstAlphaNumericIn(buffer);
			} else {
				nameIndex = indexOfFirstHyphen + 1;
			}
		} else {
			//There are words before the hyphen
			//2. Find a space, if there are no words before the space, the first non-whitespace char after the space starts the name
			if(indexOfFirstWhitespace != -1 && indexOfFirstWhitespace < indexOfFirstAlpha) {
				String buffer = fileName.substring(indexOfFirstWhitespace + 1);
				int indexOfFirstAlphaNumbericInBuffer = indexOfFirstAlphaNumericIn(buffer);
				if(indexOfFirstAlphaNumbericInBuffer != -1) {
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
	
	private String convertToTwoDigit(int num) {
		String result = "0";
		if(num < 10 && num >= 0) {
			result = result + num; 
		} else {
			result = Integer.toString(num);
		}
		return result;
	}
	
	
	/**
	 * Gets the name of the file at the passed index, removing the extension
	 * @param index The index in the fileList to retrieve the file name from
	 * @return The name of the file at the passed index, without the extension
	 */
	private String getFileNameBase(int index) {
		String fileName = fileList.get(index);
		int extensionIndex = fileName.lastIndexOf('.');
		if(extensionIndex != -1) {
			fileName = fileName.substring(0, extensionIndex);
		}
		return fileName;
	}
	
	public List<String> getProposedList() throws IllegalStateException {
		if(state == State.READY) {
			List<String> results = new ArrayList<>(proposedList);
			return results;
		} else {
			throw new IllegalStateException("Cannot retrieve the proposed list when object is not in the ready state. Current object state is " + state.toString());
		}
	}
	
	/**
	 * This method accesses the file system to make the proposed changes to the list of files
	 * @return {@code true} if successful; otherwise {@code false}
	 */
	public void upateFiles() throws IllegalStateException {
		//fileList[0] is renamed to proposedList[0]
		//fileList[1] is renamed to proposedList[1]
		//etc.
		if(state == State.READY) {
			System.out.println("Updating files");
			for(int index = 0; index < fileList.size(); index++) {
				File oldFile = new File(fileList.get(index));
				File newFile = new File(proposedList.get(index));
				if(newFile.exists()) {
					//Can't rename the file because the new filename already exists
					System.out.println("Can't rename the file because the new filename already exists");
				} else {
					oldFile.renameTo(newFile);
				}
			}
		} else {
			throw new IllegalStateException("Cannot update file system when object is not in the ready state. Current object state is " + state.toString());
		}
	}
	
	private enum State {
		UNINITIALIZED {
			@Override
			public String toString() {
				return "UNINITIALIZED";
			}
		},
		READY {
			@Override
			public String toString() {
				return "READY";
			}
		},
		DEAD {
			@Override
			public String toString() {
				return "DEAD";
			}
		},
		ERROR1 {
			@Override
			public String toString() {
				return "ERROR: Proposed file list and original file list sizes do not match";
			}
		};
	}
	
}
