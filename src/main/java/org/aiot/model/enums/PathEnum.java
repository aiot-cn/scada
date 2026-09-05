package org.aiot.model.enums;

import org.aiot.main.Constants;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.io.File;

/**
 * 路径
 * @author taojin
 *
 */
public enum PathEnum {
	temp("临时"),
	document("文档"),
	image("图片"),
	audio("音频"),
	video("视频"),
	AppData("应用"),
	lib("库"),

	ram("ram")
	;

	private static String ramPath;
	private String text;
	PathEnum(String text){
		this.text = text;
	}
	
	
	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}

	public File addDir(String name){
		String path = p()+File.separator+name;
		return Files.createDirIfNoExists(path);
	}

	public String p(){
		return Constants.HOME_PATH + File.separator + this.name();
	} 
	
	public String p2(){
		return p() + File.separator;
	}

	public String ram(){
		return ramPath + File.separator + this.name();
	}

	public File dir(){
		return new File(p());
	}

	public File getFile(String name){
		return new File(p(),name);
	}

	public File getRam(String name){
		return new File(ram(),name);
	}

	@Override
	public String toString(){
		return Constants.HOME_PATH + File.separator + this.name() + File.separator ;
	}

	public static void initPath(){
		ramPath = PropEnum.RAM_PATH.val();
		if(Strings.isBlank(ramPath)){
			String dirName = Constants.HOME_PATH.substring(Constants.HOME_PATH.lastIndexOf(File.separator)+1);
			ramPath = Lang.isWin() ? Constants.HOME_PATH + File.separator + "ram" : "/dev/shm/"+dirName;
		}

		for(PathEnum e:values()){
			Files.createDirIfNoExists(e.ram());
			Files.createDirIfNoExists(e.p());
		}
	}

	
}
