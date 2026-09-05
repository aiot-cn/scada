package org.aiot.model.table;

import java.util.Optional;

public class TBaseSeq extends TBase{


    private Float sequence;//排序

	public Float getSequence() {
		return sequence;
	}
	public void setSequence(Float sequence) {
		this.sequence = sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence.floatValue();
	}

	@Override
	public int compareTo(Object o) {
		TBaseSeq t = (TBaseSeq) o;
		Float v1 = Optional.ofNullable(this.getSequence()).orElse(-1f);
		Float v2 = Optional.ofNullable(t.getSequence()).orElse(-1f);
		return v1.equals(v2) ? super.compareTo(o) : v1.compareTo(v2);
	}

}