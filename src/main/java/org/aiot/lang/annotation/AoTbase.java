package org.aiot.lang.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
@Documented
public @interface AoTbase {

	String value() default "";
	
	/**
	 * 是否缓存
	 */
	boolean cache() default true;

	/**
	 * 来源类
	 */
	Class<?> from() default Object.class;

	/**
	 * from类的关联属性名
	 */
	String field() default "id";

}
