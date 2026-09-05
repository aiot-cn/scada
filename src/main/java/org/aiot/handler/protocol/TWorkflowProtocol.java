package org.aiot.handler.protocol;

import org.aiot.infc.SResProtocol;
import org.aiot.main.Constants;
import org.aiot.model.table.TWorkflow;
import org.aiot.service.BaseService;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

//plass=SysTrigger&pid=3
public class TWorkflowProtocol implements SResProtocol {
	private TWorkflow tWorkflow;

	public TWorkflowProtocol(String pathName){
		BaseService bs = Constants.ioc.get(BaseService.class);
		String[] a = pathName.split("[/.]");
		if(a.length == 1){
			this.tWorkflow = bs.getTCache(TWorkflow.class,Long.parseLong(a[0]));
		}else{
			String plass = a[0];
			Long pid = Long.parseLong(a[1]);
			this.tWorkflow = bs.getTCacheFirst(TWorkflow.class,v->plass.equals(v.getPlass()) && pid.equals(v.getPid()));
			if(this.tWorkflow == null){
				TWorkflow t = new TWorkflow();
				t.setPlass(plass);
				t.setPid(pid);
				this.tWorkflow = (TWorkflow) bs.daoSave(t);
			}
		}
	}

	@Override
	public String getTitle() {
		return tWorkflow.getName();
	}

	@Override
	public String getContent() {
		return tWorkflow.getContent();
	}

	@Override
	public byte[] getBytes(){
		return null;
	}

	@Override
	public String getParam() {
		NutMap nm = new NutMap();
		nm.put("id", tWorkflow.getId());
		nm.put("args", tWorkflow.getArgs());
		return Json.toJson(nm);
	}

	@Override
	public void saveContent(String content){
		tWorkflow.setContent(content);
		Constants.ioc.get(BaseService.class).daoSave(tWorkflow);
	}

}
