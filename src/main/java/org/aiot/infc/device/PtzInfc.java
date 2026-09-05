package org.aiot.infc.device;

import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.table.TPreset;

@AoReflect("云台")
public interface PtzInfc {

	@AoReflect("标定零位")
	void setZero();

	@AoReflect("到零位")
	void toZero();

	@AoReflect("到水平角")
	void toPan(float pan);

	@AoReflect("到俯仰角")
	void toTilt(float tilt);

	@AoReflect("到水平俯仰")
	void toPanTilt(float pan, float tilt);

	@AoReflect("到水平俯仰缩放")
	void toPTZ(Float pan, Float tilt, Float zoom, Integer focus);

	@AoReflect("设置预置位")
	void setPreset(int id);

	@AoReflect("到预置位")
	boolean toPreset(int id);

	@AoReflect("到系统预置位")
	boolean toPresetSys(long id);

	@AoReflect("上")
	void moveUp();

	@AoReflect("下")
	void moveDown();

	@AoReflect("左")
	void moveLeft();

	@AoReflect("右")
	void moveRight();

	@AoReflect("停止")
	void stop();

	@AoReflect(value = "获取PTZF")
	TPreset getPTZF(Integer channel);

	@AoReflect(value = "到PTZF")
	boolean toPTZF(Float pan, Float tilt, Float zoom, Integer focus, Integer channel);

	@AoReflect(value="设置聚焦模式")
	public boolean setFocusMode(
			@AoReflect(value = "通道",placeholder = "默认1")Integer channel,
			@AoReflect(select = "0:自动,1:手动,2:半自动")int model);

	@AoReflect("云台控制")
	void controlPTZ(String command,int stop,int speed,Integer channel);

}
