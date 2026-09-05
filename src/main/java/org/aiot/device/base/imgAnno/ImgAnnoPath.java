package org.aiot.device.base.imgAnno;

import java.io.File;

public enum ImgAnnoPath {
	/**
	 * 分类模型 7:2:1
	 * 目标检测 万 6:2:2 百万 8:1:1
	 */

	train("训练"),//训练集（Training Set）

	/**
	 * 用于在训练过程中调整模型的超参数（如学习率、批大小、迭代次数等）和选择最佳的模型架构。
	 * 监控模型在训练过程中的过拟合情况，帮助决定何时停止训练。
	 * 训练过程中频繁使用
	 * 验证集通常比测试集小一些
	 */
	val("验证"), //验证集（Validation Set）

	/**
	 * 测试集（Test Set）
	 * 训练完成后使用一次，用于最终评估模型的性能。测试集不应该被用于任何形式的模型训练或调整，以保持其独立性。
	 */
	test("测试"),

	classify("分类"),

	thumbnail("缩略图"),
	generate("生成"),
	zip("压缩包")
	;

	private String name;
	ImgAnnoPath(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public File mkDir(String path){
		File f =  new File(path + "/" +this.name());
		if(!f.isDirectory())
			f.mkdirs();
		return f;
	}

	public String path(String home){
		return home + "/" +this.name();
	}

	public File file(String home){
		return new File(path(home));
	}

	public File file(String home,String name){
		return new File(path(home),name);
	}
	
}
