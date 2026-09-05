package org.aiot.device.base;


import org.aiot.device.BaseDevice;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.lang.PointData;
import org.aiot.model.table.TDevice;
import org.aiot.model.table.TPoint;
import org.aiot.model.table.TRecord;
import org.aiot.service.PointService;
import org.aiot.service.WebsocketRoom;
import org.aiot.util.OpenCVUtil;
import org.nutz.dao.Cnd;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.aiot.main.Constants.ioc;

//本地
public class LocalDevice extends BaseDevice {

    Log log = Logs.get();

    @AoReflect(value = "白天",getter = true)
    private int atDaytime;

    @AoReflect(value = "晚上",getter = true)
    private int atNight;

    @AoReflect(value = "小时",getter = true)
    private int hour;

    @AoReflect(value = "分钟",getter = true)
    private int minute;

    @AoReflect(value = "属性1",type = AstEnum.analysis)
    private Integer f1;

    @AoReflect(value = "参数",type = AstEnum.param)
    private String p1;


    @Override
    public void init(){

    }

    @AoReflect("运行环境信息")
    public void operatingEnvInfo(){
        System.out.println("程序启动目录："+System.getProperty("user.dir"));
        System.out.println("用户目录："+System.getProperty("user.home"));
    }

    @AoReflect("性能测试")
    public String performanceTest(File img){
        long t1 = System.currentTimeMillis();
        Mat mat = OpenCVUtil.read(img);
        long t2 = System.currentTimeMillis();
        Mat mat2 = OpenCVUtil.resizeToTargetHeight(mat,640);
        long t3 = System.currentTimeMillis();
        String msg = "图片("+mat.width()+"*"+mat.height()+") 读取:"+(t2-t1) +"ms 缩放:"+(t3-t2)+"ms";
        OpenCVUtil.release(mat,mat2);
        return msg;
    }

   /* @AoReflect(value="测试流程",type=AstEnum.workflow)
    public BaseDevice workflowTest(){
        return (BaseDevice) execWorkflow(null);
    }*/


    @AoReflect("发送通知")
    public void sendNotify(String msg){
        notify(msg);
    }

    @AoReflect("发送消息至->")
    public void sendMsg(String room,String msg){
        ioc.get(WebsocketRoom.class).sendMsg("wsroom:"+room, msg,true);
    }

    @AoReflect("发送消息")
    public void sendMsg2(String msg,boolean isLog){
        sendSocket(msg,isLog);
    }

     /*@AoReflect("弹窗")
    public void dialog(@AoReflect("目标") String target,
                       @AoReflect(value = "类型",select = "0:信息,1:页面,2:iframe,3:加载,4:tips,5:输入框,6:执行") int type,
                       @AoReflect(value = "内容",input = "type:text") String content,
                       @AoReflect(value = "选项",input = "type:text") MsgData options
    ){
        //"/json/img?name="+fileName
        //"/plugin/hikvision/camera?style="+style+"&d="+device.getId()+"&channel="
        if(options == null){
            options = new MsgData(type, content);
        }else{
            options.setType(type);
            options.setContent(content);
        }
        options.send(Strings.sBlank(target,"index"));
    }*/

    @AoReflect("设置点位")
    public PointData setPoint(String pointCode,Object data){
        PointService ps = ioc.get(PointService.class);
        return ps.put(pointCode,data);
    }


    @AoReflect("所有设备自检")
    public void deviceTest(){
        bs.getTCache(TDevice.class).forEach(v->{
            DeviceInfc bd = ds.getInstance(v.getId());
            try {
                if(bd != null)
                    bd.selfTest();
            }catch (Exception e){
                log.errorf("设备[%s]自检异常:%s", v.getName(),e.getMessage());
            }

        });
    }

    @AoReflect("保存记录")
    public void saveRecord(){
        PointService ps = ioc.get(PointService.class);

        List<TRecord> list = new ArrayList<>();
        bs.getTCache(TPoint.class, TPoint::isRecOnTime).forEach(v->{
            PointData data = ps.getPointData(v.getId());
            if(data != null && data.getValue() != null){
                TRecord record = new TRecord();
                record.setPid(v.getId());
                record.setVal(data.getValue());
                record.setState(data.getState());
                list.add(record);
            }
        });
        bs.daoInsert(list);
    }

    @AoReflect("清理记录")
    public void cleanRecord(@AoReflect("天前") int daysAgo){
        Cnd cnd = Cnd.where("createDate", "<", Times.nextDay(null,daysAgo*-1));
        List<TRecord> rhs = bs.query(TRecord.class,cnd);
        bs.daoClear(TRecord.class,cnd);
        for(TRecord rh : rhs){
            if(Strings.isNotBlank(rh.getFile()))
                Files.deleteFile(new File(Constants.HOME_PATH+rh.getFile()));
        }
    }

