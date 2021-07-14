package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import java.util.UUID;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ChatService {
    private static final String NAME = "BluetoothChat";
    // UUID-->通用唯一识别码，能唯一辨识资讯
    private static final UUID MY_UUID = UUID
            .fromString("fa87c0d0-afac-llde-8a39-0800200c9a66");
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public ChatService(Context context, Handler handler) {//构造函数
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }
    private synchronized void setState(int state) {
        mState = state;
//        mHandler.obtainMessage(Main2Activity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
    public synchronized int getState() {
        return mState;
    }
    public synchronized void start() {//初始化
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    //取消CONNECTING和CONNECTED状态下的相关线程，然后运行新的mConnectThread线程
    public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    //开启一个ConnectedThread来管理对应的当前连接。之前先取消任意现存的mConnectThread、
    //mConnectedThread 、mAcceptThread 线程，然后开启新 mConnectedThread ，传入当前刚刚接受的
    //socket连接
    //最后通过Handler来通知UI连接
    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        Message msg = mHandler.obtainMessage(Main2Activity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Main2Activity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }
    //停止所有相关线程，设当前状态为NONE
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            setState(STATE_NONE);
        }
    }

    //在 STATE_CONNECTED 状态下，调用 mConnectedThread 里的 write 方法，写入 byte
    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    //连接失败的时候处理，通知ui ,并设为STATE一LISTEN状态
    private void connectionFailed() {
        setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage(Main2Activity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Main2Activity.TOAST, "链接不到设备");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    //当连接失去的时候，设为STATE_LISTEN状态并通知UI
    private void connectionLost() {
        setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage(Main2Activity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Main2Activity.TOAST, "设备链接中断");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    //创建监听线程，准备接受新连接。使用阻塞方式调用BluetoothServerSocket.accept()
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }
        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    synchronized (ChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }
        }
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    // 连接进程，专门用来对外发出连接对方蓝牙的请求并进行处理
    // 构造函数里，通过BluetoothDevice.createRfcommSocketToServiceRecord(),
    // 从待连接的device产生BluetoothSocket.然后在run方法中connect ,
    // 成功后调用BluetoothChatSevice的connected() 方法。定义cancel()再关闭线程时能关闭相关Socket
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }
        public void run() {
            setName("ConnectThread");
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                }
                ChatService.this.start();
                return;
//                synchronized (ChatService.this) {
//                    mConnectThread = null;
//                }
//                connected(mmSocket, mmDevice);
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    // 双方蓝牙连接后一直运行的线程。构造函数中设置输入输出流
    // Run方法中使用阻塞模式的Inputstream.read()循环读取输入流，
    // 然后post到UI线程中更新聊天信息。也提供了write()将聊天信息写入输出流传输至对方，传输成功后回写入UI线程，最后cancle()关闭连接的socket
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(Main2Activity.MESSAGE_READ, bytes,
                            -1, buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(Main2Activity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


}