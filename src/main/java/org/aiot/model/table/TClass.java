package org.aiot.model.table;

public class TClass extends TBase{
	private Long pid;//上层ID
	private String plass;//上层class


	public void setTBase(TBase tBase){
		this.pid = tBase.getId();
		this.plass = tBase.getClass().getSimpleName();
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getPlass() {
		return plass;
	}

	public void setPlass(String plass) {
		this.plass = plass;
	}
}