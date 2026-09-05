package org.aiot.lang.annotation;

import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.DeviceRoleEnum;

import java.lang.annotation.*;

/**
 参数、指令、解析 需指定类型 <br>
 其它类型自动，如字段类型继承BaseDevice为子设备
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Documented
public @interface AoReflect {

	String value() default "";

	AstEnum type() default AstEnum.auto;
	DeviceRoleEnum deviceRole() default DeviceRoleEnum.APP;

	boolean getter() default false;

	/**
	 * 编码
	 */
	String code() default "";

	/**
	 * 描述
	 */
	String placeholder() default "";

	/**
	 * 是否必须
	 */
	boolean required() default false;

	/**
	 * 选择
	 */
	String select() default "";

	/**
	 * 输入 type:text,suffix:exe
	 */
	String input() default "";

	String url() default "";

	int sequence() default 99;
}
