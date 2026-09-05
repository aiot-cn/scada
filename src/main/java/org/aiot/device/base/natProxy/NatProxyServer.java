package org.aiot.device.base.natProxy;

import io.netty.channel.Channel;
import org.aiot.device.BaseDevice;
import org.aiot.lang.device.natProxy.common.container.ContainerHelper;
import org.aiot.lang.device.natProxy.server.ProxyChannelManager;
import org.aiot.lang.device.natProxy.server.ProxyServerContainer;
import org.aiot.lang.device.natProxy.server.ProxyConfig;
import org.aiot.lang.device.natProxy.server.metrics.Metrics;
import org.aiot.lang.device.natProxy.server.metrics.MetricsCollector;
import org.aiot.device.base.natProxy.table.ProxyClinic;
import org.aiot.device.base.natProxy.table.ProxyPort;
import org.aiot.infc.device.BaseExtend;
import org.aiot.lang.NotifyEvent;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.EventEnum;

import java.util.*;

@AoReflect("内网穿透服务")
public class NatProxyServer extends BaseDevice implements Observer,BaseExtend.RMenu {

    private ProxyServerContainer proxyServerContainer;

    @Override
    public void init(){
        bs.addObserver( this);
        initTable(ProxyClinic.class);
        ProxyConfig.clients = getClients();
        proxyServerContainer = new ProxyServerContainer();

        Thread containerThread = new Thread(() -> {
            ContainerHelper.start(Arrays.asList(proxyServerContainer));
        }, "NatProxy-Container-Thread");
        containerThread.setDaemon(true);
        containerThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ProxyConfig.getInstance().update(getClients());
    }

    public List<ProxyConfig.Client> getClients(){
        List<ProxyConfig.Client> clients = new ArrayList<>();
        for(ProxyClinic clinic : bs.getTCache(ProxyClinic.class)){
            ProxyConfig.Client client = new ProxyConfig.Client();
            client.setClientKey(clinic.getKey());
            client.setName(clinic.getName());
            List<ProxyConfig.ClientProxyMapping> proxyMappings = new ArrayList<>();
            client.setProxyMappings(proxyMappings);
            for(ProxyPort port : bs.getTCache(ProxyPort.class,v->clinic.getId().equals(v.getClinicId()))){
                ProxyConfig.ClientProxyMapping mapping = new ProxyConfig.ClientProxyMapping();
                mapping.setInetPort(port.getNetPort());
                mapping.setLan(port.getLan());
                mapping.setName(port.getName());
                proxyMappings.add(mapping);
            }
            clients.add(client);
        }
        return clients;
    }

    //获取客户端状态
    public Map<Long, Integer> getClientStatus(){
        Map<Long,Integer> online = new HashMap<>();
        for (ProxyClinic client : bs.getTCache(ProxyClinic.class)) {
            Channel channel = ProxyChannelManager.getCmdChannel(client.getKey());
            online.put(client.getId(),channel != null ? 1 : 0);
        }
        return online;
    }

    public List<Metrics> getMetrics(){
        return MetricsCollector.getAllMetrics();
    }

    @Override
    public List<BaseExtend.RMenu.RMenuOption> menuList(){
        List<BaseExtend.RMenu.RMenuOption> list = new ArrayList<>();
        list.add(buildMenu("客户端配置","/base/app/natProxy/index","DEV"));
        return list;
    }

    @Override
    public Object menuClick(String menu, String val) {
        return null;
    }

    public void destroy(){
        if (proxyServerContainer != null)
            proxyServerContainer.stop();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        super.destroy();
    }

    @Override
    public void update(Observable o, Object event) {
        if(!(event instanceof NotifyEvent))
            return;
        NotifyEvent ne = (NotifyEvent) event;
        if(ne.getEventType() != EventEnum.SAVE_AFTER)
            return;
        Object arg = ne.getData();

        if(arg instanceof ProxyClinic || arg instanceof ProxyPort){
            ProxyConfig.getInstance().update(getClients());
        }
    }
}
