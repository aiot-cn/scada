package org.aiot.communication;

public class ReceiveData {
	private byte[] bytes;
	private String topic;

	public ReceiveData(byte[] bytes, String topic){
		this.bytes = bytes;
		this.topic = topic;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
