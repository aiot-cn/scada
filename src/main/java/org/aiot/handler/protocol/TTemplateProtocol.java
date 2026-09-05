package org.aiot.handler.protocol;

import org.aiot.infc.SResProtocol;
import org.aiot.main.Constants;
import org.aiot.model.table.TTemplate;
import org.aiot.service.BaseService;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

//
public class TTemplateProtocol implements SResProtocol {
	private TTemplate tTemplate;

	public TTemplateProtocol(String pathName){
		BaseService bs = Constants.ioc.get(BaseService.class);
		this.tTemplate = bs.getTCache(TTemplate.class,Long.parseLong(pathName));
	}

	public TTemplateProtocol(TTemplate tTemplate){
		this.tTemplate = tTemplate;
	}

	@Override
	public String getTitle() {
		return tTemplate.getTitle();
	}

	@Override
	public String getContent() {
		return tTemplate.getContent();
	}

	@Override
	public byte[] getBytes(){
		return null;
	}

	@Override
	public String getParam() {
		NutMap nm = new NutMap();
		nm.put("id", tTemplate.getId());
		nm.put("title", tTemplate.getTitle());
		return Json.toJson(nm);
	}

	@Override
	public void saveContent(String content){
		tTemplate.setContent(content);
		Constants.ioc.get(BaseService.class).daoSave(tTemplate);
	}

}
