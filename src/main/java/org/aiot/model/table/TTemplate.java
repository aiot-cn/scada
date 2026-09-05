package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;


@Table
@AoTbase
public class TTemplate extends TBase{

	private int type; //0 html,1 graph(2D)
	private int role;//权限 0游客 1登录 2站点
	private String path;
	private String title;//标题
	private String image;
    private String content;
	//数据源放内容中，避免依赖，使模板能够单文件运行
	//private String dataSource;


	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}