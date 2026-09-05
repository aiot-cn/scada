package org.aiot.model.enums;

import org.aiot.main.Constants;
import org.nutz.lang.Strings;

import javax.script.ScriptException;

/**
 * 比较
 * @author TAOJIN
 *
 */
public enum CompareEnum {

	gte(">="),
	lte("<="),
	gt(">"),
	lt("<"),
	eq("=="),
	neq("!="),
	in("in"),
	notIn("not in"),
	is("is"),
	isNot("is not")
	;
	
	private String name;
	
	private CompareEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static CompareEnum get(String name){
		for(CompareEnum tEnum : values()){
			if(tEnum.name().equals(name)){
				return tEnum ;
			}
		}
		return null;
	}
	
	/**
	 * @param ov 值
	 * @param cv 阈值
	 * @param hv 回差
	 * @return -2异常 -1低于回差 0正常 1 超过阀值 2超过回差
	 */
	public int eval(Object ov,Float cv,Float hv) {
		String op = this.name;//比较符
		try {
			if(ov == null || Strings.isBlank(ov.toString())){
					return op.contains("!") ? 1 : -1;
			}
			String v = ov.toString();
			if(hv == null || (!op.contains(">") && !op.contains("<")))
				return (boolean) Constants.jse.eval(v + op + cv) ? 2 : -1;

			Float[] f = op.contains(">") ? new Float[]{cv+hv,cv,cv-hv} : new Float[]{cv-hv,cv,cv+hv};
			for(int i =0;i<f.length;i++){
				boolean b = (boolean) Constants.jse.eval(v + op + f[i]);
				if(b){
					return 2 - i;
				}
			}
			return -1;

		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return -2;
	}
    
}
