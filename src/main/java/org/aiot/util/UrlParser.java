package org.aiot.util;

import org.nutz.lang.Strings;

/**
 * /protocol-a//b/c.jpg
 * url = protocol-a/b/c.jpg
 * pathName = a/b/c.jpg
 * name = c.jpg
 * suffix = jpg
 */
public class UrlParser {
	private String url; //全路径 保存时需要
	private String protocolName = "File";
	private String pathName;
	private String name;
	private String suffix;

	public UrlParser(String url){
		if(Strings.isBlank( url))
			return;
		String u = url.replaceAll("//+","/");
		if(u.indexOf("/") == 0)
			u = u.substring(1);
		// u = protocol-a/b/c.jpg
		this.url = this.pathName = u;
		String[] arr = u.split("/");
		int i1 = arr[0].indexOf("-");
		if(i1 > 0){
			this.protocolName = Strings.upperFirst(arr[0].substring(0,i1));
			this.pathName = u.substring(i1+1);
		}
		// url = a/b/c.jpg
		this.name = arr[arr.length-1];
		int pIndex = name.lastIndexOf(".");
		if(pIndex > 0)
			this.suffix = name.substring(pIndex+1).toLowerCase();
		//this.path = u.substring(0,pIndex);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}


}
