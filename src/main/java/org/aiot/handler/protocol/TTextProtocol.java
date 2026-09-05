package org.aiot.handler.protocol;

import org.aiot.infc.SResProtocol;
import org.aiot.main.Constants;
import org.aiot.model.table.TTemplate;
import org.aiot.model.table.TText;
import org.aiot.service.BaseService;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

//
public class TTextProtocol implements SResProtocol {
	private TText tText;

	public TTextProtocol(String pathName){
		BaseService bs = Constants.ioc.get(BaseService.class);
		String[] s = pathName.split("-");
		tText = bs.daoFetch(TText.class, Cnd.where("plass","=",s[0]).and("pid","=",s[1]));
		if(tText == null){
			tText = new TText();
			tText.setPlass(s[0]);
			tText.setPid(Long.parseLong(s[1]));
		}
	}

	public TTextProtocol(TText tText){
		this.tText = tText;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getContent() {
		return tText.getContent();
	}

	@Override
	public byte[] getBytes(){
		return null;
	}

	@Override
	public String getParam() {
		NutMap nm = new NutMap();
		nm.put("id", tText.getId());
		return Json.toJson(nm);
	}

	@Override
	public void saveContent(String content){
		tText.setContent(content);
		Constants.ioc.get(BaseService.class).daoSave(tText);
	}

}
