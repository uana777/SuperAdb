package com.lenovo.superadb.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 设备网络信息
 */

public class NetWorkUtil {

    private static final String TAG = NetWorkUtil.class.getSimpleName();


    /**
     * 检查网络是否可用
     */
    public static boolean checkEnable(Context paramContext) {

        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isAvailable());
    }

    public static String getIPAddress(Context context) {
        String ip = null;
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();

                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();

                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                ip = inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                @SuppressLint("WifiManagerPotentialLeak")
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                ip = intIP2StringIP(wifiInfo.getIpAddress());
            }
        }

        if (ip == null || ip.isEmpty()) {
            String[] commons = new String[1];
            commons[0] = "ifconfig eth0";

            CommandExecution.CommandResult result = CommandExecution.execCommand(commons, true);

            if (result.result == 0) {
                ip = result.successMsg.split("inet addr:")[1].split("  ")[0];
            }

            Log.d(TAG, "getIPAddress() IP = " + ip);
        }
        return ip;
    }

    /**
     *      * 将得到的int类型的IP转换为String类型
     *      *
     *      * @param ip
     *      * @return
     *      
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

}
