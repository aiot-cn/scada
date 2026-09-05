package org.aiot.device.base.imgAnno;

import org.aiot.device.BaseDevice;
import org.aiot.device.base.imgAnno.model.*;
import org.aiot.device.detector.onnx.OnnxDevice;
import org.aiot.infc.device.BaseExtend;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.lang.Target;
import org.aiot.model.table.TFile;
import org.aiot.util.*;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.img.Images;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
  project
    ├── export 训练集
    │	├── images
    │	├── labels
    │	└── coco128.yaml
    ├── validation 验证集
    ├── train 训练的模型
    │	└── 20230707 训练日期
 	│		└── exp1 验证结果1
    │   	└── weights
    │   	    ├── best.pt
    │   	 	└── last.pt
    ├── test  测试目录
    │ 	└── t1
  	├── detect 验证目录
	├── generate 图片生成
  	└── thumbnail 缩略图
 */

@AoReflect("标注平台")
public class ImgAnnoDevice extends BaseDevice implements BaseExtend.RMenu {
	Log log = Logs.get();

	private String imgPath = "/AppData/imgAnno";

	//自动标注
	private OnnxDevice onnx;

	@AoReflect("图像实列2")
	private ImgAnnoDevice imgDev2;

	@AoReflect("运行中")
	private boolean isRun;

	private Long detectProId;

	private ImgAnnoTrain trainIng;
	private Map<String, ImgTag> imgTagMap;

	private String detectDir = "";//验证目录
	private TFile detectPt;


	private final LinkedList<ImgAnnoTrain> trainList = new LinkedList<>();
	private final String[] sufArr = {"jpg","jpeg","bmp","png","tif","tiff","webp","dng","mpo","pfm"};
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	@Override
	public void init() {
		//初始化表
		initTable(ImgAnnotation.class);
		imgPath = regDir("imgAnno");
		onnx = new OnnxDevice();
	}

	@Override
	public void selfTest() {

	}

	@Override
	@AoReflect(value="接收",type=AstEnum.command)
	public void comRx(Command command){
		String r = command.getReceive();
		//       48/49      2.64G    0.05848    0.09679    0.01446        519        640:   0%|          | 0/1 00:00;
		// Results saved to /home/iteasy/station/ai/img/张晓丽/2023年导入/万用表/260616万用表/230626孔洞/train/exp12
		if(r.matches("\\s+\\d+/\\d+\\s+[\\s\\S]+")){
			if(trainIng != null)
				trainIng.setTrainProgress(r.trim().split("\\s+")[0]);
		}else if(r.matches(".+saved.+[\\\\/]train[\\s\\S]+")){
			String path = r.split("\033\\[\\d+m")[1];
			log.info("训练结果:"+path);
			try {
				String tarPath = proPath(trainIng.getProId()) + "/" +Strings.sBlank(trainIng.getDir(),"") + "/train/" +Strings.sBlank(trainIng.getTrainName(),Times.format("yyMMdd_HHmmss",new Date()));
				Files.copyDir(new File(path),new File(tarPath));
				getCommunication().sendSocket(CdataEnum.Pa,"训练目标已输出到->"+tarPath);

				//Files.deleteDir(new File(path));
				TFile tFile = FileUtil.fileInfo(new File(tarPath,"/weights/best.pt"));
				tFile.setPlass("IMGTAG"+trainIng.getProId());
				String s = "图像:"+trainIng.getImgCount()+" 标签:"+trainIng.getLabelCount()+" 耗时:"+hm();
				if(trainIng.getModel() == 3)
					s += " produce:YOLO11 model:pose";
				if(trainIng.getModel() == 4)
					s += " produce:YOLO11 model:obb";
				if(trainIng.getModel() == 5)
					s += " model:ocr";
				tFile.setDescription(trainIng.getTagsStr()+ "\n" +s);
				bs.daoSave(tFile);

				/*List<WechatDevice> wd = ds.getDevByClass(WechatDevice.class);
				if(wd.size() > 0){
					WechatDevice.SecurityAlarmReminder weMsg = new WechatDevice.SecurityAlarmReminder("耗时:"+hm()+" image:"+trainIng.getImgCount()+" label:"+trainIng.getLabelCount(),trainIng.getTagsStr());
					weMsg.setUrl("http://back.iteasy.com/station/view"+tFile.getPathName());
					wd.get(0).pushAsync(trainIng.getWeChatPush(),weMsg);
				}*/

			}catch (Exception e){
				log.error("训练结果处理异常："+e.getMessage());
			}

		}else if(r.contains("[tagDec]")){
			if(detectPt == null)
				return;
			//0标记1图片名2标签3标签序号4可信度5xMin6yMin7xMax8yMax
			String[] a = r.split(",");
			ImgDetect annotation = new ImgDetect();
			annotation.setPtId(detectPt.getId());
			annotation.setDirectory(detectDir);
			annotation.setName(a[1]);
			annotation.setTag(a[2]);
			annotation.setTagIndex(Integer.parseInt(a[3]));
			annotation.setConfidence(Float.parseFloat(a[4]));
			annotation.setPosMinMax(a[5],a[6],a[7],a[8]);
			bs.daoSave(annotation);

		}else if(r.contains("[tagSeg]")){
			if(detectPt == null)
				return;
			//[tagSeg],2024.jpg,dhf,0,0.957,0.4890625 0.46666667 0.4875 0.46944445 0.4875 0.98888886...
			String[] a = r.split(",");
			ImgDetect annotation = new ImgDetect();
			annotation.setPtId(detectPt.getId());
			annotation.setDirectory(detectDir);
			annotation.setName(a[1]);
			annotation.setTag(a[2]);
			annotation.setTagIndex(Integer.parseInt(a[3]));
			annotation.setConfidence(Float.parseFloat(a[4]));
			annotation.setPosition(a[5].replaceAll(" ",","));
			bs.daoSave(annotation);

		}else if(r.contains("[tagClassify]")){
			if(detectPt == null)
				return;
			//[tagClassify];person-240621012#C_549614.jpg;224x224;reflection:0.41,skin:0.25,fall:0.25,helmet:0.08,smoke:0.00
			String[] a = r.split(";");
			for(String a1 : a[3].split(",")){
				String[] a2 = a1.split(":");
				float confidence = Float.parseFloat(a2[1]);
				if(confidence > 0.2){
					ImgDetect annotation = new ImgDetect();
					annotation.setPtId(detectPt.getId());
					annotation.setDirectory(detectDir);
					annotation.setName(a[1]);
					annotation.setTag(a2[0]);
					annotation.setConfidence(confidence);
					annotation.setPosition("0,0,1,1");
					bs.daoSave(annotation);
				}
			}

		}else if(r.contains("[终止]") || r.contains("[error]")){
			log.info(r);
			countDownLatch.countDown();
			new Thread(()->{
				Lang.sleep(1000);
				isRun = false;
				train();
			}).start();
		}

	}

	@AoReflect("标签导入")
	public DataRes tagImport(Long id, int type, String targetDir, Long groupId, String dir){
		List<ImgAnnotation> list = new ArrayList<>();
		if(type == 0){
			list = importYolo(id,targetDir,dir,groupId);
		}else if(type == 1){
			list = importVottCsv(id,targetDir,dir,groupId);
		}else if(type == 2){
			list = importMyCsv(id,targetDir,dir,groupId);
		}
		return DataRes.success("导入标签 "+list.size());
	}