    @AoReflect("清理文件")
    public void cleanFile(File dir,@AoReflect("天前") Integer daysAgo,@AoReflect("最大数量") Integer maxCount){
        long t = System.currentTimeMillis();
        File[] files = dir.listFiles();
        //从大到小
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        boolean del = false;
        for(int i=0;i<files.length;i++){
            File file = files[i];
            if(del || (maxCount != null && i > maxCount) || (daysAgo != null && t - file.lastModified() > daysAgo * 24 * 60 * 60 * 1000)){
                del = true;
                if(file.isFile())
                    file.delete();
            }
        }
    }


    /*@AoReflect("播放音频")
    public void audio(File file){
        SysUtil.play(file);
    }
    //依赖jacob-1.18-x64.dll
    @AoReflect("播放文本")
    public void speakText(String text,@AoReflect(value = "语速",placeholder = "-10~10") int speed){
        ActiveXComponent voice = new ActiveXComponent("SAPI.SpVoice");
        try {
            // 获取可用语音列表
            Dispatch voices = voice.invoke("GetVoices").toDispatch();
            int voiceCount = Dispatch.call(voices, "Count").getInt();

            if (voiceCount == 0) {
                System.out.println("SAPI.SpVoice 未找到任何语音");
                return;
            }

            Dispatch targetVoice  = Dispatch.call(voices, "Item", new Variant(0)).toDispatch();
            *//*for (int i = 0; i < voiceCount; i++) {
                Dispatch voiceItem = Dispatch.call(voices, "Item", new Variant(i)).toDispatch();
                String voiceName = Dispatch.call(voiceItem, "GetDescription").toString();
                System.out.println("可用语音: " + voiceName);
            }*//*

            // 设置引擎
            Dispatch.putRef(voice, "Voice", targetVoice);

            // 设置音量和语速
            voice.setProperty("Volume", new Variant(100));
            voice.setProperty("Rate", new Variant(speed)); //-10 - 10

            Dispatch.call(voice, "Speak", new Variant(text));

        } catch (Exception e) {
            System.err.println("SAPI.SpVoice ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            voice.safeRelease();
        }

    }*/

    
    public int getAtDaytime(){
        int hour = getCal().get(Calendar.HOUR_OF_DAY);
        return hour >= 7 && hour < 19 ? 1 : 0;
    }

    public int getAtNight() {
        int hour = getCal().get(Calendar.HOUR_OF_DAY);
        return hour >= 7 && hour < 19 ? 0 : 1;
    }

