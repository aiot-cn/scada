package org.aiot.util;

import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.ServletEnum;
import org.aiot.model.enums.VarRuntimeEnum;
import org.aiot.model.project.ArgBean;
import org.aiot.model.project.License;
import org.aiot.model.project.MethodBean;
import org.aiot.model.table.SysScript;
import org.aiot.service.BaseService;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.repo.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.Invocable;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SysUtil {
	
	public static String runExec(int wait,String... cmd){
		String line = null;
        try{ 
        	String[] envp = {"path="+System.getenv("path")};//环境变量
        	 //执行命令    
        	Process p = Runtime.getRuntime().exec(cmd,envp);
        	if(wait > 0){
        		//取得命令结果的输出流    
        		p.waitFor(wait, TimeUnit.SECONDS);
        		InputStream stream = p.getInputStream();
        		byte[] bytes = new byte[stream.available()];
				stream.read(bytes);
        		line = new String(bytes,Lang.isWin()?"GBK":"UTF-8");
        	}                   
        } catch (Exception e) { 
            line = "Error executing : "+e.getMessage();
        } 
        return line;
	}
	
	public static String runExec(String cmd){
		String[] s = Lang.isWin() ? new String[]{"cmd","/c",cmd} : new String[]{"/bin/sh", "-c",cmd};
        return runExec(3, s);
	}
	
	public static String getHDSerial() {
		String serial = Lang.isWin() ? getHDSerialWin() : getHDSerialLinux();;
		if(Strings.isBlank(serial)){
			try {
				serial = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				serial = "NULL";
			}
			return serial;
		}
		serial = serial.replaceAll(" ","");

		return Lang.md5(serial).toUpperCase().replaceAll("(.{4})", "$1-").substring(0,24);
	}

	/**
	 * windows cmd 格式
	 *SerialNumber=LSL70240B06GF03369
	 *
	 *SerialNumber=            W1D3LSZ0
	 *
	 * windows powershell 格式
	 * SerialNumber
	 * ------------
	 * 0025_384B_3142_E0B3.
	 * WD-WXJ2AC0R6KSS
	 *
	 */
	public static String getHDSerialWin(){
		String serial = runExec("wmic diskdrive get SerialNumber /value");
		if(serial.contains("SerialNumber")){
			Constants.diskCount = serial.split("SerialNumber").length - 1;
		}else{
			serial = runExec(3,"powershell","Get-PhysicalDisk | Select-Object SerialNumber");
			Constants.diskCount = serial.split("\n").length - 2;
		}

		return serial.trim();
	}

	/**
	 * linux 格式1
	 * Disk identifier: D47F0000-0000-403E-8000-57FF000046FA
	 */
	public static String getHDSerialLinux(){
		String serial = SysUtil.runExec("sudo fdisk -l | grep -E 'identifier|磁盘标识符'");
		Constants.diskCount = serial.split("\n").length - 1;
		return serial.trim();
	}
	
	public static boolean is64VM(){
		return System.getProperty("sun.arch.data.model").equals("64");
	}

	/**
	 * @param state 0关闭 1打开
	 * @param deviceId 系统设备ID
	 */
	public static boolean setSysDevice(int state,String deviceId){
		String cmd = String.format("devcon %s @\"%s\"", state == 0 ? "disable" : "enable",deviceId);
		String re = SysUtil.runExec(cmd);
		return true;
	}

	/**
	 * 获取系统环境变量相关信息
	 * @return
	 */
	public static String envInfo(){
		String path = "archModel:"+System.getProperty("sun.arch.data.model")
				+ "\r\nbootLibrary:"+System.getProperty("sun.boot.library.path")
				+ "\r\nlibraryPath:\r\n" +System.getProperty("java.library.path").replaceAll(";", "\r\n")
				+ "\r\nextDir:\r\n"+System.getProperty("java.ext.dirs").replaceAll(";", "\r\n");

		return path;
	}

	public static void addLibraryPath(String pathToAdd){
		try {
			Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
			usrPathsField.setAccessible(true);
			String[] paths = (String[]) usrPathsField.get(null);
			String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
			newPaths[newPaths.length - 1] = pathToAdd;
			usrPathsField.set(null, newPaths);
		}catch (Exception e){
			System.out.println("添加动态链接库路径失败："+e.getMessage());
		}
	}

	public static void addProperty(String key,String value){
		System.setProperty(key,System.getProperty(key)+";"+value);
	}

	public static void addJnaPath(String path){
		addProperty("jna.library.path",path);
	}

	public static Object scriptByName(String name, Map<String,Object> arg){
		BaseService bs = Constants.ioc.get(BaseService.class);
		SysScript sysScript = bs.getTCacheFirst(SysScript.class, v->name.equals(v.getFunction()));
		List<Object> list = new ArrayList<>();
		String a = sysScript.getArgs();
		String a2 = sysScript.getType().getArgs();
		if(Strings.isNotBlank(a2)){
			for(String b2 : a2.split(",")){
				list.add(arg.get(b2));
			}
		}else if(Strings.isNotBlank(a)){
			for(String b : a.split("\n")){
				String c = b.split("\\|")[0];
				list.add(arg.get(c));
			}
		}
		return SysUtil.scriptByName(name,list.toArray());
	}

	public static Object scriptByName(String name,Object... args){
		if(Strings.isBlank(name))
			return null;
		Invocable invocable = (Invocable) Constants.jse;
		try {
			return invocable.invokeFunction("script_"+name,args);
		} catch (ScriptException e) {
			String msg = e.getCause().getMessage();//e.getMessage()会输出行号，无意义
			if(msg.contains("java.lang.RuntimeException:")){
				msg = msg.replace("java.lang.RuntimeException: ","");
			}else{
				msg = "脚本 "+name+" 执行异常:"+msg;
			}
			throw Lang.makeThrow(msg);
		} catch (NoSuchMethodException e) {
			throw Lang.makeThrow("脚本没有方法 %s",name);
		}
	}

	public static void jsFile(String... path){
		for (String p : path) {
			jsEval(Files.read(p));
		}
	}

	public static String jsFormat(String js){
		return StrUtil.replace(js,"import\\s+['\"]?[\\w._]+['\"]?\\s*;", v2->{
			String[] t1 = v2.trim().split("[.;'\"]");
			String[] t2 = v2.trim().split("[\\s;'\"]");
			return "var "+t1[t1.length-1]+"=Java.type('"+t2[t2.length-1]+"');";
		});
	}
	
	public static Object jsEval(String js){
		Object v;
		if(Strings.isBlank(js))
			return null;

		js = jsFormat(js);

		try {
			v = Constants.jse.eval(js);
		} catch (ScriptException e) {
			throw Lang.makeThrow("脚本执行错误：->\n%s",e.getMessage());
		}

		if(v == null)
			return null;

		//小数或者大于Int最大值
		if(v instanceof Double) {
			double d = (Double) v;
			if(!Double.isFinite(d)){
				return null;
			}else if(Double.toString(d).contains("E")){
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(1);//设置数字的小数部分中允许的最大位数
				nf.setGroupingUsed(false);//不使用分组方式显示数据
				return nf.format(d);
				//DecimalFormat df = new DecimalFormat( "###############0.00 ");
				//return df.format(d);
			}
			//非金额类型，不去掉.0
			/*else if(d % d.intValue() == 0){
				return d.intValue();
			}*/
		}else if(v instanceof Integer){

		}

		return v;
	}

	public static Object jsCalc(String calc,Object val){
		if(Strings.isNotBlank(calc)) {
			calc = calc.replaceAll("@", val.toString());
			val = SysUtil.jsEval(calc);
		}
		return val;
	}

	/**
	 * 播放音频文件 仅支持wav、au、aif、send 格式
	 * 在线 TTS www.6pian.cn/peiyin
	 * @param file
	 */
	public static void play(File file){
		try {
			AudioInputStream cin = AudioSystem.getAudioInputStream(file);
			AudioFormat format = cin.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);;
			line.open(format);//或者line.open();format参数可有可无
			line.start();
			int nBytesRead = 0;
			byte[] buffer = new byte[512];
			while (true) {
				nBytesRead = cin.read(buffer, 0, buffer.length);
				if (nBytesRead <= 0)
					break;
				line.write(buffer, 0, nBytesRead);
			}
			line.drain();
			line.close();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	private static final String Iv = "12345678";
	private static final String Transformation = "DESede/CBC/PKCS5Padding";

	/**
	 * 3DES编码 http://tool.chacuo.net/crypt3des
	 * 加密模式 CBC，填充PKCS5Padding 偏移量 12345678 输出 base64(hex不方便)
	 */
	public static String desEncode(String str){
		byte[] data = str.getBytes();
		byte[] key = Constants.desKey.getBytes();
		SecretKey deskey = new SecretKeySpec(key, "DESede");
		IvParameterSpec iv = new IvParameterSpec(Iv.getBytes());
		try {
			Cipher c1 = Cipher.getInstance(Transformation);
			c1.init(Cipher.ENCRYPT_MODE, deskey, iv);
			byte[] re = c1.doFinal(data);
			return Base64.encodeToString(re,false);
		} catch (Exception e) {
			throw Lang.makeThrow("加密失败："+e.getMessage());
		}
	}

	/**
	 * 3DES解码
	 */
	public static String desDecode(String str){
		byte[] data = Base64.decode(str);
		byte[] key = Constants.desKey.getBytes();
		SecretKey deskey = new SecretKeySpec(key, "DESede");
		IvParameterSpec iv = new IvParameterSpec(Iv.getBytes());
		try {
			Cipher c1 = Cipher.getInstance(Transformation);
			c1.init(Cipher.DECRYPT_MODE, deskey, iv);
			byte[] re = c1.doFinal(data);
			return new String(re);
		} catch (Exception e) {
			throw Lang.makeThrow("解密失败："+e.getMessage());
		}
	}

	/**
	 * 授权
	 */

	public static  String beAuthorized(String licenseStr){
		try {
			if (Strings.isBlank(licenseStr))
				return "无许可证";

			String str = SysUtil.desDecode(licenseStr);
			License license = Json.fromJson(License.class,str);

			if(!license.getSerial().equals(ServletEnum.serialNo.val()))
				return "许可证序列号不匹配："+license.getSerial();


			Date d = license.getExpiry();
			if(d == null)
				return "许可证无有效期";

			boolean r = d.getTime() > System.currentTimeMillis();
			if(!r)
				return  "许可证已过期："+ Times.sD(d);
		}catch (Exception e){
			return "许可证异常："+ e.getMessage();
		}
		return null;
	}

	/**
	 * 获取桌面路径
	 * @return
	 */
	public static String getHomePath(){
		File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
		return desktopDir.getPath();
	}

	/**
	 * --kiosk				启用自助服务终端模式。请注意，这不是ChromeOS信息亭模式。
	 * --kiosk-printing		在打印预览中自动按下打印按钮。
	 * --start-fullscreen 	以全屏模式启动，就像用户在启动后立即按下F11一样。
	 * --start-maximized	无论先前的设置如何，都启动浏览器最大化。
	 * --disable-infobars	防止信息栏出现。
	 * --app				指定应在"应用程序"模式下启动关联值。
	 * –-no-sandbox
	 */
	public static void runApp(String url,String param){
		try {
			String stationLnk = "start http://"+url;
			//优先使用chrome打开
			String chrome =  System.getProperties().getProperty("user.home")+"/AppData/Local/Google/Chrome/Application/chrome.exe";
			if(Files.isFile(new File(chrome))){
				stationLnk = chrome +" --app=http://"+url+" --start-maximized --disable-infobars "+param;
			}
			String[] cmd = {"cmd","/c",stationLnk};
			SysUtil.runExec(0, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MethodBean methodDetail(Method met){

		/*boolean isAbs = Modifier.isAbstract(met.getDeclaringClass().getModifiers()) ;
		if(isAbs){
			return null;
		}*/
		List<String> pname = MethodParamNamesScaner.getParamNames(met);
		if(pname == null)
			return null;
		AoReflect ao = met.getAnnotation(AoReflect.class);
		MethodBean mb = new MethodBean(met.getName(),ao == null ? "" : ao.value(),met.getReturnType());
		mb.setType(ao == null ? AstEnum.auto : ao.type());
		mb.setDeprecated(met.getAnnotation(Deprecated.class) != null);
		mb.setStatic(Modifier.isStatic(met.getModifiers()));

		Parameter[] parameters = met.getParameters();
		for(int i=0;i< parameters.length;i++){
			Parameter p = parameters[i];
			ArgBean ab = new ArgBean(pname.get(i),p.getType());
			mb.getArg().add(ab);
			AoReflect ap = p.getAnnotation(AoReflect.class);
			if(ap == null)
				continue;
			ab.setName(ap.value());
			ab.setUrl(ap.url());
			ab.setSelect(ap.select());
			ab.setPlaceholder(ap.placeholder());
		}
		return mb;
	}

	public static String urlToPdf(HttpServletRequest req,File pdfFile){
		Context context = VarRuntimeEnum.context.val();
		String p = "";
		Enumeration<String> paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			p += "&"+paramName+"="+req.getParameter(paramName);
		}
		Object pdfBin = context.get("pdfBin");
		if(pdfBin == null)
			throw Lang.makeThrow("还未设置pdfBin环境变量");
		return SysUtil.runExec(30,pdfBin.toString(),"--disable-smart-shrinking",
				"http://localhost:"+req.getLocalPort()+req.getContextPath()
						+req.getServletPath().substring(0,req.getServletPath().length()-4)
						+ (p.length() > 0 ? "?" + p.substring(1) : p),
				pdfFile.getAbsolutePath());
	}

}
