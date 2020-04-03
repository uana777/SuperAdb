package com.lenovo.superadb.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Thread:
 * 监听adb tcpip port 可用状态
 * 不可用时通过handler回调消息
 */

public class ScanPortsThread extends Thread {

    private static final String TAG = ScanPortsThread.class.getSimpleName();

    public static final int MSG_NEED_REOPEN_TCPIP = 111;

    private boolean checkPort = false;
    private int port;
    private Handler handler;
    private Context mContext;

    public ScanPortsThread(Context context, Handler handler, int port) {
        mContext = context;
        this.handler = handler;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();

        while (true) {

            try {
                Thread.sleep(5 * 60000); // 2min

                if (!checkPort) {
                    continue;
                }

                String ip = NetWorkUtil.getIPAddress(mContext);
                if (ip == null || ip.isEmpty()) {
                    continue;
                }

                Socket socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(ip, port);
                socket.connect(socketAddress, 500);
                socket.close();

                Log.d(TAG, "Every thing is OK, Port is reachable : " + port);

            } catch (Exception e) {
                Log.e(TAG, "Port is unreachable : " + port);
                if (handler != null) {
                    handler.sendEmptyMessage(MSG_NEED_REOPEN_TCPIP);
                }
                Log.e(TAG, e.getMessage());
            }

        }
    }

    public void updatePort(int newPort) {
        this.port = newPort;
    }

    public void stopScan() {
        checkPort = false;
    }

    public void startScan() {
        checkPort = true;
    }
}
