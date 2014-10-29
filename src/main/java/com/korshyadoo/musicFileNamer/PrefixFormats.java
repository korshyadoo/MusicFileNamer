package com.korshyadoo.musicFileNamer;

public enum PrefixFormats {
	PATTERN1 {
		
		@Override
		public String toString() {
			return " ";
		}
		
		public String getMessage() {
			return "## fileName.ext (number, space, file name)";
		}
	},
	PATTERN2 {
		@Override
		public String toString() {
			return "-";
		}
		
		public String getMessage() {
			return "##-fileName.ext (number, hyphen, file name)";
		}
	},
	PATTERN3 {
		@Override
		public String toString() {
			return " - ";
		}
		
		public String getMessage() {
			return "## - fileName.ext (number, space, hyphen, space, file name)";
		}
	};
	
	public abstract String getMessage();
}