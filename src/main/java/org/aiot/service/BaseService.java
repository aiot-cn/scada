package org.aiot.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.aiot.lang.NotifyEvent;
import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.EventEnum;
import org.aiot.model.enums.PatternEnum;
import org.aiot.model.enums.SessionEnum;
import org.aiot.model.table.*;
import org.nutz.dao.*;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlContext;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.*;
import org.nutz.lang.util.NutMap;
import org.nutz.resource.Scans;
import sun.misc.DoubleConsts;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * iocBean 不声明为 final，被继承时 ioc 可能会 get 到其他实例
 */
@IocBean(create="init")
public final class BaseService extends Observable {
	
	@Inject NutDao dao;

	private final Map<String,NutDao> daoMap = new HashMap<>();
	private Map<String, SqlCode> sqlCodeMap;
	private Map<Long,List<SqlCondition>> sqlConditionMap;

	private final Map<Class<?>,Map<Long,TBase>> tCache = new ConcurrentHashMap<>();//基础表缓存
	private final Map<Class<?>,AtomicLong> PK = new HashMap<>();

	private final Map<Class<?>,List<Field>> tField = new HashMap<>();//关联表缓存
	private final Map<Class<?>,Field[]> modelFields = new HashMap<>();//所有表
	private final Map<String,Class<?>> tName = new HashMap<>();

	private final Map<String,Boolean> packMap = new HashMap<>();

	private JSONObject modelJson;
	private JSONObject builtInJson;

