package org.aiot.lang;

import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.workflow.Workflow;
import org.aiot.main.Constants;
import org.aiot.model.project.ArgBean;
import org.aiot.model.table.TAction;
import org.aiot.model.table.TBase;
import org.aiot.model.table.TBaseAction;
import org.aiot.service.BaseService;
import org.aiot.service.ConfigService;
import org.aiot.service.DeviceService;
import org.aiot.service.WebsocketRoom;
import org.aiot.util.SysUtil;
import org.nutz.el.El;
import org.nutz.json.JsonField;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class CommonAction {
    private static final Map<Long,ActionState> actionStateMap = new HashMap<>();

    private final Map<String,Object> args = new HashMap<>();//动作周期参数
    private final Context context = Lang.context();
    private CountDownLatch countDownLatch;
    private TAction chain;

    private boolean isChainRun = true;//动作链执行总开关


    /**
     * 是否忽略异常，继续执行
     */
    private boolean ignoreException;
    private boolean outException = true;
    private String msg = "";

    public CommonAction(){
        context.set("Lang",Lang.class);
        context.set("Strings",Strings.class);
        context.set("Files",Files.class);
        context.set("Math",Math.class);
    }

    public CommonAction(Map<String,Object> arg){
        this();
        if(arg != null){
            arg.forEach(this::setArg);
        }
    }

    public Object chainRun(List<TAction> plist){
        isChainRun = true;
        countDownLatch = new CountDownLatch(1);
        Object re = void.class;
        int count = plist.size();
        for(int i = 0 ;isChainRun && i<count;i++){
            TAction p = plist.get(i);
            msg = "动作链["+p.getId()+"]" + (i+1)+"/"+count;
            sendSocket(msg + " 设备:" + p.getDeviceId() +" -> "+p.getMethod()+" 开始执行...");
            Object o = chainRun(p);
            if("return".equals(p.getVariable()) || (i == count-1 && re == void.class))
                re = o;
        }
        countDownLatch.countDown();
        return re == void.class ? null : re;
    }

    public Object chainRun(TAction tAction){
        chain = tAction;
        ActionState actionState = actionStateMap.computeIfAbsent(tAction.getId(),v->new ActionState());
        actionState.start(args);
        DeviceInfc baseDevice = Constants.ioc.get(DeviceService.class).getInstance(tAction.getDeviceId());
        if(baseDevice == null && tAction.getDeviceId() != -2)
            return null;
        Object chainReturn = null;
        try {
            if(tAction.getDeviceId() == -2){
                Workflow wf = new Workflow(tAction.getMethod());
                List<ArgBean> argBeans = wf.getArgs();
                String arg = tAction.getArgs();
                Map<String,Object> map = new HashMap<>();
                if(Strings.isNotBlank(arg)){
                    String[] args = arg.split(",");
                    for (int i=0;i<args.length && i < argBeans.size();i++){
                        String a = args[i];
                        String argName = argBeans.get(i).getCode();
                        map.put(argName,a);
                        if(a.startsWith("#")){
                            map.put(argName,evalEl(Strings.removeFirst(a)));
                        }
                    }
                }
                chainReturn = wf.run(map);
            }else{
                chainReturn = baseDevice.invoke(tAction.getMethod(), tAction.getArgs(), this);
            }
            actionState.setResult(chainReturn);
            if(Strings.isNotBlank(tAction.getVariable()))
                setArg(tAction.getVariable(), chainReturn);
            if(obj2Boolean(chainReturn))
                chainSubRun();
        }catch (Throwable e){
            actionState.setException(e);
            sendSocket("动作链["+tAction.getPid()+"]异常:"+e.getMessage(),true);
            if(outException)
                e.printStackTrace();
            if(!ignoreException){
                throw Lang.makeThrow(e.getMessage());
            }
        }
        return  chainReturn;
    }

    public void sendSocket(String msg,Object... o){
    	WebsocketRoom.sendRoot(String.format(Thread.currentThread().getName() +"["+Integer.toHexString(this.hashCode())+"] - "+ msg,o));
    }

    public Object chainRun(TBase t){
        if(t == null)
            return null;
        if(!TBaseAction.class.isAssignableFrom(t.getClass()))
            return  this.chainRun(t.getClass(),t.getId());

        TBaseAction t0 = (TBaseAction) t;
        Integer type = t0.getActionType();
        String code = t0.getActionCode();
        if(type == null)
            return this.chainRun(t.getClass(),t.getId());;

        BaseService bs = Constants.ioc.get(BaseService.class);
        if(type == 1){

        }
        if(type == 2){
            return SysUtil.scriptByName(code,args);
        }
        return null;
    }

    /**
     * 执行当前动作链的子集动作链
     */
    public Object chainSubRun(){
        if(chain.getId() == null)
            return null;
        ConfigService cs = Constants.ioc.get(ConfigService.class);
        return chainRun(cs.getAction(chain.getId()));
    }

    public Object chainRun(Class<?> klass,Long pid){
        return chainRun(klass.getName(),pid);
    }

    public Object chainRun(String klass,Long pid){
        ConfigService cs = Constants.ioc.get(ConfigService.class);
        return chainRun(cs.getAction(klass,pid));
    }

    public Object evalEl(String el){
        return El.eval(context,el);
    }

    /**
     * 停止该对象所有动作链
     */
    public void stop(){
        sendSocket("动作链提前终止",true);
        isChainRun = false;
    }

    public void stop(int timeout){
        isChainRun = false;
        sendSocket("动作链提前终止,等待正在执行的任务结束",true);
        try {
            if(countDownLatch != null)
                countDownLatch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return msg + " 参数："+args;
    }

    public static boolean obj2Boolean(Object val){
        if(val == null)
            return false;
        if(val instanceof Boolean)
            return (boolean) val;
        if(val instanceof Number)
            return ((Number) val).doubleValue() > 0;
        if(val instanceof CharSequence)
            return  Lang.parseBoolean(val.toString());

        return true;
    }

    public static ActionState getActionState(Long id){
        return actionStateMap.get(id);
    }

    public TAction getChain() {
        return chain;
    }

    public boolean isChainRun() {
        return isChainRun;
    }
    public boolean isChainRun(boolean isChainRun) {
        return this.isChainRun = isChainRun;
    }

    public void setIgnoreException(boolean ignoreException) {
        this.ignoreException = ignoreException;
    }

    public boolean isOutException() {
        return outException;
    }

    public void setOutException(boolean outException) {
        this.outException = outException;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public Object getArg(String name){
        return args.get(name);
    }

    public void setArg(String name,Object value){
        args.put(name,value);
        context.set(name, value);
    }

    public void setContext(String name, Object value){
        context.set(name, value);
    }

    /*public void removeArg(String... name){
        for(String k : name)
            args.remove(k);
    }*/

    public static class ActionState{
        @JsonField(dataFormat="yyyy/MM/dd hh:mm:ss.SSS")
        private Date executionTime;
        @JsonField(dataFormat="yyyy/MM/dd hh:mm:ss.SSS")
        private Date finishTime;
        private String consumingTime;
        private Object result;
        private NutMap args;
        private Throwable exception;

        public void start(Map<String,Object> args){
            executionTime = new Date();
            this.args = new NutMap(args);
            //二次执行取的对象相同
            result = null;
            finishTime = null;
            consumingTime = null;
            exception = null;
        }

        public Date getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(Date executionTime) {
            this.executionTime = executionTime;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
            finishTime = new Date();
        }

        public Date getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(Date finishTime) {
            this.finishTime = finishTime;
        }

        public String getConsumingTime() {
            if(executionTime == null || finishTime == null)
                return null;
            long t = finishTime.getTime() - executionTime.getTime();
            if(t < 10 * 1000)
                consumingTime = t + "ms";
            else
                consumingTime = (t/1000)+"s";
            return consumingTime;
        }

        public void setConsumingTime(String consumingTime) {
            this.consumingTime = consumingTime;
        }

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
            finishTime = new Date();
        }

        public NutMap getArgs() {
            return args;
        }

        public void setArgs(NutMap args) {
            this.args = args;
        }

    }

}
