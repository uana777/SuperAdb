package com.lenovo.superadb.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lenovo.superadb.util.CommandExecution;

public class AdbService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String[] commons = new String[3];
        commons[0] = "setprop service.adb.tcp.port 5555";
        commons[1] = "stop adbd";
        commons[2] = "start adbd";
        CommandExecution.CommandResult result = CommandExecution.execCommand(commons, true);
        Log.d("AdbService", "net adb result = " + result);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
