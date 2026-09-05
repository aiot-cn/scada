package org.aiot.controller;

import com.alibaba.fastjson.JSONObject;
import org.aiot.lang.annotation.AoTbase;
import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.CompareEnum;
import org.aiot.model.table.TBase;
import org.aiot.model.table.TBaseSeq;
import org.aiot.mvc.CheckLevel;
import org.aiot.service.BaseService;
import org.nutz.castor.Castors;
import org.nutz.dao.Chain;
import org.nutz.dao.*;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.adaptor.injector.ObjectPairInjector;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.aiot.main.Constants.ioc;

@At("/table")
@Filters
public class TableController {

	@At("/?")
	public void getList2(String tableName,HttpServletRequest r,HttpServletResponse res) throws Throwable {
		getList(tableName,r,res);
	}

	@At("/getList")
	public void getList(String tableName,HttpServletRequest r,HttpServletResponse res) throws Throwable {
		BaseService bs = ioc.get(BaseService.class);
		Class<?> c = bs.getModelClass(tableName);
		int pageNumber = 1;
		int pageSize = 1000;
		Cnd cnd = Cnd.NEW();

	    Enumeration<String> paramNames = r.getParameterNames();
	    while (paramNames.hasMoreElements()) {
	      String paramName = paramNames.nextElement();
	      String paramValue = r.getParameter(paramName);

	      switch (paramName) {
			case "tableName":
			case "DESC":
			case "ASC":
				 break;

			case "pageNum":
				pageNumber = Integer.parseInt(paramValue);
				break;
			case "pageSize":
				pageSize = Integer.parseInt(paramValue);
				break;
			default:
				if(Strings.isNotBlank(paramValue)){
					String op = "=";
					if(paramValue.matches("^%.*%$")){
						op = "like";
					}else if(paramValue.equalsIgnoreCase("NULL")){
						paramValue = null;
					}
					String[] name = paramName.split("_");
					if(name.length > 1){
						CompareEnum ce = CompareEnum.get(name[1]);
						if(ce != null)
							op = ce.getName();
					}
					if("in".equals(op) && !paramValue.startsWith("'")){
						cnd.and(name[0], op, paramValue.split(","));
					}else{
						cnd.and(name[0], op, paramValue);
					}

				}
				break;
			}

		}


		if("GET".equals(r.getMethod()) && pageSize == 0) {
			NutDao dao = bs.getDao();
			TBase t1 = (TBase) dao.fetch(c,cnd.clone().desc("id"));
			long maxId = t1 == null ? 0 : t1.getId();
			int count = dao.count(c,cnd);
			String etag = maxId+"-"+count;
			TBase t2 = (TBase) dao.fetch(c,cnd.clone().and("updateDate","is not",null).desc("updateDate"));
			if(t2 != null && t2.getUpdateDate() != null)
				etag += "-"+ Constants.ymdhmsFormat.format(t2.getUpdateDate());
			res.setHeader("Etag",etag);
			String ifNoneMath = r.getHeader("If-None-Match");
			if(Strings.equals(etag,ifNoneMath)){
				new HttpStatusView(304).render(r,res,null);
				return;
			}
		}

		String[] asc = r.getParameterValues("ASC");
		if(asc != null)
			for(String s : asc) {
				if(s.matches("\\w+"))
					cnd.asc(s);
			}
		String[] desc = r.getParameterValues("DESC");
		if(desc != null)
			for(String s : desc) {
				if(s.matches("\\w+"))
					cnd.desc(s);
			}

    	QueryResult result =  bs.query(c,cnd,pageSize,pageNumber);
		UTF8JsonView.COMPACT.render(r,res,result);
	}

	@At("/sqlCode/?")
	public @Ok("json") QueryResult sqlCode(String code,HttpServletRequest r){
		int pageNumber = 1,pageSize = 100;
		NutMap map = new NutMap();
		Enumeration<String> paramNames = r.getParameterNames();
		while (paramNames.hasMoreElements()) {
		      String paramName = (String) paramNames.nextElement();
		      String paramValue = r.getParameter(paramName);
		      switch (paramName) {
		      	case "pageNum":
					pageNumber = Integer.parseInt(paramValue);
					break;
				case "pageSize":
					pageSize = Integer.parseInt(paramValue);
					break;

				default:
					map.put(paramName, paramValue);

					break;
				}
		}
		Pager pager = null;
		BaseService bs = ioc.get(BaseService.class);
		if(pageNumber != 0){
			pager = bs.getDao().createPager(pageNumber, pageSize);
		}

		return new QueryResult(bs.querySqlCode(code, map, NutMap.class,pager), pager);
	}

