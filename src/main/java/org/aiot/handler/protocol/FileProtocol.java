package org.aiot.handler.protocol;

import org.aiot.infc.SResProtocol;
import org.aiot.util.FileUtil;
import org.nutz.lang.Files;

import java.io.File;

public class FileProtocol implements SResProtocol {
	private File file;
	private String pathName;

	public FileProtocol(String pathName){
		this.pathName = pathName;
		this.file = FileUtil.toFile(pathName);
	}

	@Override
	public String getTitle() {
		return file.getName();
	}

	@Override
	public String getContent() {
		return Files.read(file);
	}

	@Override
	public byte[] getBytes(){
		return Files.readBytes(file);
	}

	@Override
	public String getParam() {
		return "{filePath:\""+pathName+"\"}";
	}

	@Override
	public void saveContent(String content){
		Files.write(file,content);
	}

}
