package org.aiot.communication.mode;

import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import org.aiot.communication.CommunicationInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.table.TDevice;
import org.aiot.util.CalcUtil;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Optional;

@AoReflect("串口")
public class SerialPort extends CommunicationInfc {
    Log log = Logs.get();
	private com.fazecast.jSerialComm.SerialPort serialPort;

	private String port = "COM3";
    private boolean isHex = true;

    private SerialPortDataListener listener;

    @AoReflect(value="波特率",type = AstEnum.param)
    private int rate = 9600;

    @AoReflect(value="校验位",type = AstEnum.param,select = "0:None,1:Odd,2:Even,3:Mark,4:Space")
    private int parity = 0;

    @AoReflect(value="分包间隔",type = AstEnum.param)
    private int timeout = 20;//信息回复超时,9600波特率需要20ms

    @AoReflect(value="打开时发送",type = AstEnum.param)
    private String openSend = "";

    @AoReflect(value="结束时发送",type = AstEnum.param)
    private String closeSend = "";

    @AoReflect(value="监听间隔",type = AstEnum.param)
    private Long listenDelay = null;

    @AoReflect(value="分隔符",type = AstEnum.param)
    private String delimiter = null;


    
   /* public static void main(String[] args) throws InterruptedException {
    	JSerialPort serialComm = new JSerialPort("COM4");
        serialComm.openPort();
        while (true) {
            int i = 1;
        	byte[] bs = serialComm.writeAndRead(new byte[]{0x7F, (byte) (i+1), 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, (byte) (0x14 + i)});
            System.out.println(StringUtil.bytesToHexString(bs));
            Thread.sleep(1000);
        }
    }*/

 
    /**
     * 打开串口
     */
    public boolean open() {

        if (serialPort != null  && serialPort.isOpen()) {
            return true;
        }

        this.port = commun.getUri();
        this.isHex = commun.isHex();
        try {
            //Linux下会直接异常，导致主线程挂掉
            serialPort = com.fazecast.jSerialComm.SerialPort.getCommPort(port);
            if(serialPort.openPort()){ //上层输出一次打开失败信息，防止后台输出太多
                /*
                  很多时候，确保read调用总是返回至少1字节的有效数据是有益的。单个函数调用通常也最好不要无限期地阻塞。这两种行为都可以通过jSerialComm库的半阻塞读取模式来启用
                  comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

                  有时您会先验地知道在任何给定时间期望读取的数据量。在这些情况下，您不希望在读取所有预期的数据字节之前返回read()调用。此行为可以与读取超时相结合，以确保调用不会无限期阻塞
                  在这种情况下，readBytes()调用应该总是返回请求的n字节，除非这个字节数没有通过串行端口传输10毫秒，
                  /serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 10, 0);
                 */
                serialPort.setBaudRate(rate);
                serialPort.setParity(parity);
                log.infof("串口 %s:%d 已打开",port,rate);
                if(Strings.isNotBlank(openSend)){
                    byte[] b = isHex ? CalcUtil.hexToByte(openSend) : openSend.getBytes();
                    send(b);
                }
                return true;
            }

        }catch (Exception e){
            sendSocket(CdataEnum.OTS,"串口 %s 打开异常：%s",port,e.getMessage());
        }

        return false;
    }
 
 
    /**
     * 关闭串口
     *
     */
    public boolean close() {
        deleteObservers();
        if(serialPort != null){
            serialPort.removeDataListener();
            if (serialPort.isOpen()) {

                if(Strings.isNotBlank(closeSend)){
                    byte[] b = isHex ? CalcUtil.hexToByte(closeSend) : closeSend.getBytes();
                    send(b);
                }

                serialPort.closePort();
            }
        }
        log.warn(port + " 串口已关闭");
        return true;
    }

    public void send(byte[] b) {
        serialPort.writeBytes(b,b.length);
    }

    /**
     * 向com口发送数据并且读取数据
     */
    @Override
    public byte[] send(Command command) {
        byte[] b = command.getDataToSend();
        int t2 = Optional.ofNullable(command.getOutTime()).orElse(timeout);
        //响应时间
        int responseTime = Optional.ofNullable(command.getResponseTime()).orElse(1000);
        //切换波特率
        TDevice dev = command.getDevice();
        Integer ratec = null;
        if(ratec != null && !ratec.equals(rate)){
            serialPort.setBaudRate(ratec);
            rate = ratec;
        }

        long t1 = System.currentTimeMillis();
        //先清空
        int ba = serialPort.bytesAvailable();
        if(ba > 0)
            serialPort.readBytes(new byte[ba], ba);

        int numWrite = serialPort.writeBytes(b,b.length);

        if (numWrite == 0 || commun.isListen()) {
            return null;
        }

        while(System.currentTimeMillis() - t1 < responseTime && serialPort.bytesAvailable() == 0){
            Lang.sleep(10);//控制CPU占用
        }

        int length = serialPort.bytesAvailable();
        if(length == 0){
            return null;
        }

        byte[] res = new byte[0];
        do{
            byte[] readBuffer = new byte[length];
            serialPort.readBytes(readBuffer, readBuffer.length);
            res = CalcUtil.byteJoin(res, readBuffer);
            Lang.sleep(t2);
            length = serialPort.bytesAvailable();
        }while (length > 0);

        return res.length == 0 ? null : res;
    }


    @Override
    public void receive() {
        if(!open())
            return;

        log.infof("串口 %s:%d HEX:%b 开始监听",port,rate,commun.isHex());
        listener = new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                //SerialPort.LISTENING_EVENT_DATA_RECEIVED;//收到一定的数据量 event.getReceivedData();获取
                return com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;//任何可用的数据可以通过串行端口读取
            }

            @Override
            public void serialEvent(SerialPortEvent event){
                if (event.getEventType() != com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;

                if(listenDelay != null){
                    int s0;

                    do{
                        s0 = serialPort.bytesAvailable();
                        Lang.sleep(listenDelay);
                    }while (serialPort.bytesAvailable() != s0);
                }

                int bsize = serialPort.bytesAvailable();
                if(bsize > 0){
                    byte[] bytes = new byte[bsize];
                    serialPort.readBytes(bytes, bsize);
                    setChanged();
                    notifyObservers(commun.isHex() ? bytes : new String(bytes));
                }
            }

        };

        if(Strings.isNotBlank(delimiter))
            listener = new SerialPortMessageListener() {
                @Override
                public int getListeningEvents() {
                    return com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }
                @Override
                public byte[] getMessageDelimiter() {
                    return CalcUtil.hexToByte(delimiter.replace("^",""));
                }

                @Override
                public boolean delimiterIndicatesEndOfMessage() {
                    return !Strings.startsWithChar(delimiter,'^');////是结束符
                }

                @Override
                public void serialEvent(SerialPortEvent event){
                    byte[] delimitedMessage = event.getReceivedData();
                    setChanged();
                    notifyObservers(commun.isHex() ? delimitedMessage : new String(delimitedMessage));
                }

            };

        serialPort.addDataListener(listener);

    }

}