	/**
	 *  清除缓存
	 * @return
	 */

	@At
	public @Ok("json") DataRes refreshSqlCode(){
		ioc.get(BaseService.class).initSqlCode();
		return new DataRes();
	}

	@At
	public @Ok("json") DataRes initSqlCode(){
		BaseService bs = ioc.get(BaseService.class);
		bs.clearSqlCode();
		bs.loadSqlCode();
		return new DataRes();
	}

	@At("/doSave")
	public @Ok("json") DataRes doSave(String tableName,HttpServletRequest r){
		String fieldFilter = "UK";
		Enumeration<String> paramNames = r.getParameterNames();
		while (paramNames.hasMoreElements()) {
			fieldFilter += "|" + paramNames.nextElement();
		}
		BaseService bs = ioc.get(BaseService.class);
		Class<?> c = bs.getModelClass(tableName);
		ObjectPairInjector inject = new ObjectPairInjector(null, c);
		TBase tBase = (TBase) inject.get(null, r, null, null);

		if(tBase.getId() == null){
			String uk = r.getHeader("UK");
			if(Strings.isNotBlank(uk)){
				Cnd cnd = Cnd.NEW();
				for(String u : uk.split(",")){
					cnd.and(u,"=",r.getParameter(u));
				}
				TBase t = (TBase) bs.daoFetch(c,cnd);
				if(t != null){
					tBase.setId(t.getId());
				}
			}
		}
		bs.daoSave(tBase,fieldFilter);
		return new DataRes(bs.getDao().fetch(c, tBase.getId()));
	}

	@At("/doDel")
	public @Ok("json") DataRes doDel(String tableName,String primaryKey,HttpServletRequest r){
		if(primaryKey == null){
			primaryKey = "id";
		}
		BaseService bs = ioc.get(BaseService.class);
		Class<?> c = bs.getModelClass(tableName);
		String[] v = r.getParameterValues(primaryKey);
		Long[] pks = Castors.me().castTo(v.length > 1 ? v : v[0],Long[].class);
		if(bs.isTCache(c)){
			for (Long pk : pks) {
				bs.daoDel(c, pk);
			}
		}else{
			bs.daoClear(c,Cnd.where(primaryKey,"in",pks));
		}

		return new DataRes();
	}

	@At("/doClear/?")
	public @Ok("json") DataRes doClear(String tableName){
		BaseService bs = ioc.get(BaseService.class);
		Class<?> c = bs.getModelClass(tableName);
		bs.daoClear(c);
		return new DataRes();
	}

	@At("/doOrder")
	@AdaptBy(type=JsonAdaptor.class)//String tableName,String order(排序字段全小写),
	public @Ok("json") DataRes doOrder(@Param("..") List<JSONObject> list,HttpServletRequest request){
		String primaryKey = Optional.ofNullable(request.getParameter("primaryKey")).orElse("id");
		primaryKey = Strings.hump2Line(primaryKey);
		String tableName = request.getParameter("tableName");
		BaseService bs = ioc.get(BaseService.class);
		Class<?> c = bs.getModelClass(tableName);
		String order = request.getParameter("order");
		order = Strings.hump2Line(order);

		for(JSONObject j : list){
			int seq = j.getIntValue(order);
			Long id = j.getLong(primaryKey);
			int i = bs.getDao().update(c, Chain.make(order,seq), Cnd.where(primaryKey, "=", id));

			if(i == 1 && TBaseSeq.class.isAssignableFrom(c)){
				AoTbase ao = c.getAnnotation(AoTbase.class);
				if(ao != null && ao.cache()){
					Map<Long,TBase> map = bs.getTCacheMap(c);
					TBaseSeq baseSeq = (TBaseSeq) map.get(id);
					if(baseSeq != null)
						baseSeq.setSequence(seq);
				}
			}
		}
		return new DataRes();
	}

	@At("/execSql")
	public @Ok("json") DataRes execSql(String sql){
		String[] sqls = sql.split(";");
		String msg = null;
		for(String s : sqls){
			try {
				ioc.get(BaseService.class).getDao().execute(Sqls.create(s));
			} catch (DaoException e) {
				msg = e.getMessage();
				break;
			}

		}
		return new DataRes(msg);
	}
}
