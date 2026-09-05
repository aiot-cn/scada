package org.aiot.infc.device;

import java.util.List;

public class BaseExtend {
	public interface RMenu{
		//图片预览 右键 发送到 选项 钉钉:1
		List<RMenuOption> menuList();
		Object menuClick(String menu,String val);

		class RMenuOption{
			String name;//菜单名称 发送到/a
			String menu;//code
			String suffix; //img 图片,DEV 设备
			Long devId;

			public RMenuOption(String name,String menu,Long devId) {
				this.name = name;
				this.menu = menu;
				this.devId = devId;
			}

			public RMenuOption(String name,String menu,Long devId,String suffix) {
				this(name,menu,devId);
				this.suffix = suffix;
			}
		}
	}
}
