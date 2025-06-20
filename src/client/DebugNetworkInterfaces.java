package client;

import java.net.*;
import java.util.*;

public class DebugNetworkInterfaces {
    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                System.out.println("Interface: " + iface.getDisplayName());
                System.out.println("  Nom technique: " + iface.getName());
                System.out.println("  Active: " + iface.isUp());
                System.out.println("  Virtuelle: " + iface.isVirtual());
                System.out.println("  Loopback: " + iface.isLoopback());

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    System.out.println("    Adresse: " + addr.getHostAddress());
                }
                System.out.println("--------------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}