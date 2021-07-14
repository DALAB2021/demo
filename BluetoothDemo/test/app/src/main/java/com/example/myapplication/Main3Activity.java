package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.connect.AcceptThread;
import com.example.myapplication.connect.ConnectThread;
import com.example.myapplication.connect.Constant;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    private BlueToothController mController = new BlueToothController();
    private Toast mToast;
    private TextView logView;
    public static final int REQUEST_CODE = 0;
    public static final int ACCESS_LOCATION = 1;

    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();
    private ListView mListView;
    private DeviceAdapter mAdapter;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private Handler mUIHandler = new MyHandler();

    //此外还需要一个广播来检测
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //蓝牙打开是一个过程,异步的操作...手机上可以看到会转圈
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);//获取本地蓝牙的当前状态
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    showToast("STATE_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    showToast("STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    showToast("STATE_TURNING_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    showToast("STATE_TURNING_OFF");
                    break;
                default:
                    showToast("Unkown STATE");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main3);
        logView = (TextView) findViewById(R.id.LogText);
        initUI();

        getPermission();//授权
//        mListView=(ListView)findViewById(R.id.listview);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);//当状态改变的时候会收到广播
        registerReceiver(receiver, filter);

        IntentFilter lookFilter = new IntentFilter();
        //开始查找
        lookFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        lookFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //找到设备
        lookFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        lookFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        lookFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, lookFilter);


        mController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    //初始化用户界面
    private void initUI() {
        mListView = findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(bindDeviceClick);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//首先需要知道是那种action的类型(因为上面add了很多种)
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                logView.setText("ACTION_DISCOVERY_STARTED");
//                setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                logView.setText("ACTION_DISCOVERY_FINISHED");
//                setProgressBarIndeterminateVisibility(false);
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                logView.setText("Found One");

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //但是找到的可能会重复 或者 是null？所以需要先过滤一下？
                String name = device.getName();
                boolean dup = false;
                if (name == null) {
                    logView.setText("Null");
                } else {//name!=null
                    //要求不重复...
                    for (BluetoothDevice bd : mDeviceList) {
                        if (name.equals(bd.getName())) {
                            dup = true;
                            break;
                        }
                    }
                    for (BluetoothDevice Bd : mBondedDeviceList)//已经绑定的也需要比较，不能重复
                    {
                        if (name.equals(Bd.getName())) {
                            dup = true;
                            break;
                        }
                    }
                    if (!dup)//不是空且不重复
                    {
                        //找到一个,添加一个
                        mDeviceList.add(device);
                        mAdapter.notifyDataSetChanged();
                    }
                }




            }
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action))//字符串的比较,把常量放前面
            {
//                logView.setText("scanMode");
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);//获取当前的mode
//                logView.setText( String.valueOf(scanMode));
//                logView.setText(String.valueOf(0));
                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//                    setProgressBarIndeterminateVisibility(true);//进度条
                } else {
//                    setProgressBarIndeterminateVisibility(false);
                }

            }
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                logView.setText("ACTION_BOND_STATE_CHANGED");
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice == null) {
                    showToast("无设备");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if (status == BluetoothDevice.BOND_BONDED) {
                    showToast("已绑定" + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_BONDING) {
                    showToast("正在绑定" + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_NONE) {
                    showToast("未绑定" + remoteDevice.getName());
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)//如果我们点击接受,那么就返回OK,否则CANCLE
        {
            showToast("打开成功");
        } else {//RESULT_CANCELED
            showToast("打开失败");
        }
    }

    public void isSupportBlueTooth(View view) {
        boolean ret = mController.isSupportBlueTooth();
        showToast("support Bluetooth?" + ret);
    }

    public void isBlueToothEnable(View view) {
        boolean ret = mController.getBlueToothStatus();
        showToast(" Bluetooth enable?" + ret);
    }

    public void requestTurnOnBlueTooth(View view) {
        mController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    public void turnOffBlueTooth(View view) {
        mController.turnOffBlueTooth();
    }

    public void enableVisibly(View view) {
        mController.enableVisibly(this);//上下文
    }

    public void discoverDevice(View view) {
        showToast("开始查找");
//        showToast(mDeviceList.get(0).getName());
        mAdapter.refresh(mDeviceList);
        mController.findDevice();//查找的结果是通过广播的方式传递的
        mListView.setOnItemClickListener(bindDeviceClick);
        showToast("开始查找end" + mDeviceList.isEmpty());

    }

    //查看已绑定
    public void getDeviceList(View view) {
        mBondedDeviceList = mController.getBondedDeviceList();
        mAdapter.refresh(mBondedDeviceList);
//        mListView.setOnItemClickListener(null);//已经绑定好了就设为null
        mListView.setOnItemClickListener(bondedDeviceClick);
    }

    //开始监听
    public void startListening(View view)
    {
        //启动一个线程
        if( mAcceptThread != null) {
            mAcceptThread.cancel();
        }
        mAcceptThread = new AcceptThread(mController.getAdapter(), mUIHandler);
        mAcceptThread.start();
        logView.setText("开始监听");
    }

    //停止监听
    public void stopListening(View view)
    {
        if(mAcceptThread!=null)
        {
            mAcceptThread.cancel();
        }
        logView.setText("结束监听");
    }

    //断开连接
    public void disconnect(View view)
    {
        if(mConnectThread!=null)
        {
            mConnectThread.cancel();
        }
        logView.setText("断开连接");
    }

    //发送hello
    public void sayHello(View view)
    {
        say("Hello");
        logView.setText("say Hello");
    }
    private void say(String word) {
        if (mAcceptThread != null) {
            try {
                mAcceptThread.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        else if( mConnectThread != null) {
            try {
                mConnectThread.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case Constant.MSG_GOT_DATA:
                    showToast("data:" + String.valueOf(message.obj));
                    break;
                case Constant.MSG_ERROR:
                    showToast("error:" + String.valueOf(message.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    showToast("连接到服务端");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    showToast("找到服务端");
                    break;
            }
        }
    }
    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    //绑定的方法,每一个item被点击了之后就可以进行绑定
    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = mDeviceList.get(position);
            device.createBond();
        }
    };
    private AdapterView.OnItemClickListener bondedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mBondedDeviceList.get(i);
            if (mConnectThread != null) {
                mConnectThread.cancel();
            }
            mConnectThread = new ConnectThread(device, mController.getAdapter(), mUIHandler);//把网络数据发到UI...
            mConnectThread.start();
        }
    };


    //下面这一段都是为了获取权限，然后才能够搜索得到蓝牙设备
    @SuppressLint("WrongConstant")
    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            } else {
                // 已经获得权限
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION:
                if (hasAllPermissionGranted(grantResults)) {
//                    Log.d(TAG, "onRequestPermissionsResult: OK");
                } else {
//                    Log.d(TAG, "onRequestPermissionsResult: NOT OK");
                }
                break;
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    //注销广播等等
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
        }
        unregisterReceiver(receiver);
        unregisterReceiver(mReceiver);
    }
}
