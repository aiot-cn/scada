package org.aiot.device.base;


import org.aiot.device.BaseDevice;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.DeviceRoleEnum;


import java.util.List;

@AoReflect(value = "节点控制器",deviceRole = DeviceRoleEnum.CONTROLLER)
public class NodeDevice extends BaseDevice {

    @Override
    public void init(){

    }

    @AoReflect("开关模式")
    public void doModel(String no,@AoReflect(select = ":自动,1:强制开,0:强制关")Integer model){
        /*Data d = getData(no);
        if(d == null)
            throw Lang.makeThrow(device.getName()+"还没有获取到开关"+no+"状态，不能设置开关模式");
        d.setToVal(model);*/
    }

    @AoReflect("开关控制")
    public void doOperation(String no,@AoReflect(select = "1:打开,0:关闭")int type){
        comSet(no,type);
    }

    @AoReflect(value="开关(秒)")
    public void comRemoteControl(String DO,int ON,int second){
        /*Data data = DATA.getData(DO);
        if(data == null){
            comSet(DO, ON);
        }else {
            Integer toOn = data.getToVal();//强制状态
            if(toOn == null && System.currentTimeMillis() > data.getToTime()){
                if(second != 0)
                    data.setToTime(System.currentTimeMillis() + second * 1000L);
                if(!Objects.equals(data.getInt(),ON))
                    comSet(DO, ON);
            }
        }*/
    }

    @Override
    public List<Command> comPoll(){
         autoComSet();
        return exec();
    }

    public void autoComSet(){

    }

}
