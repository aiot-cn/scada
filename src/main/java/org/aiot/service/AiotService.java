package org.aiot.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.aiot.main.Constants;
import org.aiot.model.project.Token;
import org.aiot.model.table.TBase;
import org.aiot.model.table.TFile;
import org.aiot.util.HttpUtil;
import org.aiot.util.SysUtil;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.File;
import java.net.ConnectException;
import java.util.*;


@IocBean
public class AiotService {
	Log log = Logs.get();
	@Inject BaseService bs;
	@Inject ConfigService cs;
	@Inject CronService crons;
	@Inject DeviceService ds;
	@Inject CommuService commus;
	
	public void destroy(){
		commus.close();
		crons.clean();
		ds.destroy();
		DruidDataSource dataSource = (DruidDataSource) bs.getDao().getDataSource();
		dataSource.close();
	}

	public void reInit(String dataSourceUrl){
		bs.initDataSource(dataSourceUrl);
		bs.clear();
		bs.init();
		cs.init();
		//ds.init();
		ds.loadDeviceConfig();
		ds.initDevice();
		commus.init();
		crons.init();
	}

	public void pullTable(String host,String... table){
		for(String t:table)
			pullTable(host,t);
	}

	public void pullTable(String host,String table){
		String url = host + "/table/getList?tableName="+table;
		String token = SysUtil.desEncode(Json.toJson(new Token("admin")));
		token = token.replace('+', '-').replace('/', '_').replaceAll("=", "");

		Class<?> t = bs.getModelClass(table);
		int maxId = bs.getMaxId(t);
		Date date = bs.getLast(t);
		date = date == null? new Date(0L) : new Date(date.getTime() + 1000);

		String updateLast = Times.sDT(date);
		log.infof("拉取表[%s]新增开始ID:%d 更新从:%s",table,maxId,updateLast);
		//更新已有的
		Map<String,String> p1 = new HashMap<>();
		p1.put("token",token);
		p1.put("id_lte",maxId+"");
		p1.put("updateDate_gt",updateLast);

		try {
			JSONObject json = HttpUtil.requestJson(url,p1);
			List<?> updateList = JSONArray.parseArray(json.getString("list"),t);
			for (Object o : updateList) {
				TBase tBase = (TBase) o;
				bs.daoSave(tBase);
			}
			//添加
			Map<String,String> p2 = new HashMap<>();
			p2.put("token",token);
			p2.put("id_gt",maxId+"");
			p2.put("ASC","id");

			json = HttpUtil.requestJson(url,p2);
			List<?> insertList = JSONArray.parseArray(json.getString("list"),t);
			bs.fastInsert(insertList);
			log.infof("拉取表[%s]完成 新增数量:%d 更新数量:%d",table,insertList.size(),updateList.size());
		}catch (Exception e){
			log.errorf("拉取表[%s]错误：%s",table,e.getMessage());
		}

	}

	public void pullFile(String host,String table,String field,String pathPrefix){
		Class<?> t = bs.getModelClass(table);
		Mirror<?> mirror = Mirror.me(t);
		int maxId = bs.getMax(TFile.class,"pid",Cnd.where("plass","=",t.getSimpleName()));
		List<?> list = bs.query(t,Cnd.where("id", ">",maxId).and(field,"is not",null).asc("id"));

		List<TFile> tFiles = new ArrayList<>();
		for (Object o : list) {
			TBase tBase = (TBase) o;
			Object path = mirror.getValue(tBase, field);
			if (path == null || Strings.isBlank(path.toString()))
				continue;

			TFile tFile = new TFile();
			tFile.setTBase(tBase);
			tFile.setPathName(pathPrefix + "/" + path);
			tFile.setSize(0f);
			tFiles.add(tFile);
		}
		bs.fastInsert(tFiles);

		String token = SysUtil.desEncode(Json.toJson(new Token("admin")));
		token = token.replace('+', '-').replace('/', '_').replaceAll("=", "");
		Map<String,String> p1 = new HashMap<>();
		p1.put("token",token);

		List<TFile> downTFiles = bs.query(TFile.class,Cnd.where("size","=",0).and("plass","=",t.getSimpleName()));
		log.infof("下载表[%s]字段%s的文件到目录 %s 开始ID:%d 新增:%d 下载:%d",table,field,Strings.sBlank(pathPrefix,"/"),
				maxId,list.size(),downTFiles.size());
		for(int i=0;i<downTFiles.size();i++){
			TFile v = downTFiles.get(i);
			String info = (i+1) + "/" + downTFiles.size() + " " + v.getPathName() + " ";

			String url = host + "/json/file?name="+v.getPathName();
			File file = new File(Constants.HOME_PATH,v.getPathName());
			if(file.length() > 0){
				info += "文件已存在";
				v.setSize(file.length()/1024f);
				bs.daoSave(v);
			}else{
				try {
					HttpUtil.downloadFile(url,file,p1);
					long fileLength = file.length();
					if(fileLength > 0){
						v.setSize(fileLength/1024f);
						info += "下载成功 "+v.getSize()+"kb";
					}else{
						info += "下载失败";
					}
				}catch (Exception e){
					info += "下载异常:"+e.getMessage();
					v.setDescription(e.getMessage());
					if(!(e instanceof ConnectException)){
						v.setSize(-1F);
					}
				}finally {
					bs.daoSave(v);
				}
			}
			log.info(info);
		}

	}
	
}
