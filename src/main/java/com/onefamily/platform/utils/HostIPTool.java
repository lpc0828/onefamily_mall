package com.onefamily.platform.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class HostIPTool {

    public static String getLocalHostIP() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            return "";
        }

    }

    public static String getLocalHost() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException e) {
            return "";
        }

    }

    public static String getLocalIP() {
        try {
            return (InetAddress.getLocalHost()).getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }

    }

    public static String getLocalIP_A() {
        return getLocalIP_Seg(0);
    }
    public static String getLocalIP_B() {
        return getLocalIP_Seg(1);
    }
    public static String getLocalIP_C() {
        return getLocalIP_Seg(2);
    }
    public static String getLocalIP_D() {
        return getLocalIP_Seg(3);
    }
    private static String getLocalIP_Seg(int seg) {
        try {
            String ip = (InetAddress.getLocalHost()).getHostAddress();
            String[] d = StringUtils.split(ip, ".");
            if (d != null && d.length == 4 && seg <4)
                return d[seg];
            else
                return "";
        } catch (UnknownHostException e) {
            return "";
        }
        
    }
	public static String getMACAddr() throws SocketException, UnknownHostException {
		NetworkInterface netInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
		// 获得Mac地址的byte数组
		byte[] macAddr = netInterface.getHardwareAddress();
		// 循环输出
		StringBuilder tmpSb = new StringBuilder();
		for (byte b : macAddr) {
			tmpSb.append(toHexString(b));
		}
		return tmpSb.toString();
	}

	private static String toHexString(int integer) {
		// 将得来的int类型数字转化为十六进制数
		String str = Integer.toHexString((int) (integer & 0xff));
		// 如果遇到单字符，前置0占位补满两格
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}

}