	public void init(){
		Daos.FORCE_HUMP_COLUMN_NAME = true;
		String str = Files.read("conf/config.json");
		String str2 = Files.read("conf/builtIn.json");
		modelJson = JSONObject.parseObject(str);
		builtInJson = JSONObject.parseObject(str2);
		initTable("org.aiot.model.table");
		initSqlCode();
		loadSqlCode();
		for(SysDataSource src :  getTCache(SysDataSource.class)){
			try {
				daoMap.put(src.getName(),loadDao(src));
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	}

	public void clear(){
		tCache.clear();
		PK.clear();
		packMap.clear();
	}

	//重新加载主数据库
	public void initDataSource(String url){
		DruidDataSource druid = new DruidDataSource();
		druid.setUrl(url);
		druid.setMaxWait(1000);//默认-1 一直等待可用连接
		druid.setDefaultAutoCommit(false);//默认true
		druid.setValidationQuery("SELECT 1");

		NutDao nutDao = new NutDao();
		nutDao.setDataSource(druid);
		dao = nutDao;
	}
	/**
	 * 自动建表，初始化表字段
	 * @param pack 包名
	 */
	public synchronized void initTable(String pack){
		if(packMap.containsKey(pack))
			return;
		packMap.put(pack,true);

		Daos.createTablesInPackage(dao, pack, false);//自动建表		
		Daos.migration(dao, pack,true,false,false);//表自动增减字段  pojo含非表字段 sqlite索引会报错

		initModel(pack);
		for(Class<?> klass: Scans.me().scanPackage(pack)) {
			Table aot = klass.getAnnotation(Table.class);
			if(aot == null)
				continue;
			tName.put(klass.getSimpleName(),klass);
			modelFields.put(klass,Mirror.me(klass).getFields());

			if (isTCache(klass)) {
				tCache.put(klass,new HashMap<>());
				List<TBase> list = (List<TBase>)dao.query(klass,null);
				JSONArray array = builtInJson.getJSONArray(klass.getSimpleName());
				if(array != null){
					array.forEach(m->{
						JSONObject j = (JSONObject) m;
						TBase base = (TBase) j.toJavaObject(klass);
						list.add(base);
					});
				}
				list.forEach(this::setTCache);

			}

			Field[] fields = klass.getDeclaredFields();

			//级联删除用
			for(Field field : fields){
				AoTbase fAo = field.getAnnotation(AoTbase.class);
				if(fAo != null && fAo.from() != Object.class){
					List<Field> fieldList = tField.computeIfAbsent(fAo.from(),v->new ArrayList<>());
					fieldList.add(field);
				}
			}
		}
	}

	public NutDao loadDao(SysDataSource src){
		DruidDataSource druid = new DruidDataSource();
		druid.setName(src.getName());//标识数据源名称
		druid.setUrl(src.getUrl());
		druid.setUsername(src.getUsername());
		druid.setPassword(src.getPassword());
		druid.setMaxActive(src.getMaxActive());//默认8
		druid.setMaxWait(src.getMaxWait() == null ? 5000 : src.getMaxWait());//默认-1 一直等待可用连接
		druid.setDefaultAutoCommit(1 == src.getDefaultAutoCommit());//默认true
		druid.setValidationQuery(src.getValidationQuery());

		NutDao nutDao= new NutDao();
		nutDao.setDataSource(druid);
		return nutDao;
	}

	/**
	 * 初始化表数据
	 */
	public void initModel(String pack) {
		modelJson.forEach((k,v)->{
			try {
				if(k.startsWith(pack)){
					//TODO 重复加载
					Class<?> c = Lang.loadClass(k);
					if(dao.count(c) == 0) {
						JSONArray arr = (JSONArray)v;
						arr.forEach(m->{
							JSONObject j = (JSONObject) m;
							TBase base = (TBase) j.toJavaObject(c);
							daoInsert(base);
						});
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		});
	}

	//----------------------------------- CRUD -------------------------------------------------------

	public Long getPK(Class<?> klass){
		AtomicLong seq = PK.computeIfAbsent(klass,v->new AtomicLong(getMaxId(klass)));
		return seq.incrementAndGet();
	}

	public void rePK(Class<?> klass){
		PK.put(klass,new AtomicLong(getMaxId(klass)));
	}

	public <T> List<T> getParent(Class<T> klass,Long... id){
		NutMap nm = NutMap.NEW().setv("id",id);
		nm.setv("name",Strings.hump2Line(klass.getSimpleName()));
		return querySqlCode("sysParent",nm,klass,null);
	}

	public <T> List<T> getSub(Class<T> klass,Long... id){
		NutMap nm = NutMap.NEW().setv("id",id);
		nm.setv("name",Strings.hump2Line(klass.getSimpleName()));
		return querySqlCode("sysSub",nm,klass,null);
	}
	/**
	 * ID不为空执行更新，忽略null<br>
	 * ID为空执行新增，忽略null及空白字符串
	 */
	public synchronized TBase daoSave(TBase tBase,String fieldFilter) {
		SysUser user = SessionEnum.user.val();

		AoTbase ao = tBase.getClass().getAnnotation(AoTbase.class);
		boolean isCache = ao != null && ao.cache();
		boolean isNew = tBase.getId() == null;
		if(isCache){
			setChanged();
			notifyObservers(new NotifyEvent(EventEnum.SAVE_BEFORE,tBase));
		}
		if(isNew){
			Class<?> klass = tBase.getClass();
			Long id = getPK(klass);
			tBase.setId(id);
			if(user != null)
				tBase.setCreateBy(user.getId());
			tBase.setCreateDate(new Date());
			dao.insert(tBase,true,false,true);
		}else {
			if(user != null)
				tBase.setUpdateBy(user.getId());
			tBase.setUpdateDate(new Date());
			if(fieldFilter == null){
				dao.updateIgnoreNull(tBase);
			}else {
				dao.update(tBase, "^("+fieldFilter+"|updateDate|updateBy)$");
			}				
		}

		if(isCache){
			TBase tBaseBefore = getTCache(tBase.getClass(),tBase.getId());
			tBase = dao.fetch(tBase.getClass(), tBase.getId());
			setTCache(tBase);
			setChanged();
			notifyObservers(new NotifyEvent(EventEnum.SAVE_AFTER,tBase,tBaseBefore));
		}
		return tBase;
	}

	public TBase daoSave(TBase tBase) {
		return daoSave(tBase,null);
	}

	public void daoDel(Class<?> classOfT,long id){
		TBase tBase =  (TBase)(isTCache(classOfT) ? getTCache(classOfT,id) : dao.fetch(classOfT,id));
		daoDel(tBase);
		setChanged();
		notifyObservers(new NotifyEvent(EventEnum.DELETE_AFTER,tBase));
	}

	public void daoDel(List<? extends TBase> list){
		list.forEach(this::daoDel);
	}

	public void daoDel(TBase tbase){
		Class<?> classOfT = tbase.getClass();
		List<Field> fields = tField.get(classOfT);
		//需要级联删除的
		if(fields != null){
			for(Field field:fields){
				AoTbase at = field.getAnnotation(AoTbase.class);
				Object val = Mirror.me(tbase).getValue(tbase, at.field());
				Class<TBase> subClass = (Class<TBase>) field.getDeclaringClass();//下一级的类
				//没有缓存也不需要级联删除
				if(!isTCache(subClass) && tField.get(subClass) == null){
					dao.clear(subClass,Cnd.where(field.getName(),"=",val));
				}else if(val != null){
					List<TBase> subList = dao.query(subClass,Cnd.where(field.getName(),"=",val));
					for(TBase sub:subList)
						daoDel(sub);
				}

			}
		}
		int i = dao.delete(classOfT, tbase.getId());
		if(i > 0 && isTCache(tbase.getClass())){
			Map<Long,TBase> m = getTCacheMap(classOfT);
			m.keySet().removeIf(v->v.equals(tbase.getId()));
		}

	}

	public void daoClear(Class<?> classOfT){
		int i = dao.clear(classOfT);
		if(i > 0){
			tCache.keySet().removeIf(v->v.equals(classOfT));
		}
	}

	/**
	 * 有ID的情况仍然执行插入
	 */
	public void daoInsert(TBase tBase) {
		SysUser user = SessionEnum.user.val();
		if(user != null)
			tBase.setCreateBy(user.getId());
		tBase.setCreateDate(new Date());
		if(tBase.getId() == null){
			tBase.setId(getPK(tBase.getClass()));
		}	
		dao.insert(tBase,true,false,true);
	}

	public void daoInsert(List<? extends TBase> list){
		if(list == null || list.size() == 0)
			return;
		Date date = new Date();
		for(TBase b : list){
			if(b.getCreateDate() == null)
				b.setCreateDate(date);
		}
		dao.fastInsert(list);

		TBase t = list.get(0);
		AtomicLong l = PK.get(t.getClass());
		if(l != null)
			l.set(dao.getMaxId(t.getClass()));
	}

	public  boolean isTCache(Class<?> classOfT){
		AoTbase ao = classOfT.getAnnotation(AoTbase.class);
		return ao != null && ao.cache();
	}

	//----------------------------------- SqlCode -------------------------------------------------------
	public void initSqlCode() {
		List<SqlCode> sqlCodeList= dao.query(SqlCode.class, Cnd.where("isRemoved", "=", 0));
		sqlCodeMap = sqlCodeList.stream().collect(Collectors.toMap(SqlCode::getCode,v->v));

		List<SqlCondition> sqlConditionList= dao.query(SqlCondition.class, Cnd.where("isRemoved", "=", 0).asc("sequence").asc("id"));
		sqlConditionMap = sqlConditionList.stream().collect(Collectors.groupingBy(SqlCondition::getCodeId));
	}

	public void loadSqlCode() {

		String sqlCodeStr = Files.read("conf/sqlCode.json");
		JSONObject sqlCodeJson = JSONObject.parseObject(sqlCodeStr);
		boolean isUpdate = false;
		for(String k : sqlCodeJson.keySet()) {
			JSONObject sqlCodeObject = sqlCodeJson.getJSONObject(k);
			SqlCode sqlCode = sqlCodeObject.toJavaObject(SqlCode.class);
			sqlCode.setCode(k);

			SqlCode sqlCode2 = sqlCodeMap.get(k);
			if(sqlCode2 != null && sqlCode.getUpdateDate() != null){
				if(sqlCode.getUpdateDate().getTime() > (sqlCode2.getUpdateDate() != null ? sqlCode2.getUpdateDate().getTime() : sqlCode2.getCreateDate().getTime())){
					sqlCode.setId(sqlCode2.getId());
					dao.clear(SqlCondition.class,Cnd.where("codeId","=",sqlCode2.getId()));
					sqlCode2 = null;
				}
			}

			if(sqlCode2 == null) {
				daoSave(sqlCode);
				List<SqlCondition> a = JSON.parseArray(sqlCodeObject.getString("condition"), SqlCondition.class);
				if(a != null) {
					a.forEach(v-> {
						v.setId(getPK(SqlCondition.class));
						v.setCodeId(sqlCode.getId());
					});
					dao.fastInsert(a);
				}
				isUpdate = true;
			}
		}

		if(isUpdate) {
			initSqlCode();
		}
	}

	/**
	 * 清除sqlCode
	 */
	public void clearSqlCode() {
		sqlCodeMap = new HashMap<>();
		dao.clear(SqlCode.class);
		dao.clear(SqlCondition.class);
	}

	public List<NutMap> querySql(String sqlStr,NutMap params){
		return querySql(sqlStr,null,NutMap.class,params,null,null);
	}

	public List<NutMap> querySql(String sqlStr,Cnd cnd){
		if(!sqlStr.contains("$condition"))
			sqlStr += " $condition";
		return querySql(sqlStr,cnd,NutMap.class,null,null,null);
	}

	public <T> List<T> querySql(String sqlStr,Cnd cnd, Class<T> classOfT, NutMap params,NutMap vars,Pager pager){
		Sql sql = Sqls.create(sqlStr);
		sql.setCallback(new QueryHumpCallback());
		sql.setCondition(cnd);
		sql.setParams(params);
		sql.setVars(vars);
		sql.setPager(pager);
		dao.execute(sql);
		return sql.getList(classOfT);
	}

	public List<NutMap> queryCode(String code,Map<String,Object> p,Pager pager){
		return querySqlCode(code,p,NutMap.class,pager);
	}

	/**
	 * SQLCode查询
	 * @param p 参数 关键词参数 ASC、DESC、HAS_COUNT
	 * @param classOfT 实体类
	 * @param pager 分页对象
	 */
	public <T> List<T> querySqlCode(String code,Map<String,Object> p,Class<T> classOfT,Pager pager){
		NutDao dao = this.dao;
		SqlCode sqlCode = sqlCodeMap.get(code);//sqlCodeJson.getObject(code,SqlCode.class);
		if(sqlCode == null){
			throw Lang.makeThrow("SqlCode:"+code+"还没有被定义");
		}
		
		Sql sql = Sqls.create(sqlCode.getSqlStatement());//加换行会影响sqlServer分页
		if(classOfT.getAnnotation(Table.class) != null) {
			sql.setCallback(Sqls.callback.entities());
			sql.setEntity(dao.getEntity(classOfT));
		}else {
			sql.setCallback(new QueryHumpCallback());
		}

			
		Cnd cnd = Cnd.NEW();
		SqlExpressionGroup group= null;
		List<SqlCondition> cons = sqlConditionMap.get(sqlCode.getId());
		if(cons != null){
			for(SqlCondition con : cons){
				Object parameter = p.get(con.getValue());
				
				if(parameter != null){
					if(parameter instanceof String){
						String ps = parameter.toString();
						if(PatternEnum.P_TIME.matches(ps)) {
							parameter = Times.C(ps);
						}
						if(ps.equals("null")) {
							parameter = null;
						}
					}
					//┌ ├ └ ┼
					if("in".equals(con.getOp()) && parameter instanceof String){
						String s = parameter+"";
						if(s.indexOf("'") != 0)
							parameter = "'"+s.replaceAll(",","','")+"'";
					}
					if("┌".equals(con.getGro())){
						group = Cnd.exps(con.getName(), con.getOp(), parameter);
						if(cnd == null){
							cnd =Cnd.where(group);
						}else if("OR".equals(con.getAo())) {
							cnd.or(group);
						}else if("andNot".equals(con.getAo())){
							cnd.andNot(group);
						}else {
							cnd.and(group);
						}
					}else if(Strings.isin(new String[]{"├","└"}, con.getGro())){
						if("OR".equals(con.getAo())) {
							group.or(con.getName(), con.getOp(), parameter);
						}else {
							group.and(con.getName(), con.getOp(), parameter);
						}
					}else if("InBySql".equals(con.getOp())){
						cnd.where().andInBySql(con.getName(), con.getSql(), parameter);
					}else {
						if(cnd == null) {
							cnd =Cnd.where(con.getName(), con.getOp(), parameter);
						}else if("OR".equals(con.getAo())){
							cnd.or(con.getName(), con.getOp(), parameter);
						}else if("andNot".equals(con.getAo())){
							cnd.andNot(con.getName(), con.getOp(), parameter);
						}else{
							cnd.and(con.getName(), con.getOp(), parameter);
						}
					}
				}
			}
		}

		String[] orderField = new String[]{"ASC","DESC","ASC2","DESC2","ASC3","DESC3"};
		for(String f : orderField){
			if(p.get(f) == null)
				continue;
			String sc = p.get(f)+"";
			sc = Strings.hump2Line(sc);
			if(f.indexOf("ASC")==0)
				cnd.asc(sc);
			else
				cnd.desc(sc);
		}
		sql.setCondition(cnd);

		if(pager != null) {
			sql.setPager(pager);
			if(p.get("HAS_COUNT") != null){
				pager.setRecordCount((int) Daos.queryCount(dao, sql));
			}
		}
		dao.execute(sql);
		return sql.getList(classOfT);
		
	}

	public <T> T queryCode(Class<T> classOfT, String code, NutMap p){
		List<T> list = querySqlCode(code,p,classOfT,null);
		if(list == null || list.size() == 0)
			return null;
		return list.get(0);
	}
	public <T> List<T> query(Class<T> classOfT,Condition cnd){
		return  dao.query(classOfT,cnd);
	}

	public <T> T query(Class<T> classOfT,Long id){
		return dao.fetch(classOfT,id);
	}

	public int update(Class<?> classOfT, Chain var2, Condition var3){
		return dao.update(classOfT, var2, var3);
	}

	public <T> T fastInsert(T var1){
		Object first = Lang.first(var1);
		if(first == null)
			return null;

		T  t =  dao.fastInsert(var1);
		rePK(first.getClass());
		return t;
	}

	public int getMaxId(Class<?> classOfT){
		return dao.getMaxId(classOfT);
	}

	public int getMax(Class<?> classOfT,String fieldName,Condition cnd){
		return dao.func(classOfT, "MAX",fieldName, cnd);
	}

	public Date getLast(Class<?> classOfT){
		Object o =  dao.func2(classOfT,"MAX","updateDate");
		if(o == null)
			return null;
		if(o instanceof Long)
			return Times.D(((Long) o));

		return (Date) o;
	}
	public Pager createPager(int pageNumber, int pageSize) {
		return dao.createPager(pageNumber, pageSize);
	}
	public int count(Class<?> var1, Condition var2){
		return dao.count(var1, var2);
	}

	public QueryResult query(Class<?> klass,Condition cnd,Integer pageSize,Integer pageNumber){
		if(pageSize == null)
			pageSize = 1000;
		if(pageNumber == null)
			pageNumber = 1;

		Pager pager = null;
		if(pageSize != 0){
			pager = dao.createPager(pageNumber, pageSize);
			pager.setRecordCount(dao.count(klass,cnd));
		}
		List<?> s = dao.query(klass,cnd,pager);

		return new QueryResult(s, pager);
	}

	public int daoClear(Class<?> classOfT, Condition cnd){
		return dao.clear(classOfT,cnd);
	}

	public <T> T daoFetch(Class<T> classOfT, Condition cnd){
		return dao.fetch(classOfT,cnd);
	}


	//----------------------------------- 缓存 -------------------------------------------------------
	/**
	 * 设置基础表缓存

	 */
	public void setTCache(TBase tBase){
		Map<Long, TBase> map = tCache.get(tBase.getClass());
		map.put(tBase.getId(),tBase);
		/*TBase cBase = map.get(tBase.getId());
		if(cBase == null){
			map.put(tBase.getId(),tBase);
		}else{
			Lang.copyProperties(tBase, cBase); //这样之前的数据会丢失
		}*/
	}

	/**
	 * 从缓存查找数据删除，同时删除数据库并且级联删除
	 */
	public <T extends TBase> void delTCache(Class<T> classOfT,Predicate<T> predicate){
		getTCache(classOfT,predicate).forEach(this::daoDel);
	}

	public Map<Long,TBase> getTCacheMap(Class<?> classOfT){
		AoTbase ao = classOfT.getAnnotation(AoTbase.class);
		if(ao == null || !ao.cache())
			throw Lang.makeThrow(classOfT.getSimpleName()+"没有设置缓存");
		return tCache.computeIfAbsent(classOfT, k -> new HashMap<>());
	}

	public <T,K> Map<K,List<T>> getTCacheMap(Class<T> classOfT,
											 Predicate<T> predicate,
											 Function<T,K> keyMapper){
		return getTCacheStream(classOfT).filter(predicate).collect(Collectors.groupingBy(keyMapper));
	};

	public <T> Stream<T> getTCacheStreamAll(Class<T> classOfT){
		Map<Long,TBase> map =  getTCacheMap(classOfT);
		return map.values().stream().map(v -> (T) v);
	}

	public <T> Stream<T> getTCacheStream(Class<T> classOfT){
		return getTCacheStreamAll(classOfT).filter(v->((TBase) v).getIsRemoved() == 0);
	}

	public <T extends TBase> List<T> getTCache(Class<T> classOfT){
		return getTCache(classOfT,v->true);
	}

	public <T extends TBase> List<T> getTCache(Class<T> classOfT,Predicate<? super T> predicate){
		List<T> list = getTCacheStream(classOfT).filter(predicate).collect(Collectors.toList());
		if(TBaseSeq.class.isAssignableFrom(classOfT)){
			list.sort(T::compareTo);
		}
		return list;
	}

	public <T, K, V> Map<K,V> getTCacheMap(Class<T> classOfT, Predicate<? super T> predicate,
										   Function<? super T, ? extends K> keyMapper,
										   Function<? super T, ? extends V> valueMapper){
		return getTCacheStream(classOfT).filter(predicate).collect(Collectors.toMap(keyMapper,valueMapper));
	}

	/**
	 * 缓存原始值，获取到的值直接修改会导致和数据库不同步
	 */
	@SuppressWarnings("unchecked")
	public <T> T getTCache(Class<T> classOfT,Long id){
		return (T) getTCacheMap(classOfT).get(id);
	}
	/**
	 * 缓存原始值，获取到的值直接修改会导致和数据库不同步
	 */
	public <T> T getTCacheFirst(Class<T> classOfT,Predicate<? super T> predicate){
		return getTCacheStream(classOfT).filter(predicate).findFirst().orElse(null);
	}

	public <T> T getTCacheAllFirst(Class<T> classOfT,Predicate<? super T> predicate){
		return getTCacheStreamAll(classOfT).filter(predicate).findFirst().orElse(null);
	}

	public JSONObject getModelJson() {
		return modelJson;
	}

	public Map<Class<?>, Field[]> getModelFields() {
		return modelFields;
	}

	public Class<?> getModelClass(String name){
		Class<?> c =  tName.get(Strings.upperFirst(name));
		if(c == null)
			throw Lang.makeThrow("没有表："+name);
		return c;
	}

	public NutDao getDao() {
		return dao;
	}

	public NutDao getDao(String name) {
		return daoMap.get(name);
	}

	public static class QueryHumpCallback implements SqlCallback {


		public Object invoke(Connection conn, ResultSet rs, Sql sql)throws SQLException {
			// ResultSetLooping 封装了遍历结果集的方法,里面包含了针对sqlserver等浮标型分页的支持
			ResultSetLooping ing = new ResultSetLooping() {
				protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCout) {
					NutMap re = new NutMap();
					create(re, rs);
					list.add(re);
					return true;
				}
			};
			ing.doLoop(rs, sql.getContext());
			return ing.getList();
		}

		public void create(Map<String, Object> re, ResultSet rs) {

			String name = null;
			int i = 0;
			try {
				ResultSetMetaData meta = rs.getMetaData();
				int count = meta.getColumnCount();
				for (i = 1; i <= count; i++) {
					name = meta.getColumnLabel(i);
					name =  Strings.lowerFirst(Strings.line2Hump(name));


					switch (meta.getColumnType(i)) {
						case Types.TIMESTAMP: // ORACLE的DATE类型包含时间,如果用默认的只有日期没有时间 from
						case Types.DATE: {
							re.put(name, rs.getTimestamp(i));
							break;
						}
						case Types.CLOB: {
							re.put(name, rs.getString(i));
							break;
						}
						case Types.FLOAT:{
							if(!(Math.abs(rs.getDouble(i)) <= DoubleConsts.MAX_VALUE)){
								re.put(name,"∞");
								break;
							}
						}
						default:
							re.put(name, rs.getObject(i));
							break;
					}
				}
			}
			catch (SQLException e) {
				if (name != null) {
					throw new DaoException(String.format("Column Name=%s, index=%d", name, i), e);
				}
				throw new DaoException(e);
			}
		}

	}
}
