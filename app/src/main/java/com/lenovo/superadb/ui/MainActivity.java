package com.lenovo.superadb.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.superadb.util.CommandExecution;
import com.lenovo.superadb.R;
import com.lenovo.superadb.util.NetWorkUtil;
import com.lenovo.superadb.util.ScanPortsThread;

public class MainActivity extends AppCompatActivity {

    ScanPortsThread scanPortsThread;

    TextView tvIp;
    EditText etPort;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ScanPortsThread.MSG_NEED_REOPEN_TCPIP) {
                initNetAdb();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        showNetInfo();

        initNetAdb();
        startScanThread();
    }

    private void initView() {
        tvIp = findViewById(R.id.ip);
        etPort = findViewById(R.id.port);

        Button startNetAdb = findViewById(R.id.start_net_adb);

        startNetAdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNetAdb();
                startScanThread();
            }
        });

        findViewById(R.id.boot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] commons = new String[1];
                commons[0] = "reboot -p";

                CommandExecution.CommandResult result = CommandExecution.execCommand(commons, true);
            }
        });

        findViewById(R.id.reboot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] commons = new String[1];
                commons[0] = "reboot";

                CommandExecution.CommandResult result = CommandExecution.execCommand(commons, true);
            }
        });

        findViewById(R.id.reboot_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] commons = new String[1];
                commons[0] = "am start -S com.lenovo.superadb/.ui.MainActivity";

                CommandExecution.CommandResult result = CommandExecution.execCommand(commons, true);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showNetInfo() {
        if (NetWorkUtil.checkEnable(this)) {
            tvIp.setText("IP: " + NetWorkUtil.getIPAddress(this));
        }
    }

    /**
     * 开启网络调试模式
     * 默认开启 5555 端口
     */
    private void initNetAdb() {

        String port = etPort.getText().toString().trim();
        if (port.isEmpty()) {
            port = "5555";
        }

        String[] commons = new String[3];
        commons[0] = "setprop service.adb.tcp.port " + port;
        commons[1] = "stop adbd";
        commons[2] = "start adbd";

        CommandExecution.CommandResult result = CommandExecution.execCommand(commons, true);

        if (result.result == 0) {
            Toast.makeText(this, "Set net ADB success " + tvIp.getText().toString() + ":" + port, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 监听adb端口状态
     */
    private void startScanThread() {
        String port = etPort.getText().toString().trim();
        if (port.isEmpty()) {
            port = "5555";
        }

        if (scanPortsThread == null) {
            scanPortsThread = new ScanPortsThread(this, handler, Integer.parseInt(port));
            scanPortsThread.start();
        } else {
            scanPortsThread.stopScan();
            scanPortsThread.updatePort(Integer.parseInt(port));
        }

        scanPortsThread.startScan();
    }
}