    @AoReflect("cpu使用率")
    public double getCpuUseAge(){
        double cpuUsage = 0;
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            double d = sunOsBean.getProcessCpuLoad()*100;
            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.HALF_UP);  // 设置四舍五入模式
            String str = df.format(d);
            cpuUsage = Double.parseDouble(str);
            System.out.println("CPU Usage: " + cpuUsage * 100 + "%");
        } else {
            System.out.println("Unable to get CPU usage from the current JVM");
        }
        return cpuUsage;
    }
    /**
	        示例：唤醒 00:1A:2B:3C:4D:5E 的设备
     */
    @AoReflect("局域网唤醒")
    public void localWakeUP(String macAddress) throws Exception {
    	String ipAddress = "255.255.255.255";
    	int port = 9;
        // 1. 将 MAC 地址转换为字节数组
        byte[] macBytes = parseMacAddress(macAddress);

        // 2. 构建魔术包数据
        byte[] magicPacket = new byte[6 + 16 * macBytes.length];
        // 填充 6 字节的 0xFF
        for (int i = 0; i < 6; i++) {
            magicPacket[i] = (byte) 0xFF;
        }
        // 重复 MAC 地址 16 次
        for (int i = 6; i < magicPacket.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, magicPacket, i, macBytes.length);
        }

        // 3. 发送 UDP 数据包
        InetAddress address = InetAddress.getByName(ipAddress);
        DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, port);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
            sendSocket("魔术包已发送至 " + ipAddress + ":" + port);
        }
    }
    // 解析 MAC 地址（支持格式如 "00:1A:2B:3C:4D:5E" 或 "00-1A-2B-3C-4D-5E"）
    private  byte[] parseMacAddress(String macAddress) {
        String cleanMac = macAddress.replaceAll("[:\\-]", "");
        if (cleanMac.length() != 12) {
            throw new IllegalArgumentException("无效的 MAC 地址格式");
        }

        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            String hex = cleanMac.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(hex, 16);
        }
        return bytes;
    }
    @AoReflect("内存使用率")
    public double getMemUseAge(){
        double result = 0;
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

            // 获取总内存
            long totalPhysicalMemory = sunOsBean.getTotalPhysicalMemorySize();
            System.out.println("Total Physical Memory: " + totalPhysicalMemory / 1024 / 1024 + "MB");

            // 获取已使用的内存
            long usedPhysicalMemory = sunOsBean.getTotalPhysicalMemorySize() - sunOsBean.getFreePhysicalMemorySize();
            System.out.println("Used Physical Memory: " + usedPhysicalMemory / 1024 / 1024 + "MB");

            // 获取空闲内存
            long freePhysicalMemory = sunOsBean.getFreePhysicalMemorySize();
            System.out.println("Free Physical Memory: " + freePhysicalMemory / 1024 / 1024 + "MB");

            double d = (usedPhysicalMemory *100 / totalPhysicalMemory);
            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.HALF_UP);  // 设置四舍五入模式
            String str = df.format(d);
            result = Double.parseDouble(str);
        } else {
            System.out.println("Unable to get memory usage from the current JVM");
        }
        return result;
    }
    @AoReflect("磁盘使用率")
    public double getDiskUseAge(String paths){
        long totalSpace = 0;
        long freeSpace = 0;
        String[] pathArray = paths.split(",");
        for(int i=0;i<pathArray.length;i++){
            String s = pathArray[i];
            Path path = Paths.get(s); // 你可以改变这个路径为你想要检查的任何路径
            try {
                totalSpace += java.nio.file.Files.getFileStore(path).getTotalSpace();
                freeSpace += java.nio.file.Files.getFileStore(path).getUsableSpace();

                System.out.println(s+" Total space: " + totalSpace / 1024 / 1024 + "MB");
                System.out.println(s+" Free space: " + freeSpace / 1024 / 1024 + "MB");
                System.out.println(s+" Used space: " + (totalSpace - freeSpace) / 1024 / 1024 + "MB");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(totalSpace == 0){
            return 0;
        }else{
            double d = (totalSpace - freeSpace) * 100 / totalSpace;
            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.HALF_UP);  // 设置四舍五入模式
            String str = df.format(d);
            return Double.parseDouble(str);
        }
    }
    /**
     * win10需要以管理员运行
     * @param encoding 经测试，win10：UTF-8  win7：GBK
     * @param networkName 无线或WLAN
     * @param enabled
     * @return
     */
    
    @AoReflect("网卡启停(win)")
    public String dealNetworkCardWin(String encoding,String networkName,boolean enabled){
    	String result = "";
        try {
            String interfaceName = getWirelessInterfaceName(networkName,encoding);
            
            if (interfaceName == null) {
            	result = "找不到无线网卡，networkName" + networkName;
            }

            if (enabled) {
            	result = enableWireless(interfaceName,encoding);
            }else {
            	result = disableWireless(interfaceName,encoding);
            }
        } catch (Exception e) {
        	result = "操作失败: " + e.getMessage();
        }
        return result;
    }
    private String getWirelessInterfaceName(String networkName,String encoding) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("netsh interface show interface");
        String output = readProcessOutput(process,encoding);
        process.waitFor();

        // 解析输出以查找无线接口
        String[] lines = output.split("\n");
        Pattern pattern = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.*)$");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                String type = matcher.group(3);
                String name = matcher.group(4);
                if (name.indexOf(networkName) >= 0 || "无线".equals(type)) {
                    return matcher.group(4).trim();
                }
            }
        }
        return null;
    }

    private String disableWireless(String interfaceName,String encoding) throws IOException, InterruptedException {
    	return executeNetworkCommand("disable", interfaceName,encoding);
    }

    private String enableWireless(String interfaceName,String encoding) throws IOException, InterruptedException {
    	return executeNetworkCommand("enable", interfaceName,encoding);
    }

    private String executeNetworkCommand(String operation, String interfaceName,String encoding) throws IOException, InterruptedException {
        String command = String.format("netsh interface set interface \"%s\" admin=%s", interfaceName, operation);
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        String output = readProcessOutput(process,encoding);

        if (exitCode != 0) {
            return "操作失败，错误信息: " + output;
        }
       return "无线网卡已成功" + ("enable".equals(operation) ? "启用" : "禁用");
    }

    private String readProcessOutput(Process process,String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), encoding)); // 中文系统编码
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), encoding));

        StringBuilder output = new StringBuilder();
        String line;

        // 读取标准输出
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // 读取错误输出
        while ((line = errorReader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString().trim();
    }
    public int getHour() {
        return getCal().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return getCal().get(Calendar.MINUTE);
    }

    public Calendar getCal(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal;
    }
}
