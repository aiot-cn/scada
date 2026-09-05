package org.aiot.model.enums;

import org.nutz.dao.impl.NutDao;

/**
 * 数据源
 */
public enum DaoEnum {

	main(null)
;

	private NutDao dao;

	DaoEnum(NutDao dao){
		this.dao = dao;
	}


	public NutDao getDao() {
		return dao;
	}

	public void setDao(NutDao dao) {
		this.dao = dao;
	}
}
