package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.CameraBrandEnum;
import org.aiot.util.StrUtil;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class TVideoSource extends TBaseSeq{

	private String name;
	private CameraBrandEnum cameraBrand;
	/**
	 * cameraBrand存在时 ip:port
	 * 否则 ip:port/path 或者 rtsp 全路径
	 */
	private String url;
	private String account;
	private String password;
	private String channel;
	private Integer streamType;//0主码流 1子码流 2第三码流

	private Long workId;
	private Float workInterval;//工作间隔，单位S
	private Integer pushResult;//推送结果 1推图 2推流


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CameraBrandEnum getCameraBrand() {
		return cameraBrand;
	}

	public void setCameraBrand(CameraBrandEnum cameraBrand) {
		this.cameraBrand = cameraBrand;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getStreamType() {
		return streamType;
	}

	public void setStreamType(Integer streamType) {
		this.streamType = streamType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Float getWorkInterval() {
		return workInterval;
	}

	public void setWorkInterval(Float workInterval) {
		this.workInterval = workInterval;
	}

	public Integer getPushResult() {
		return pushResult;
	}

	public void setPushResult(Integer pushResult) {
		this.pushResult = pushResult;
	}

	/**
	 * 获取实际的RTSP地址：url有值时直接返回，否则根据品牌模板和结构化字段拼接
	 */
	public String getRtspUrl() {
		if(cameraBrand == null)
			return this.url;

		String rtsp = "rtsp://"+account+":"+password+"@"+url;
		String template = cameraBrand.getTemplate();
		template = StrUtil.replace(template,"\\[\\S+\\]", v->{
			v = v.substring(1,v.length()-1);
			return v.split(",")[streamType];
		});
		return template.replace("{rtsp}",rtsp).replace("{ch}",channel);
	}
}
