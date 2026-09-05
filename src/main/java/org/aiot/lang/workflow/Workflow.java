package org.aiot.lang.workflow;

import org.aiot.infc.device.DeviceInfc;
import org.aiot.main.Constants;
import org.aiot.model.project.ArgBean;
import org.aiot.model.project.MethodBean;
import org.aiot.model.table.TWorkflow;
import org.aiot.service.BaseService;
import org.aiot.service.DeviceService;
import org.aiot.util.StrUtil;
import org.aiot.util.SysUtil;
import org.nutz.json.ToJson;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.aiot.main.Constants.scriptManager;

@ToJson
public class Workflow {
    public static Map<Long,Bindings> bindingsMap = new HashMap<>();
    public static Map<Long,List<WorkflowConnection>> connectionsMap = new HashMap<>();

    private final ScriptEngine manager = scriptManager.getEngineByName("nashorn");
    private final Bindings bindings = manager.getBindings(ScriptContext.ENGINE_SCOPE);
    private final Map<Integer,Object> resultMap = new HashMap<>();

    private TWorkflow tWorkflow;
    private List<WorkflowMethod> workflowMethods;
    private List<WorkflowConnection> workflowConnections;
    private List<WorkflowConnection> connectionsActive = new ArrayList<>();

    public static Object getGlobal(String uri){
        if(uri.startsWith("/"))
            uri = uri.substring(1);
        String[] s = uri.split("[/.\\\\]+");
        Bindings b =  bindingsMap.get(Long.parseLong(s[0]));
        return b.get(s[1]);
    }

    public Workflow(){

    }

    public Workflow(TWorkflow tWorkflow){
        this.tWorkflow = tWorkflow;
        if(tWorkflow != null && Strings.isNotBlank(tWorkflow.getContent()))
            setJson(tWorkflow.getContent());
    }

    //脚本中
    public Workflow(String code){
        BaseService bs = Constants.ioc.get(BaseService.class);
        tWorkflow = bs.getTCacheFirst(TWorkflow.class, v->Strings.equals(code,v.getCode()));
        setJson(tWorkflow.getContent());
    }

    public void setJson(String json){
        NutMap nm = new NutMap(json);
        this.workflowMethods = nm.getAsList("method",WorkflowMethod.class);
        this.workflowConnections =  nm.getAsList("connection",WorkflowConnection.class);
        workflowConnections.sort(WorkflowConnection::compareTo);
    }

    public Object run(Map<String,Object> param){
        bindings.put("_this",this);
        bindings.put("RUN_TIME",new Date());
        if(workflowMethods == null)
            return null;
        if(param != null)
            bindings.putAll(param);
        WorkflowMethod wm = getWorkflowMethod(WorkflowMethod::isStart);
        if(wm == null)
            return null;

        if(tWorkflow != null){
            bindingsMap.put(tWorkflow.getId(),bindings);
            connectionsMap.put(tWorkflow.getId(),connectionsActive);
        }

        try {
            callMethod(wm,false);
        } catch (Throwable e) {
            throw Lang.makeThrow(e.getMessage());
        }finally{
            resultMap.forEach((k,v)->{
                if(v instanceof AutoCloseable){
                    try {
                        ((AutoCloseable)v).close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return bindings.get("return");
    }

    public void callMethod(WorkflowMethod wm,boolean isArg) throws ScriptException {
        Object re;
        if(wm.getDeviceId() == -1){
            //脚本
            String content = wm.getArg().get("content").toString();
            if(Strings.isBlank( content)){
                re = null;
            }else{
                content = SysUtil.jsFormat(content);
                re = manager.eval(content);
            }
        }else if(wm.getDeviceId() == -2){
            //工作流
            Workflow wf = new Workflow(wm.getMethod());
            re = wf.run(buildArg(wm,wf.getArgs()));
        }else{
            //设备方法
            DeviceService ds = Constants.ioc.get(DeviceService.class);
            DeviceInfc bd = ds.getInstance(wm.getDeviceId());
            MethodBean mb = ds.methodDetail(bd.getClass(),wm.getMethod());
            re =  bd.invoke(wm.getMethod(),buildArg(wm,mb.getArg()));
        }

        resultMap.put(wm.getIndex(),re);
        if(Strings.isNotBlank(wm.getVariable()))
            bindings.put(wm.getVariable(),re);

        if(isArg)
            return;

        for(WorkflowConnection wc : getConns(v->v.getSourceIndex().equals(wm.getIndex()))){
            if(Strings.isNotBlank(wc.getCondition()) && !(Boolean) manager.eval(wc.getCondition()))
                continue;
            connectionsActive.add(wc);
            WorkflowMethod wm2 = getWorkflowMethod(v->wc.getTargetIndex().equals(v.getIndex()));
            callMethod(wm2,false);
            break;
        }

    }

    public Map<String,Object> buildArg(WorkflowMethod wm,List<ArgBean> args) throws ScriptException {
        Map<String,Object> a = new HashMap<>();
        for (ArgBean ab : args) {
            Map<String,Object> arg = wm.getArg(); //不能赋值
            Object argVal = arg.get(ab.getCode());
            if(argVal != null && Strings.isNotBlank(argVal.toString())){
                a.put(ab.getCode(),manager.eval(argVal.toString()));
            }

            List<WorkflowConnection> wcs = getConns(v->(wm.getIndex()+"-"+ab.getCode()).equals(v.getTarget()));
            for (WorkflowConnection wc : wcs) {
                if (!resultMap.containsKey(wc.getSourceIndex())) {
                    WorkflowMethod wm2 = getWorkflowMethod(v -> v.getIndex().equals(wc.getSourceIndex()));
                    callMethod(wm2, true);
                }
                Object o = resultMap.get(wc.getSourceIndex());
                a.put(ab.getCode(), o);
            }
        }
        return a;
    }

    public Object get(String key){
        return bindings.get(key);
    }

    public Object put(String name,Object value){
        return bindings.put(name,value);
    }

    public List<WorkflowConnection> getConns(Predicate<WorkflowConnection> predicate){
        return workflowConnections.stream().filter(predicate).collect(Collectors.toList());
    }

    public WorkflowMethod getWorkflowMethod(Predicate<WorkflowMethod> predicate){
        return workflowMethods.stream().filter(predicate).findFirst().orElse(null);
    }

    public Bindings getBindings(){
        return bindings;
    }

    public List<ArgBean> getArgs(){
        return StrUtil.String2List(tWorkflow.getArgs(),"\n",ArgBean.class);
    }

    public String toJson(){
        return "\""+ this +"\"";
    }

}
