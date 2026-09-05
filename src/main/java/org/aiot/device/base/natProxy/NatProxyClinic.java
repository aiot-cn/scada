package org.aiot.device.base.natProxy;

import org.aiot.device.BaseDevice;
import org.aiot.lang.device.natProxy.client.ClinicConfig;
import org.aiot.lang.device.natProxy.client.ProxyClientContainer;
import org.aiot.lang.device.natProxy.common.container.ContainerHelper;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.AstEnum;

import java.util.Arrays;

@AoReflect("内网穿透客户端")
public class NatProxyClinic extends BaseDevice {

    @AoReflect(value ="密钥",type = AstEnum.param)
    private String clientKey = "TEST";

    @AoReflect(value ="开启SSL",type = AstEnum.param)
    private boolean sslEnable = false;
    @AoReflect(value ="JKS证书路径",type = AstEnum.param)
    private String sslJksPath = "test.jks";
    @AoReflect(value ="SSL密钥",type = AstEnum.param)
    private String sslKeyStorePassword = "123456";

    private ProxyClientContainer proxyClientContainer;

    @Override
    public void init(){
        String[] addr = device.getAddress().split(":");
        ClinicConfig.serverHost = addr[0];
        ClinicConfig.serverPort = Integer.parseInt(addr[1]);
        ClinicConfig.clientKey = clientKey;
        ClinicConfig.sslEnable = sslEnable;
        ClinicConfig.sslJksPath = sslJksPath;
        ClinicConfig.sslKeyStorePassword = sslKeyStorePassword;

        proxyClientContainer = new ProxyClientContainer();
        Thread containerThread = new Thread(() -> {
            ContainerHelper.start(Arrays.asList(proxyClientContainer));
        }, "NatProxyClinic-Container");
        containerThread.setDaemon(true);
        containerThread.start();
    }


    public void destroy(){
        if (proxyClientContainer != null)
            proxyClientContainer.stop();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        super.destroy();
    }

}
