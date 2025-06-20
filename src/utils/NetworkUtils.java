package utils;

import java.net.*;
import java.util.Enumeration;

public class NetworkUtils {
    public static String getEthernetIp(String interfaceName) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.getDisplayName().equalsIgnoreCase(interfaceName)) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }
}
