package org.aiot.model.enums;

/**
 * American National Standards Institute
 * @author TAOJIN
 *
 */
public enum ANSI {
	REST(0);

	/** 前景色 */
	public enum COLOR_FORE{
		/** 黑色 */
		black("",30),
		/** 红色 */
		red("",31),
		/** 绿色 */
		green("",32),
		/** 黄色 */
		yellow("",33),
		/** 蓝色 */
		blue("",34),
		/** 紫色（品红） */
		purple("",35),
		/** 青色 */
		cyan("",36),
		/** 白色 */
		white("",37),
		/** 默认 */
		normal("",39);

		private int value;

		COLOR_FORE(String name,int value){
			this.value = value;
		}

		public int val(){
			return value;
		}

		public String format(String str){
			return this + str + ANSI.REST;
		}

		public String format(String str,int start,int end){
			return str.substring(0,start) + this + str.substring(start,end) + ANSI.REST + str.substring(end);
		}

		public String toString(){
			return "\033["+this.value+"m";
		}
	}

	/** 背景色 */
	public enum COLOR_BACK{
		/** 黑色 */
		black("黑色",40),
		/** 红色 */
		red("红色",41),
		/** 绿色 */
		green("绿色",42),
		/** 黄色 */
		yellow("黄色",43),
		/** 蓝色 */
		blue("蓝色",44),
		/** 紫色（品红） */
		purple("紫色（品红）",45),
		/** 青色 */
		cyan("青色",46),
		/** 白色 */
		white("白色",47),
		/** 默认 */
		normal("默认",49);

		private int value;

		COLOR_BACK(String name,int value){
			this.value = value;
		}

		public int val(){
			return value;
		}

		public String format(String str){
			return this + str + ANSI.REST;
		}

		public String format(String str,int start,int end){
			return str.substring(0,start) + this + str.substring(start,end) + ANSI.REST + str.substring(end);
		}

		public String toString(){
			return "\033["+this.value+"m";
		}
	}

	/** 样式 */
	public enum STYLE{
		/** 加粗 */
		bold("加粗",1),
		/** 淡色 */
		light("淡色",2),
		/** 斜体 */
		italic("斜体",3),
		/** 下划线 */
		underline("下划线",4),
		/** 闪烁 */
		flicker("闪烁",5),
		/** 反色 */
		invert("反色",7),
		/** 隐藏 */
		hidden("隐藏",8);

		private int value;

		STYLE(String name,int value){
			this.value = value;
		}

		public int val(){
			return value;
		}

		public String format(String name,String str){
			return this + str + ANSI.REST;
		}

		public String format(String str,int start,int end){
			return str.substring(0,start) + this + str.substring(start,end) + ANSI.REST + str.substring(end);
		}

		public String toString(){
			return "\033["+this.value+"m";
		}
	}

	private int value;
	ANSI(int value){
		this.value = value;
	}

	public String toString(){
		return "\033["+this.value+"m";
	}
    
    
}
