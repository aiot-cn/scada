package org.aiot.service;

import org.aiot.infc.ValInfc;
import org.aiot.model.lang.PointData;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.aiot.model.table.SysTrigger;
import org.aiot.model.table.TPoint;
import org.aiot.model.table.TRecord;
import org.aiot.util.*;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * 点位服务
 */
@IocBean(create="init")
public class PointService implements Observer  {
	Log log = Logs.get();
	@Inject BaseService bs;

	private final Map<Long,PointData> pointDataMap = new HashMap<>();

	public void init() {

	}

	public void close(){

	}

	@Override
	public void update(Observable o, Object arg) {

	}

	public TPoint getOrCreate(String code){
		TPoint point = bs.getTCacheFirst(TPoint.class,v->Strings.equals(code,v.getCode()));
		if(point != null)
			return point;
		point = new TPoint();
		point.setCode(code);
		//point.setTypeId(typeId);
		bs.daoSave(point);
		return point;
	}

	public PointData put(TPoint point, Object data){
		long id = point.getId();
		String alarmRule = point.getAlarmRule();
		PointData pd = pointDataMap.computeIfAbsent(id,v->new PointData());
		pd.setValue(data);

		if(Strings.isNotBlank(alarmRule)){
			Object v = data;
			if(data instanceof ValInfc)
				v = ((ValInfc)data).getValue();
			String s = v + alarmRule;
			Object v2 = SysUtil.jsEval(s);
			if(v2 instanceof Number){
				pd.setState(((Number)v2).intValue());
			}else if(v2 instanceof Boolean){
				pd.setState(((Boolean)v2) ? 2 : 0);
			}
		}

		if(point.isRecOnEvery() || (point.isRecOnState() && pd.changedState()) || pd.changedVal(point.getRecOnValue())){
			TRecord tRecord = new TRecord();
			if(data instanceof RecognitionRes){
				tRecord = ((RecognitionRes) data).toRecord();
			}else{
				tRecord.setVal(data);
			}
			tRecord.setState(pd.getState());
			tRecord.setPid(id);
			bs.daoSave(tRecord);
		}

		return pd;
	}

	public PointData put(Long id, Object data){
		if(data == null)
			return null;
		TPoint point = bs.getTCache(TPoint.class,id);
		if(point == null)
			return null;
		return put(point,data);
	}

	public PointData put(String code, Object data){
		if(data == null)
			return null;
		TPoint point = bs.getTCacheFirst(TPoint.class,v->Strings.equals(code,v.getCode()));
		if(point == null)
			return null;
		return put(point,data);
	}

	//按图片一次识别模板图片全部点位
	public RecognitionRes recognition(String code,Object targetImg){
		List<TPoint> points = bs.getTCache(TPoint.class,v->Strings.equals(code,v.getCode()));
		if(points.size() == 0)
			return null;
		RecognitionRes src = new RecognitionRes(points.get(0).getImage());
		for(TPoint point : points)
			src.addTarget(new Target(point.getTarget()));
		RecognitionRes targetRes = SiftUtil.featureTarget(src, targetImg);
		List<Target> targets = targetRes.getTargets();
		for(int i=0;i<targets.size();i++){
			Target target = targets.get(i);
			Mat mat = OpenCVUtil.crop(targetImg,target);
			BufferedImage bi = ImgUtil.read(mat);
			OpenCVUtil.release(mat);
			TPoint point = points.get(i);
			NutMap param = new NutMap("image",bi);
			param.put("point",point);

			SysTrigger trigger = BaseUtils.getTrigger(-5L,target.getLabel());
			Object val = BaseUtils.runWorkflow(trigger,param);
			target.setLabel(val == null ? null : val.toString());

			NutMap nm = new NutMap("point",point);
			nm.put("trigger",trigger);
			target.setRemark(nm);
			//保存点位记录
			RecognitionRes res = new RecognitionRes(targetImg,target);
			res.setValue(val);
			put(point,res);
		}
		return targetRes;
	}

	public Map<Long,PointData> getPointDataMap(){
		return pointDataMap;
	}

	public PointData getPointData(Long id){
		return pointDataMap.get(id);
	}

}
