package org.aiot.device.base;

import org.aiot.device.BaseDevice;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OnvifCamera extends BaseDevice {
    private static final String MULTICAST_ADDRESS = "239.255.255.250";
    private static final int PORT = 3702;

    private static String buildProbeMessage() {
        String messageId = "urn:uuid:"+UUID.randomUUID();
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope " +
                "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                "xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" " +
                "xmlns:wsd=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\" " +
                ">" +
                    "<soap:Header>" +
                        "<wsa:MessageID>" + messageId + "</wsa:MessageID>" +
                    "</soap:Header>" +

                    "<soap:Body>" +
                        "<wsd:Probe>" +
                            "<wsd:Types>dn:NetworkVideoTransmitter</wsd:Types>" +
                        "</wsd:Probe>" +
                    "</soap:Body>" +
                "</soap:Envelope>";
    }

    public static List<String> discoverDevices(int timeoutMs) throws Exception {
        LinkedHashSet<String> deviceAddresses = new LinkedHashSet<>();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

        List<NetworkInterface> usableInterfaces = new ArrayList<>();
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            if (ni.isUp() && !ni.isLoopback() && ni.supportsMulticast()) {
                usableInterfaces.add(ni);
            }
        }

        if (usableInterfaces.isEmpty()) {
            System.out.println("无可用网络接口");
            return new ArrayList<>();
        }

        try (MulticastSocket socket = new MulticastSocket(null)) {
            socket.setReuseAddress(true);
            // 修改：使用随机端口而不是固定的 3702 端口
            socket.bind(new InetSocketAddress(0));
            socket.setTimeToLive(4);

            // 在每个可用网络上加入组播组
            for (NetworkInterface ni : usableInterfaces) {
                try {
                    socket.joinGroup(new InetSocketAddress(group, PORT), ni);
                    System.out.println("加入组播 [" + ni.getDisplayName() + "]");
                } catch (Exception e) {
                    System.out.println("加入失败 [" + ni.getDisplayName() + "]: " + e.getMessage());
                }
            }

            byte[] probeData = buildProbeMessage().getBytes(StandardCharsets.UTF_8);

            // 在每个接口上发送 Probe
            for (NetworkInterface ni : usableInterfaces) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        try {
                            socket.setInterface(addr);
                            DatagramPacket sendPacket = new DatagramPacket(probeData, probeData.length, group, PORT);
                            socket.send(sendPacket);
                            System.out.println("发送 Probe [" + ni.getDisplayName() + " / " + addr.getHostAddress() + "]");
                        } catch (Exception e) {
                            System.out.println("发送失败 [" + ni.getDisplayName() + "]: " + e.getMessage());
                        }
                        break; // 每个接口只用一个 IPv4 地址
                    }
                }
            }

            // 接收响应
            socket.setSoTimeout(timeoutMs);
            byte[] buffer = new byte[1024*8];
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);

                    try {
                        Document doc = Jsoup.parse(response, "", org.jsoup.parser.Parser.xmlParser());
                        Element xaddrsElement = doc.selectFirst("d|XAddrs");
                        if (xaddrsElement != null) {
                            String xaddrs = xaddrsElement.text().trim();
                            deviceAddresses.add(xaddrs);
                            System.out.println(">>> 发现设备: " + xaddrs);
                            String[] scopes = doc.selectFirst("d|Scopes").text().trim().split("onvif://www.onvif.org/");
                            for (String scope : scopes) {
                                System.out.println(scope);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("XML解析失败: " + e.getMessage());
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        }
        return new ArrayList<>(deviceAddresses);
    }

    public static void main(String[] args) throws Exception {
        List<String> devices = discoverDevices(5000);
        if (devices.isEmpty()) {
            System.out.println("未发现任何 ONVIF 设备");
        }
    }
}