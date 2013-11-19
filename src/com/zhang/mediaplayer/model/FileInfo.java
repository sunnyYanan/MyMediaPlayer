package com.zhang.mediaplayer.model;

import java.util.ArrayList;

enum FileType {
	folder, file
}

public class FileInfo {
	private String fileName;
	private String filePath;
	private FileType fileType;
	static ArrayList<String> musicType= new ArrayList<String>();
	static{
		musicType.add(".mp3");
	}

	public FileInfo(String fileName, String filePath, boolean isDir) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileType = isDir ? FileType.folder : FileType.file;
	}

	public boolean isMusicFile() {
		int i = fileName.lastIndexOf(".");
		if (i < 0)
			return false;
		String fileSuffix = fileName.substring(i);
		if(musicType.contains(fileSuffix)) {
			return true;
		}
		return false;
	}
	public boolean isDir() {
		if(fileType == FileType.folder)
			return true;
		return false;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