	public void expTagMy(Long id,Long[] groupId,Long[] tagId,String dir){
		StringBuilder sb = new StringBuilder();
		sb.append("image,group,label,labelName,left,top,width,height");
		Map<Long,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getId);
		Map<Long, ImgGroup> groupMap = bs.query(ImgGroup.class, Cnd.where("pid","=",id)).stream().collect(Collectors.toMap(ImgGroup::getId, v->v));
		List<ImgAnnotation> annotations = getAnno(id,groupId,tagId,dir,null);
		annotations.forEach(v->{
			ImgTag t = tagMap.get(v.getTagId());
			ImgGroup g = groupMap.get(v.getGroupId());
			if(t != null)
				sb.append("\n").append(v.getName()).append(",")
						.append(g == null ? v.getGroupId() : g.getName()).append(",")
						.append(t.getCode()).append(",").append(t.getName()).append(",")
						.append(v.getPosition());
		});
		String pathName = proPath(id)+"/export/imgAnno.csv";
		Files.write(pathName,sb);
	}

	public List<ImgAnnotation> getAnno(Long id,Long[] groupId,Long[] tagId,String dir,String names){
		return bs.query(ImgAnnotation.class,cndAnno(id,groupId,tagId,dir,names));
	}

	public Map<String, Integer> getAnnoImg(Long id, Long[] groupId, Long[] tagId, String dir){
		Cnd cnd = cndAnno(id,groupId,tagId,dir,null);
		cnd.groupBy("name");
		List<NutMap> list = bs.querySql("select name,count(name) tag_count from Img_Annotation",cnd);
		Map<String,Integer> map = list.stream().collect(
				Collectors.toMap(v->v.getString("name"),v->v.getInt("tagCount"))
		);
		return map;
	}

	public List<NutMap> getAnnoStatistics(Long id,Long[] groupId,Long[] tagId,String dir){
		Cnd cnd = cndAnno(id,groupId,tagId,dir,null);
		cnd.groupBy("tag_id");
		return bs.querySql("select tag_id,count(tag_id) count from Img_Annotation",cnd);
	}

	@AoReflect("标签导出")
	public String tagExport(@AoReflect(type = AstEnum.param) ImgAnnoTrain imgTrain, String targetDir){
		Long id = imgTrain.getProId();
		String proPath = proPath(id);
		String expPath = Strings.isBlank(targetDir) ? expPath(id) : (proPath + "/" + targetDir + "/export_" + Times.format("yyMMdd_hhmmss",new Date()));

		//存在没有选标签的情况
		Map<Long,ImgTag> tagMap = getTagMap(id,imgTrain.getDir(),ImgTag::getId);

		if(imgTrain.getTagId() == null && imgTrain.getTagCode() != null){
			List<ImgTag> tags =  bs.query(ImgTag.class,Cnd.where("pid","=",imgTrain.getProId()).and("dir","=",imgTrain.getDir()).and("code","in",imgTrain.getTagCode()));
			imgTrain.setTagId(tags.stream().map(ImgTag::getId).toArray(Long[]::new));
		}
		List<ImgAnnotation> annotations = getAnno(id,imgTrain.getGroupId(),imgTrain.getTagId(),imgTrain.getDir(),null);
		annotations = annotations.stream().filter(v->{
			ImgTag tag = tagMap.get(v.getTagId());
			if(tag == null)
				return false;
			if("[mask]".equals(tag.getCode())){
				v.setType(-1);
				return true;
			}

			if(imgTrain.getModel() == 3 || imgTrain.getModel() == 4 || imgTrain.getModel() == 5)
				return v.getType() == 0;

			return v.getType() == imgTrain.getModel();
		}).collect(Collectors.toList());

		List<ImgAnnotation> annoTag = annotations.stream().filter(v->v.getType()!= -1)
				.collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ImgAnnotation::getTagId))), ArrayList::new));

		String valPath = (imgTrain.getValDir() != null || imgTrain.getValPercent() != null) ? "val" : "train";
		String con = "path: "+expPath+"  \n" +
				"train: train  \n" +
				"val: "+valPath+" \n" +
				"test: test  \n" +
				"names:\n";

		List<ImgTag> tags = new ArrayList<>();
		for(int i=0;i<annoTag.size();i++){
			ImgTag imgTag = tagMap.get(annoTag.get(i).getTagId());
			imgTag.setSequence(i);
			con += " " +i+": "+imgTag.getCode()+"\n";
			tags.add(imgTag);
		}
		imgTrain.setTags(tags);


		if(imgTrain.getModel() == 3){
			float[] pos = annoTag.get(0).getPosFloat();
			int pointCount = (pos.length - 5)/3;
			int[] idx = new int[pointCount];
			for(int i = 0;i< idx.length;i++){
				idx[i] = i;
			}

			con += "kpt_shape: ["+pointCount+", 3]\n";
			//水平反转增强
			con += "flip_idx: "+Arrays.toString(idx)+"\n";
		}

		File nameFile = new File(expPath + File.separator + "coco128.yaml");
		Files.write(nameFile,con);

		Map<String,List<ImgAnnotation>> annoNameMap = annotations.stream().collect(Collectors.groupingBy(ImgAnnotation::getName));
		File[] imgs = new File(proPath,imgTrain.getDir()).listFiles(v->v.isFile() && annoNameMap.containsKey(v.getName()));
		Float percent = imgTrain.getValPercent();
		//验证集
		if(percent != null){
			if(percent <= 0)
				percent = 0.2f;
			List<File> list = Arrays.asList(imgs);
			Collections.shuffle(list);
			int part1 = (int) (list.size() * percent);
			exportFile(list.subList(0,part1).toArray(new File[0]),expPath+"/"+valPath,annotations,tagMap,imgTrain);
			exportFile(list.subList(part1, list.size()).toArray(new File[0]),expPath+"/train",annotations,tagMap,imgTrain);
		}else{
			exportFile(imgs,expPath+"/train",annotations,tagMap,imgTrain);
		}

		if(imgTrain.getValDir() != null){
			List<ImgAnnotation> annotations2 = getAnno(id,imgTrain.getGroupId(),imgTrain.getTagId(),imgTrain.getValDir(),null);
			imgs = new File(proPath,imgTrain.getValDir()).listFiles(v->v.isFile() && FileUtil.isSuffix(v.getName(), String.valueOf(sufArr)));
			exportFile(imgs,expPath+"/"+valPath,annotations2,tagMap,imgTrain);
		}

		return expPath;
	}

	public void exportFile(File[] imgs, String expPath, List<ImgAnnotation> list, Map<Long,ImgTag> tagMap, ImgAnnoTrain imgTrain){
		Files.clearDir(new File(expPath));
		String imgDirs = expPath  +  "/images";
		Files.createDirIfNoExists(imgDirs);
		File labDir = Files.createDirIfNoExists(expPath + "/labels");

		Map<String,List<ImgAnnotation>> listMap = list.stream().collect(Collectors.groupingBy(ImgAnnotation::getName));

		for (File imgFile : imgs) {
			String imgName = imgFile.getName();
			List<ImgAnnotation> v = listMap.get(imgName);
			if(v == null)
				continue;
			try {
				List<ImgAnnotation> maskAnno = v.stream().filter(v2->v2.getType() == -1).collect(Collectors.toList());
				if(maskAnno.size() == v.size())
					continue;
				if(trainIng != null)
					trainIng.imgCountInc();
				File toFile = new File(imgDirs+"/"+imgName);
				int width = 0; int height = 0;
				if(imgTrain.getModel() == 5){
					Mat mat = OpenCVUtil.readImg(imgFile);
					width = mat.width();
					height = mat.height();
					Mat mat2 = OpenCVUtil.resizeToTargetHeight(mat,64);
					Mat mat3 = OpenCVUtil.fillToSquare(mat2,640);
					OpenCVUtil.writeImg(mat3,toFile);
					OpenCVUtil.release(mat,mat2,mat3);
				}else{
					if(maskAnno.size() > 0){
						BufferedImage bi = imgMask(imgFile,maskAnno);
						width = bi.getWidth();
						height = bi.getHeight();
						ImageIO.write(bi, "jpg", toFile);
					}else{
						Files.copyFile(imgFile,toFile);
					}
				}


				String fName = imgName.substring(0,imgName.lastIndexOf("."));
				File tagFile = new File(labDir.getAbsolutePath()+File.separator+fName+".txt");
				String p = "";
				//Mat mat = OpenCVUtil.readImg(toFile);
				for(ImgAnnotation ia : v){
					if(ia.getType() == -1)
						continue;
					if(trainIng != null)
						trainIng.labelCountInc();
					int seq = tagMap.get(ia.getTagId()).getSequence().intValue();
					p += seq + " ";
					if(imgTrain.getModel() == 3){
						String[] pos = ia.getPosition().split(",");
						String vis = "";
						for(int i=0;i<(pos.length-5)/3;i++){
							int j = i * 3 + 5;
							if("0".equals(pos[j+2]))
								vis += " 0.0 0.0 0";
							else
								vis += " " + pos[j] + " " + pos[j+1] + " " + pos[j+2];
						}
						p += ia.getPosYolo() + vis;
					}else if(imgTrain.getModel() == 4){
						float[] f = ia.getPosFloat();
						float x = f[0];
						float y = f[1];
						float w = f[2];
						float h = f[3];
						float[][] points = {
								{x, y}, // 左上角
								{x + w, y}, // 右上角
								{x + w, y + h}, // 右下角
								{x, y + h}  // 左下角
						};
						float[] c = {x + w/2,y + h/2};
						float[][] b = MathUtil.pointRotate(points,c,f[4]);
						for(float[] d : b){
							p += d[0] + " " + d[1] + " ";
						}
					}else if(imgTrain.getModel() == 5){
						float[] f = ia.getPosFloat();
						float c = 64f / height;
						float x = f[0] * c * width  / 640;
						float y = f[1] * c * height / 640;
						float w = f[2] * c * width  / 640;
						float h = f[3] * c * height / 640;
						p +=  (x + w/2) + " " + (y + h/2) + " " + w + " " + h;
						//OpenCVUtil.rectangle(mat,x,y,x+w,y+h,null,1);
					}else{
						p += ia.getPosYolo();
					}
					p +="\n";
				}
				//OpenCVUtil.writeImg(mat,new File(toFile.getAbsolutePath()+".png"));

				Files.write(tagFile,p);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public BufferedImage imgMask(File img,List<ImgAnnotation> annos) throws IOException {
		BufferedImage bi = ImageIO.read(img);
		Graphics2D g2d = bi.createGraphics();
		for(ImgAnnotation ia : annos){
			g2d.setColor(Color.decode("#000000"));
			ImgAnnotation.Pos pos = ia.getPos();
			g2d.fillRect((int) (pos.x*bi.getWidth()), (int) (pos.y*bi.getHeight()),(int) (pos.w*bi.getWidth()),(int) (pos.h*bi.getHeight()));
		}
		g2d.dispose();
		return bi;
	}

	public String runExec(Long id){
		if(isRun)
			throw Lang.makeThrow("当前有任务正在执行");
		return proPath(id);
	}

	/** 目标检测
	 *--weights,	type=str, default=ROOT / 'yolov5s.pt', help='initial weights path')
	 *--cfg,		type=str, default='', help='model.yaml path')
	 *--data,		type=str, default=ROOT / 'data/coco128.yaml', help='dataset.yaml path')
	 *--hyp,		type=str, default=ROOT / 'data/hyps/hyp.scratch-low.yaml', help='hyperparameters path')
	 *--epochs,		type=int, default=300, help='total training epochs')
	 *--batch-size,	type=int, default=-1, help='total batch size for all GPUs, -1 for autobatch')
	 *--imgsz,		'--img', '--img-size', type=int, default=640, help='train, val image size (pixels)')
	 *--rect,		action='store_true', help='rectangular training')
	 *--resume,		nargs='?', const=True, default=False, help='resume most recent training')
	 *--nosave,		action='store_true', help='only save final checkpoint')
	 *--noval,		action='store_true', help='only validate final epoch')
	 *--noautoanchor, action='store_true', help='disable AutoAnchor')
	 *--noplots,	action='store_true', help='save no plot files')
	 *--evolve,		type=int, nargs='?', const=300, help='evolve hyperparameters for x generations')
	 *--bucket,		type=str, default='', help='gsutil bucket')
	 *--cache,		type=str, nargs='?', const='ram', help='image --cache ram/disk')
	 *--image-weights, action='store_true', help='use weighted image selection for training')
	 *--device,		default='', help='cuda device, i.e. 0 or 0,1,2,3 or cpu')
	 *--multi-scale,action='store_true', help='vary img-size +/- 50%%')
	 *--single-cls,	action='store_true', help='train multi-class data as single-class')
	 *--optimizer,	type=str, choices=['SGD', 'Adam', 'AdamW'], default='SGD', help='optimizer')
	 *--sync-bn,	action='store_true', help='use SyncBatchNorm, only available in DDP mode')
	 *--workers,	type=int, default=8, help='max dataloader workers (per RANK in DDP mode)')
	 *--project,	default=ROOT / 'runs/train', help='save to project/name')
	 *--name,		default='exp', help='save to project/name')
	 *--exist-ok,	action='store_true', help='existing project/name ok, do not increment')
	 *--quad,		action='store_true', help='quad dataloader')
	 *--cos-lr,		action='store_true', help='cosine LR scheduler')
	 *--label-smoothing, type=float, default=0.0, help='Label smoothing epsilon')
	 *--patience,	type=int, default=100, help='EarlyStopping patience (epochs without improvement)')
	 *--freeze,		nargs='+', type=int, default=[0], help='Freeze layers: backbone=10, first3=0 1 2')
	 *--save-period,type=int, default=-1, help='Save checkpoint every x epochs (disabled if < 1)')
	 *--seed,		type=int, default=0, help='Global training seed')
	 *--local_rank,	type=int, default=-1, help='Automatic DDP Multi-GPU argument, do not modify')
	 */
	@AoReflect(value = "执行训练")
	public void train(){
		if(isRun)
			return;
		trainIng = trainList.poll();
		if(trainIng == null)
			return;

		trainIng.setStartTime(System.currentTimeMillis());
		if(trainIng.getModel() == 2){
			trainClassify();
		}else{
			if(trainIng.getYaml() == null){
				String expPath = tagExport(trainIng,null);
				trainIng.setYaml(expPath + "/coco128.yaml");
			}
			if(trainIng.getModel() == 1)
				trainSeg();
			else if(trainIng.getModel() == 3)
				trainPose();
			else if(trainIng.getModel() == 4)
				trainOBB();
			else
				trainDet();
		}
		log.info("开始训练 model:"+trainIng.getModel() + " "+trainIng.getYaml());
		isRun = true;
	}

	@AoReflect(value = "训练-目标检测",type = AstEnum.command)
	public void trainDet(){
		exec(trainIng.renderYoloArg() + " --data " + trainIng.getYaml());
	}

	@AoReflect(value = "训练-语义分割",type = AstEnum.command)
	public void trainSeg(){
		exec(trainIng.renderYoloArg() + " --data " + trainIng.getYaml());
	}

	/**
	 * --model,		type=str, default='yolov5s-cls.pt', help='initial weights path')
	 * --data,		type=str, default='imagenette160', help='cifar10, cifar100, mnist, imagenet, ...')
	 * --epochs,	type=int, default=10, help='total training epochs')
	 * --batch-size,type=int, default=64, help='total batch size for all GPUs')
	 * --imgsz,		'--img', '--img-size', type=int, default=224, help='train, val image size (pixels)')
	 * --nosave,	action='store_true', help='only save final checkpoint')
	 * --cache,		type=str, nargs='?', const='ram', help='--cache images in "ram" (default) or "disk"')
	 * --device,	default='5', help='cuda device, i.e. 0 or 0,1,2,3 or cpu')
	 * --workers,	type=int, default=8, help='max dataloader workers (per RANK in DDP mode)')
	 * --project,	default=ROOT / 'runs/train-cls', help='save to project/name')
	 * --name,		default='exp', help='save to project/name')
	 * --exist-ok,	action='store_true', help='existing project/name ok, do not increment')
	 * --pretrained,nargs='?', const=True, default=True, help='start from i.e. --pretrained False')
	 * --optimizer,	choices=['SGD', 'Adam', 'AdamW', 'RMSProp'], default='Adam', help='optimizer')
	 * --lr0,		type=float, default=0.001, help='initial learning rate')
	 * --decay,		type=float, default=5e-5, help='weight decay')
	 * --label-smoothing, type=float, default=0.1, help='Label smoothing epsilon')
	 * --cutoff,	type=int, default=None, help='Model layer cutoff index for Classify() head')
	 * --dropout,	type=float, default=None, help='Dropout (fraction)')
	 * --verbose,	action='store_true', help='Verbose mode')
	 * --seed,		type=int, default=0, help='Global training seed')
	 * --local_rank,type=int, default=-1, help='Automatic DDP Multi-GPU argument, do not modify')
	 */
	@AoReflect(value = "训练-分类",type = AstEnum.command)
	public void trainClassify(){
		String expPath = expPath(trainIng.getProId()) + "/classify";
		File classifyDir = Files.createDirIfNoExists(expPath);
		Files.clearDir(classifyDir);

		String proPath = proPath(trainIng.getProId());
		String srcPath = proPath + trainIng.getClassifyDir();
		log.info("分类训练-图像目录:"+srcPath);
		File[] dirs = new File(srcPath).listFiles((v,k)->v.isDirectory());
		String tagStr = "[分类] ";
		int count = 0;
		for(File dir:dirs){
			if(dir.getName().contains("@"))
				continue;

			File[] imgs = dir.listFiles();
			if(imgs == null || imgs.length == 0)
				continue;

			int i = imgs.length;
			count +=i;
			tagStr += dir.getName()+"("+i+") ";

			File pTrain = Files.createDirIfNoExists(expPath + "/train/" + dir.getName());
			File pVal = Files.createDirIfNoExists(expPath + "/val/" + dir.getName());
			File pTest = Files.createDirIfNoExists(expPath + "/test/" + dir.getName());
			List<File> list = Arrays.asList(imgs);
			Collections.shuffle(list); // 直接洗牌原数组视图
			int part1 = (7 * list.size()) / 10;
			int part2 = (9 * list.size()) / 10;
			for(int j = 0;j<list.size();j++){
				File f = list.get(j);
				if(!isImg(f.getName()))
					continue;
				String toDir = pTrain.getAbsolutePath();
				if(j > part1)
					toDir = pVal.getAbsolutePath();
				if(j > part2)
					toDir = pTest.getAbsolutePath();
				try {
					Files.copyFile(f,new File(toDir,f.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		if(trainIng.getImgsz() == null){
			trainIng.setImgsz(224);
		}
		trainIng.setImgCount(count);
		trainIng.setLabelCount(count);
		trainIng.setTagsStr(tagStr);

		exec(trainIng.renderYoloArg() + " --data " + expPath);

		trainIng.setYaml(expPath);
	}
	@AoReflect(value = "训练-Pose",type = AstEnum.command)
	public void trainPose(){
		exec(trainIng.renderYoloArg() + " --data " + trainIng.getYaml());
	}
	@AoReflect(value = "训练-OBB",type = AstEnum.command)
	public void trainOBB(){
		exec(trainIng.renderYoloArg() + " --data " + trainIng.getYaml());
	}

	public DataRes trainPro(Long id, String trainFile, @AoReflect(type = AstEnum.param) ImgAnnoTrain it) throws IOException {

		String proPath = proPath(id);
		it.setProId(id);
		String valDir = it.getValDir();
		if(Strings.isNotBlank(valDir))
			it.setValDir((valDir.startsWith("/") ? "" : "/")+valDir);
		if(Strings.isNotBlank(trainFile)){
			if(FileUtil.isSuffix(trainFile,"yaml"))
				it.setYaml(proPath + "/"+trainFile);
		}

		train();
		if(it.equals(trainIng))
			return DataRes.error("该任务正在训练中... 进度:"+trainIng.getTrainProgress());
		if(trainList.contains(it))
			return DataRes.error("已有相同的任务正在等待执行");

		trainList.add(it);
		if(isRun)
			return DataRes.success("任务已添加,之前还有"+trainList.size()+"个训练任务");

		train();
		return DataRes.success("即将进行训练");
	}

	public File trainImgDir(String dir, ImgAnnoTrain it){

		File imgDir = new File(dir);
		File parentDir = new File(imgDir.getParent());
		String labelPath = parentDir.getAbsolutePath()+"/labels";
		Files.makeDir(new File(labelPath));

		String con = "path: "+parentDir.getParent()+"  \n" +
				"train: "+parentDir.getName()+"  \n" +
				"val: "+parentDir.getName()+" \n" +
				"test: test  \n" +
				"names:\n";


		String[] names = imgDir.list();
		Map<String,Integer> tagMap = new HashMap<>();
		String tagStr = "[标签] ";
		int i = 0;
		for(String n : names){
			String tagName = n.split("-")[0];
			Integer tagIndex = tagMap.get(tagName);
			if(tagIndex == null){
				con += " " +i+": "+tagName+"\n";
				tagMap.put(tagName,i);
				tagIndex = i++;
				tagStr += tagName + ",";
			}
			String imgName = n.substring(0,n.lastIndexOf("."));
			File tagFile = new File(labelPath+"/"+imgName+".txt");
			Files.write(tagFile,tagIndex + " " + "0.5 0.5 1 1");
		}
		it.setTagsStr(tagStr.substring(0,tagStr.length()-1));
		File yamlFile = new File(parentDir.getAbsolutePath() + File.separator + "coco128.yaml");
		Files.write(yamlFile,con);
		it.setYaml(yamlFile.getAbsolutePath());
		return yamlFile;
	}

	@AoReflect(value = "验证-目标",type = AstEnum.command)
	public void detect(int type,Long id,String dir,String ptName,String source,Integer imgsz){
		detectProId = id;
		String proPath = runExec(id);
		if(!ptName.endsWith(".pt"))
			ptName += "/weights/best.pt";
		File ptFile = new File(proPath+"/"+ptName) ;
		source = proPath + "/" + Strings.sBlank(source,"test");
		detectDir = source.substring(source.lastIndexOf("/")+1);
		detectPt = bs.daoFetch(TFile.class,Cnd.where("md5","=",Lang.md5(ptFile)));
		if(detectPt != null){
			bs.daoClear(ImgDetect.class,Cnd.where("ptId","=",detectPt.getId()).and("directory","=",detectDir));
		}
		String param = " --project "+ptFile.getParentFile().getParent() + " --weights "+ ptFile.getAbsolutePath() + " --source "+source;
		if(imgsz != null)
			param += " --imgsz "+imgsz;
		if(type == 0)
			exec(param);
		else if(type == 1)
			predict(param);
		else if(type == 2)
			verifySeg(param);
		else
			verifyObb(param);
		isRun = true;
	}

	@AoReflect(value = "验证-分类",type = AstEnum.command)
	public void predict(String param){
		exec(param);
	}

	@AoReflect(value = "验证-语义分割",type = AstEnum.command)
	public void verifySeg(String param){
		exec(param);
	}

	@AoReflect(value = "验证-旋转框",type = AstEnum.command)
	public void verifyObb(String param){
		exec(param);
	}

	@AoReflect(value = "自动标注",type = AstEnum.command)
	public synchronized String autoLabel(Long id,String dir,String names,Long groupId,Long[] tagId,String ptName){
		String proPath = proPath(id);
		Map<String,ImgTag> tagMap = tagId == null ? getTagMap(id,dir,ImgTag::getCode) : getTagMap(tagId);
		File modelFile = new File(proPath,ptName);
		if(modelFile.isDirectory())
			modelFile = new File(modelFile.getAbsolutePath(),"weights/best.onnx");
		onnx.setModelFile(modelFile);

		List<ImgAnnotation> annotations = new ArrayList<>();
		String[] imgArr = names.split("/");
		for (String name : imgArr){
			File imgFile = new File(proPath+dir,name);
			List<Target> a =  onnx.recognize(imgFile,null).getTargets();
			for(Target b : a){
				ImgTag tag = tagMap.get(b.getLabel());
				if(tag != null){
					ImgAnnotation annotation = new ImgAnnotation(id,dir,name,tag.getId(),groupId == null ? -2L : groupId);
					annotation.setConfidence(b.getConfidence());
					String pos = b.getLeft()+","+b.getTop()+","+b.getWidth()+","+b.getHeight();
					if(b.getAngel() != 0)
						pos += "," + b.getAngel();
					annotation.setPosition(pos);
					annotations.add(annotation);
				}
			}
		}
		bs.fastInsert(annotations);
		return "图片：" + imgArr.length + " 标签："+annotations.size();

		/*Map<Long,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getId);
		String expPath = expPath(id)+"/detect";
		Files.clearDir(Files.createDirIfNoExists(expPath));
		for (String name : names.split("/")){
			List<ImgAnnotation> list = getAnno(id,null,tagId,dir,name).stream().filter(v->{
				ImgTag it = tagMap.get(v.getTagId());
				return "[mask]".equals(it.getCode());
			}).collect(Collectors.toList());
			File f1 = new File(proPath+dir+"/"+name);
			File toFile = new File(expPath+"/"+name);
			if(list.size() > 0){
				BufferedImage bi = imgMask(f1,list);
				ImageIO.write(bi, "jpg", toFile);
			}else{
				Files.copyFile(f1,toFile);
			}
		}

		if(!ptName.endsWith(".pt"))
			ptName += "/weights/best.pt";
		String param = " --weights "+proPath+"/"+ptName;
		param += " --source "+expPath;

		exec(param);
		isRun = true;*/
	}

	@AoReflect(value="发送->",type=AstEnum.command)
	public void sendCmd(String text){
		exec(text);
	}

	@AoReflect(value="PT转ONNX",type=AstEnum.command)
	public File ptToOnnx(File pt){
		if(imgDev2 != null)
			return imgDev2.ptToOnnx(pt);
		String ptPath = pt.getAbsolutePath();
		File onnx = new File(ptPath.replace(".pt",".onnx"));
		if(onnx.isFile())
			return onnx;

		//[分类]
		TFile tfile = FileUtil.fileInfo(pt);
		String imgsz = "640";
		if(tfile.getDescription() != null){
			String des = tfile.getDescription();
			Matcher matcher = Pattern.compile("大小:(\\d+)").matcher(des);
			if(matcher.find()){
				imgsz = matcher.group(1);
			}else if(des.contains("[分类]")){
				imgsz = "224";
			}


			matcher = Pattern.compile("produce:(\\S+)").matcher(des);
			if(matcher.find()){
				String produce = matcher.group(1);
				if(produce.contains("YOLO11"))
					return yolo11ToOnnx(pt);
			}
		}

		countDownLatch = new CountDownLatch(1);
		exec(ptPath,imgsz);
		try {
			countDownLatch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(onnx.isFile())
			return onnx;
		return null;
	}

	@AoReflect(value="YOLO11转ONNX",type=AstEnum.command)
	public File yolo11ToOnnx(File pt){
		countDownLatch = new CountDownLatch(1);
		exec(pt.getAbsolutePath());
		try {
			countDownLatch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new File(pt.getAbsolutePath().replace(".pt",".onnx"));
	}

	//旋转的图不在该项目中，应该清理旋转图像所在项目的缓存
	@AoReflect(value = "图像旋转")
	public void rotate(Long id,String dir,String names,int type,float start,float step,int size) throws IOException {
		String proPath = proPath(id);
		String proPath2 = proPath + dir;
		String cropPath = proPath2 + "/generate";
		Files.createDirIfNoExists(new File(cropPath));
		for(String n : names.split("/")){
			String imgPath = proPath2+ File.separator + n;
			if(type == 0){
				int a = n.lastIndexOf(".");
				String name = n.substring(0,a);
				String suffix = n.substring(a+1);

				File srcIm = new File(imgPath);
				for(int i = 0;i<size;i++){
					float degree = start + i * step;
					BufferedImage im2 = imgRotate(ImageIO.read(srcIm), degree);
					ImageIO.write(im2,suffix,new File(cropPath+"/"+name+"-R"+(int)(10*degree)+"."+suffix));
				}
			}else{
				int[] degree = {0,90,180,270};
				Images.rotate(imgPath,imgPath,degree[type]);
				List<ImgAnnotation> annotations = bs.query(ImgAnnotation.class,cndAnno(id,null,null,dir,n));
				for (ImgAnnotation a : annotations){
					String[] p = a.getPosition().split(",");
					float x = Float.parseFloat(p[0]);
					float y = Float.parseFloat(p[1]);
					float w = Float.parseFloat(p[2]);
					float h = Float.parseFloat(p[3]);
					if(type == 1){
						float left = 1 - y - h;
						float top = x;
						a.setPosition(left+","+top+","+h+","+w);
					}else if(type == 2){
						float left = 1 - x - w;
						float top = 1 - y - h;
						a.setPosition(left+","+top+","+w+","+h);
					}else if(type == 3){
						float left = y;
						float top = 1-x-w;
						a.setPosition(left+","+top+","+h+","+w);
					}
					bs.daoSave(a);
				}
			}

		}

	}

	//切的图不在该项目中，应该清理切图所在项目的缓存
	@AoReflect(value = "切图")
	public DataRes crop(Long id, String dir, String toDir, Long[] groupId, Long[] tagId,String names,int col,int row){
		String proPath = proPath(id);
		String imgPath = proPath + dir;
		String cropPath = proPath + "/" + Strings.sBlank(toDir, ImgAnnoPath.generate.name());
		Files.createDirIfNoExists(cropPath);
		int c = 0;

		if(row > 0 && col > 0){
			for(String name : names.split("/")){
				int k = name.lastIndexOf(".");
				String suffix = name.substring(k);
				String n = name.substring(0,k);
				try {
					BufferedImage bi = ImageIO.read(new File(imgPath,name));
					int w = bi.getWidth()/col;
					int h = bi.getHeight()/row;
					for(int i = 0;i<row;i++){
						for(int j=0;j<col;j++){
							c++;
							File taIm = new File(cropPath +"/"+n+"-"+(i+1)+"-"+(j+1)+suffix);
							Images.clipScale(bi,taIm,new int[]{w*j,h*i},new int[]{w*j+w,h*i+h});
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			//标签切图
			List<ImgAnnotation> annotations = bs.query(ImgAnnotation.class,cndAnno(id,groupId,tagId,dir,names).asc("name"));
			Map<Long,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getId);
			String prevName = null;
			Mat srcIm = null;
			for(ImgAnnotation v : annotations){
				int i = v.getName().lastIndexOf(".");
				String name = v.getName().substring(0,i);
				String suffix = v.getName().substring(i);
				String tagCode = tagMap.get(v.getTagId()).getCode();
				String toPath = cropPath +"/"+tagCode;
				Files.createDirIfNoExists(toPath);
				File taIm = new File(toPath,name+"-C_"+v.getId()+suffix);
				try {
					if(prevName == null || !Strings.equals(prevName,v.getName())){
						OpenCVUtil.release(srcIm);
						srcIm = OpenCVUtil.readImg(new File(imgPath,v.getName()));
						prevName = v.getName();
					}
					ImgAnnotation.Pos pos = v.getPos();
					float w = pos.w*srcIm.width();
					float h = pos.h*srcIm.height();
					RotatedRect rotatedRect = new RotatedRect(
							new Point(pos.x*srcIm.width() + w/2, pos.y*srcIm.height()+h/2),// 中心点
							new Size(w, h),pos.r // 旋转角度（顺时针）
					);
					Mat m2 = OpenCVUtil.rotateCrop(srcIm,rotatedRect);
					OpenCVUtil.writeImg(m2,taIm);
					m2.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//System.out.println(name + " - " + suffix + " - " + tagMap.get(v.getTagId()).getName());
			}
			OpenCVUtil.release(srcIm);
			c = annotations.size();
		}

		log.info("切图("+c+")张 ->" + cropPath);
		return DataRes.success("切图生成在 ->"+cropPath.substring(Constants.HOME_PATH.length()) +" "+c+"张");

	}

	@AoReflect(value = "按文件名标注")
	public void nameToTag(Long id,String dir,Long[] groupId,String names){
		Map<String,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getCode);
		List<ImgAnnotation> list = new ArrayList<>();
		for(String name : names.split("/")){
			String tagCode = name.split("-")[0];
			ImgTag tag = tagMap.get(tagCode);
			if(tag != null){
				ImgAnnotation a = new ImgAnnotation(id,dir,name,tag.getId(),groupId[0]);
				a.setPosition("0,0,1,1");
				list.add(a);
			}
		}
		bs.fastInsert(list);
	}

	@AoReflect(value = "合并图像")
	public void stitchedImg(Long id,String dir,String toDir,Long groupId,int col,int row,int spacing,String names,String prefix){
		if(Strings.isBlank(toDir))
			throw Lang.makeThrow("请选择合并后的图像目录");

		prefix = Strings.sNull(prefix,new SimpleDateFormat("yyMMddHHmm").format(new Date()));
		String proPath = proPath(id);
		String imgPath = proPath + dir;
		String toPath = proPath + toDir;

		Map<String,ImgTag> tMap = getTagMap(id,dir,ImgTag::getCode);
		String[] name = names.split("/");
		File[] fs = new File[name.length];
		BufferedImage[] bufImgs = new BufferedImage[name.length];
		for(int i=0;i< name.length;i++){
			fs[i] = new File(imgPath+"/"+name[i]);
			try {
				bufImgs[i] = ImageIO.read(fs[i]);
			} catch (IOException e) {
				throw Lang.makeThrow(name[i] +" 加载异常："+e.getMessage());
			}
		}

		List<ImgAnnotation> annotations = new ArrayList<>();
		for(int g=0;g<fs.length/(col*row);g++) {
			float width = 0;
			float height = spacing * (row - 1);

			for (int i = 0; i < row; i++) {
				int w2 = spacing * (col - 1);
				int h2 = 0;
				for (int j = 0; j < col; j++) {
					int fi = g * col * row + col * i + j;
					if (fi < fs.length) {
						BufferedImage bf = bufImgs[fi];
						w2 += bf.getWidth();
						h2 = Math.max(h2, bf.getHeight());
					}

				}
				width = Math.max(width, w2);
				height += h2;
			}
			//System.out.println("----- width:" + width + " height:" + height);
			String gname = prefix+"#G"+ g + ".jpg";
			BufferedImage img = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = img.createGraphics();
			int h3 = 0;

			for (int i = 0; i < row; i++) {
				int w2 = 0;
				int h2 = 0;
				for (int j = 0; j < col; j++) {
					int fi = g * col * row + col * i + j;
					if (fi < fs.length) {
						BufferedImage bf = bufImgs[fi];
						g2d.drawImage(bf, w2, h3, null);

						String imgName = fs[fi].getName();
						ImgTag tag = tMap.get(imgName.split("-")[0]);
						if(tag != null){
							ImgAnnotation anno = new ImgAnnotation(id,toDir,gname,tag.getId(),groupId);
							anno.setPosition(w2/width+","+h3/height+","+bf.getWidth()/width+","+bf.getHeight()/height);
							annotations.add(anno);
						}else{
							List<ImgAnnotation> annos = getAnno(id,null,null,dir,imgName);
							for(ImgAnnotation annotation : annos){
								ImgAnnotation anno = new ImgAnnotation(id,toDir,gname,annotation.getTagId(),groupId);
								ImgAnnotation.Pos p = annotation.getPos();
								float left = (p.x*bf.getWidth() + w2)/width;
								float right = (p.y*bf.getHeight() + h3)/height;
								float width2 = p.w * bf.getWidth() / width;
								float height2 = p.h * bf.getHeight() / height;
								anno.setPosition(left+","+right+","+width2+","+height2);
								annotations.add(anno);
							}
						}
						w2 += bf.getWidth() + spacing;
						h2 = Math.max(h2, bf.getHeight());
					}
				}
				h3 += h2 + spacing;
			}
			g2d.dispose();
			File f = new File(toPath,gname);

			try {
				boolean b = ImageIO.write(img, "jpg", f);
				System.out.println("合并 " +b+ "："+f.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
				throw Lang.makeThrow(gname +" 合并异常："+e.getMessage());
			}

		}
		bs.fastInsert(annotations);
		//clean(toPid,new int[]{1},null);
	}

	//0移动 1复制
	@AoReflect("移动/复制标签")
	public void moveGroup(Long id,Long[] groupId, Long[] tagId,int type,String dir,String names,
						  Long toGroupId,Long toTagId,Long toPid,String toDir){
		Cnd cnd = cndAnno(id,groupId,tagId,dir,names);
		String proPath = proPath(id);
		//到项目
		if(toPid != null){

			for(String name : getName(cnd)){
				File src = new File(proPath + dir + "/"+name);
				File target = new File(proPath(toPid) + "/"+ name);
				try {
					if(type == 0){
						Files.move(src,target);
					}else if(type == 1){
						Files.copyFile(src,target);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}


			if(type == 0){
				bs.update(ImgAnnotation.class,Chain.make("pid",toPid),cnd);
			}else if(type == 1){
				List<ImgAnnotation> list = bs.query(ImgAnnotation.class,cnd);
				for(ImgAnnotation a : list){
					a.setId(null);
					a.setPid(toPid);
				}
				bs.fastInsert(list);
			}

		}else{
			if(type == 0){
				Chain chain = Chain.make("isRemoved",0);
				if(toGroupId != null)
					chain.add("groupId",toGroupId);
				if(toTagId != null)
					chain.add("tagId",toTagId);
				if(Strings.isNotBlank(toDir))
					chain.add("dir",toDir);
				bs.update(ImgAnnotation.class,chain,cnd);
			}else if(type == 1){
				List<ImgAnnotation> list = bs.query(ImgAnnotation.class,cnd);
				for(ImgAnnotation a : list){
					a.setId(null);
					a.setDir(dir);
					if(toGroupId != null)
						a.setGroupId(toGroupId);
					if(toTagId != null)
						a.setTagId(toTagId);
					if(Strings.isNotBlank(toDir)){
						a.setDir(toDir);
					}
				}
				bs.fastInsert(list);
			}

			if(Strings.isNotBlank(toDir)){
				for(String name : names.split("/")){
					File src = new File(proPath + dir +  "/"+name);
					File target = new File(proPath + "/"+toDir + "/"+name);
					try {
						if(type == 0)
							Files.move(src,target);
						else
							Files.copyFile(src,target);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

	}

	@AoReflect("重命名")
	public void reName(Long id,String dir,String names,String newName) throws IOException {
		String proPath = proPath(id);
		Files.rename(new File(proPath + dir,names),newName);
		bs.update(ImgAnnotation.class, Chain.make("name",newName), cndAnno(id,null,null,dir,names));
	}

	@AoReflect("删除图片")
	public DataRes delImg(Long id,String dir,String names) throws IOException {
		String proPath = proPath(id);
		int i = 0;
		for(String n : names.split("/")){
			if(Files.deleteFile(new File(proPath + dir,n)))
				i++;
		}
		int j = bs.daoClear(ImgAnnotation.class,cndAnno(id,null,null,dir,names));
		return DataRes.success("删除图片："+i+" 删除标签："+j);
	}

	@AoReflect("清理")
	public DataRes clean(Long id,String dir,int[] type,String names){
		String proPath = proPath(id);
		String imgPath = proPath + dir;
		String expPath = expPath(id);
		List<String> msg = new ArrayList<>();
		for(int t : type){
			if(t == 0){ //回收站
				int i = bs.daoClear(ImgAnnotation.class,cndAnno(id,new Long[]{-3L},null,dir,null));
				msg.add("清空回收站："+i);
			}else if(t == 1){ //训练图像缓存、缩略图
				File dir1 = ImgAnnoPath.thumbnail.file(imgPath);
				File dir2 = ImgAnnoPath.train.file(expPath);
				Files.clearDir(dir1);
				Files.clearDir(dir2);
				msg.add("缓存已清空");
			}else if(t == 2){//所选图片
				String[] ns = names.split("/");
				int i = 0;
				for(String n : ns){
					File f = new File(imgPath + "/" + n);
					if(f.isFile()){
						i++;
						f.delete();
					}
				}
				int j = bs.daoClear(ImgAnnotation.class,cndAnno(id,null,null,dir,names));
				msg.add("删除图片："+i+" 关联标签："+j);
			}else if(t == 3){
				String[] name = new File(imgPath).list();
				int i = bs.daoClear(ImgAnnotation.class,Cnd.where("pid","=",id).and("dir","=",dir).and("name","not in",name));
				msg.add("无图片标签："+i);
			}else if(t == 4){
				List<String> list = getPt(id);
				msg.add("更新后模型列表："+list.size());
			}
		}
		return DataRes.success(String.join("；",msg));

	}

	@AoReflect("重置运行状态")
	public void noRun(){
		isRun = false;
	}

	public Cnd cndAnno(Long id, Long[] groupId, Long[] tagId,String dir,String names){
		Cnd cnd = Cnd.where("pid","=",id);
		if(groupId != null && groupId.length > 0)
			cnd.and("group_Id","in",groupId);
		if(tagId != null && tagId.length>0)
			cnd.and("tag_Id","in",tagId);
		cnd.and("dir","=",Strings.sBlank(dir,"/"));
		if(Strings.isNotBlank(names))
			cnd.and("name","in",names.split("/"));
		return cnd;
	}

	public String basePath(){
		return Constants.HOME_PATH + File.separator + imgPath;
	}

	public String expPath(Long id){
		String basePath = PathEnum.image.p2()+"yolo";
		return basePath + File.separator + "p"+id;
	}

	public String proPath(Long id){
		return proPath(bs.query(ImgProject.class,id));
	}

	public String proPath(ImgProject imgProject){
		return (basePath() + imgProject.getPath()).replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
	}

	public Long[] proIds(Long id){
		return new Long[]{id};
	}

	public String[] getName(Cnd cnd){
		List<ImgAnnotation> tags = bs.query(ImgAnnotation.class,cnd.clone().groupBy("name"));
		return tags.stream().map(ImgAnnotation::getName).toArray(String[]::new);
	}

	public List<ImgTag> getTagList(Long pid,String dir){
		return bs.query(ImgTag.class, Cnd.where("pid","in",proIds(pid)).and("dir","in",new String[]{"/",dir}));
	}

	public <K> Map<K,ImgTag> getTagMap(Long pid,String dir, Function<ImgTag, ? extends K> keyMapper) {
		return getTagList(pid,dir).stream().collect(Collectors.toMap(keyMapper, v->v,(k1, k2) -> k1));
	}

	public Map<String,ImgTag> getTagMap(Long[] id) {
		List<ImgTag> list = bs.query(ImgTag.class, Cnd.where("id","in",id));
		return list.stream().collect(Collectors.toMap(ImgTag::getCode, v->v,(k1, k2) -> k1));
	}

	public List<ImgAnnotation> importYolo(Long id,String from,String dir,Long groupId){
		String propPath = proPath(id);
		String tagPath = propPath + from;//标签文件路径
		String[] imgList = new File(propPath+dir).list();
		Map<String,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getName);

		List<ImgAnnotation> annoList = new ArrayList<>();
		for (String imgName: imgList){
			int i1 = imgName.lastIndexOf(".");
			if(i1 == -1)
				continue;

			String suf = imgName.substring(i1+1).toLowerCase();
			String name = imgName.substring(0,i1);
			if(!Strings.isin(sufArr,suf))
				continue;
			File tagFile = new File(tagPath + File.separator + name + ".txt");
			if(!tagFile.isFile())
				continue;
			List<String> tagList = Files.readLines(tagFile);
			for (String s : tagList){
				String[] tag = s.split("\\s+");
				ImgTag imgTag = tagMap.computeIfAbsent(tag[0],v->{
					ImgTag t = new ImgTag();
					t.setPid(id);
					t.setCode(tag[0]);
					t.setName(tag[0]);
					t.setColor("#"+Strings.toHex(new Random().nextInt(0xffffff),6));
					bs.daoSave(t);
					return t;
				});
				ImgAnnotation annotation = new ImgAnnotation(id,dir,imgName,imgTag.getId(),groupId);
				if(tag.length > 6){
					String p = String.join(",",tag);
					annotation.setPosition(p.substring(p.indexOf(",")+1));
					annotation.setType(1);
				}else{
					float x = Float.parseFloat(tag[1]);
					float y = Float.parseFloat(tag[2]);
					float w = Float.parseFloat(tag[3]);
					float h = Float.parseFloat(tag[4]);
					annotation.setPosition((x - (w/2)) + "," + (y - (h/2) + "," + w + "," + h));
				}
				annoList.add(annotation);
				//System.out.println(annotation.getId() + ":" + imgName+" project:"+id+" tag:" + imgTag.getId()+"->"+pos);
			}
		}
		bs.fastInsert(annoList);
		return annoList;
	}

	public List<ImgAnnotation> importVottCsv(Long id,String from,String dir,Long groupId){
		String proPath = proPath(id);
		List<String> list = Files.readLines(new File(proPath + "/"+from));
		Map<String,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getName);
		Map<String,int[]> imgSize = new HashMap<>();
		List<ImgAnnotation> annoList = new ArrayList<>();
		for(int i=1;i<list.size();i++){
			String[] label = list.get(i).split(",");
			String imgName = label[0].replaceAll("\"","");
			File imgFile = new File(proPath+"/"+imgName);
			if(!imgFile.isFile())
				continue;

			int[] size = imgSize.computeIfAbsent(imgName,v->{
				try {
					BufferedImage img = ImageIO.read(imgFile);
					return new int[]{img.getWidth(),img.getHeight()};
				} catch (IOException e) {
					return  null;
				}
			});

			String tag = label[5].replaceAll("\"","");
			ImgTag imgTag = tagMap.computeIfAbsent(tag,v->{
				ImgTag t = new ImgTag();
				t.setPid(id);
				t.setCode(tag);
				t.setName(tag);
				t.setColor("#"+Strings.toHex(new Random().nextInt(0xffffff),6));
				bs.daoSave(t);
				return t;
			});

			float xmin = Float.parseFloat(label[1]);
			float ymin = Float.parseFloat(label[2]);
			float xmax = Float.parseFloat(label[3]);
			float ymax = Float.parseFloat(label[4]);
			String pos = (xmin / size[0]) + "," +(ymin /size[1]) + "," + ((xmax-xmin) / size[0]) + "," + ((ymax - ymin) /size[1]);
			ImgAnnotation annotation = new ImgAnnotation(id,dir, imgFile.getName(),imgTag.getId(),groupId);
			annotation.setPosition(pos);
			annoList.add(annotation);
		}
		bs.fastInsert(annoList);
		return annoList;
	}

	//name,tagCode,left,top,width,height
	public List<ImgAnnotation> importMyCsv(Long id,String from,String dir,Long groupId){
		String proPath = proPath(id);
		List<String> list = Files.readLines(new File(proPath + "/"+from));
		Map<String,ImgTag> tagMap = getTagMap(id,dir,ImgTag::getCode);

		List<ImgAnnotation> annoList = new ArrayList<>();
		for(int i=1;i<list.size();i++){
			String[] label = list.get(i).split(",");
			String imgName = label[0];

			String tag = label[1];
			ImgTag imgTag = tagMap.computeIfAbsent(tag,v->{
				ImgTag t = new ImgTag();
				t.setPid(id);
				t.setCode(tag);
				t.setName(tag);
				t.setColor("#"+Strings.toHex(new Random().nextInt(0xffffff),6));
				bs.daoSave(t);
				return t;
			});

			String pos = label[2] + "," +label[3] + "," + label[4] + "," + label[5];
			ImgAnnotation annotation = new ImgAnnotation(id,dir,imgName,imgTag.getId(),groupId);
			annotation.setPosition(pos);
			annoList.add(annotation);
		}
		bs.fastInsert(annoList);
		return annoList;
	}

	@AoReflect("更新模型列表")
	public List<String> getPt(Long id){
		String proPath = proPath(id);
		String ptPath = ImgAnnoPath.train.path(proPath);
		File[] files = Files.scanDirs(new File(ptPath));
		List<String> list = new ArrayList<>();
		for(File f : files){
			File[] pt = Files.files(f,"pt");
			if(pt != null)
				for(File f2 : pt){
					String ptName = f2.getAbsolutePath();
					if(ptName.contains("best.pt")){
						TFile tFile = FileUtil.fileInfo(f2);
						tFile.setPlass("IMGTAG"+id);
						bs.daoSave(tFile);
						list.add(ptName);
					}

				}
		}
		List<TFile> tFiles = bs.query(TFile.class,Cnd.where("plass","=","IMGTAG"+id));
		for(TFile tFile : tFiles){
			File file = FileUtil.toFile(tFile.getPathName());
			if(!file.isFile())
				bs.daoDel(tFile);
		}
		return list;
	}

	private String hm(){
		long t1 = System.currentTimeMillis() - trainIng.getStartTime();
		long m = t1 / 1000 / 60;//分钟
		if(m < 60)
			return m+"分";
		long s = m / 60;
		m = m % 60;
		return  s+"时"+m+"分";
	}

	public BufferedImage imgRotate(BufferedImage image, float degree) {
		int width = image.getWidth();// 原始图象的宽度
		int height = image.getHeight();// 原始图象的高度

		BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
		Graphics2D g2d = rotatedImage.createGraphics();

		// 设置抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 旋转10度
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(degree), width / 2, height / 2);
		g2d.transform(at);

		// 绘制原始图片到旋转后的图片上
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		return rotatedImage;
	}

	public ImgAnnoDevice getImgDev2() {
		return imgDev2;
	}

	@AoReflect("获取CSV标注")
	public List<ImgDetect> getCsvPos(String imgName,File csvFile){
		if(csvFile == null)
			throw Lang.makeThrow("未选择CSV标注文件");
		List<String> sss = Files.readLines(csvFile);
		List<ImgDetect> list = new ArrayList<>();
		for(String ss : sss){
			String[] s = ss.split(",");
			if(s[0].equals(imgName)){
				ImgDetect det = new ImgDetect();
				det.setTag(s[1]);
				det.setPosition(s[2]+","+s[3]+","+s[4]+","+s[5]);
				list.add(det);
			}
		}
		return list;
	}

	public boolean isImg(String name){
		int i1 = name.lastIndexOf(".");
		if(i1 == -1)
			return false;
		return Strings.isin(sufArr,name.substring(i1+1).toLowerCase());
	}

	public TFile delPt(Long id){
		TFile tFile = bs.query(TFile.class,id);
		String path = tFile.getPathName().split("/weights/")[0];
		bs.daoDel(TFile.class,id);
		Files.deleteDir(new File(Constants.HOME_PATH,path));
		return tFile;
	}

	@Override
	public List<RMenuOption> menuList() {
		List<RMenuOption> list = new ArrayList<>();
		list.add(buildMenu("转换为onnx","pt2onnx","pt"));
		list.add(buildMenu("项目","/base/app/imgAnno/imgProject","DEV"));
		return list;
	}

	@Override
	public Object menuClick(String menu,String val){
		File file = FileUtil.toFile(val);
		if("pt2onnx".equals(menu)){
			return ptToOnnx(file);
		}
		return null;
	}

	public String getImgPath() {
		return imgPath;
	}

}
