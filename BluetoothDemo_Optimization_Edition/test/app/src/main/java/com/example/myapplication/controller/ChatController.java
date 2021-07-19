package com.example.myapplication.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.example.myapplication.connect.AcceptThread;
import com.example.myapplication.connect.ConnectThread;

import java.io.UnsupportedEncodingException;
import android.os.Handler;

public class ChatController {
    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;

    //网络协议的处理函数
//    private class ChatProtocol implements  ProtocolHandler<String>
    private class ChatProtocol
    {
        private static final String CHARSET_NAME="utf-8";

        public byte[] endcodePackage(String data)
        {
            if(data==null)
            {
                return new byte[0];
            }
            else{
                try{
                    return data.getBytes(CHARSET_NAME);
                }catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                    return  new byte[0];
                }
            }
        }

        //我们用到的更多可能是解包把——把网上是数据转化成我们想要的结构体
        public String decodePackage(byte[] netData)
        {
            if(netData==null)
            {
                return "";
            }
            try{
                return new String(netData,CHARSET_NAME);
            }catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                return "";
            }
        }
    }

    private ChatProtocol mProtocol =new ChatProtocol();

    //与服务器连接进行聊天
    public void startChat(BluetoothDevice device, BluetoothAdapter adapter, Handler handler)
    {
        mConnectThread=new ConnectThread(device,adapter,handler);
        mConnectThread.start();
    }
    //等待客户端来连接
    public void waitingForFriends(BluetoothAdapter adapter,Handler handler)
    {
        mAcceptThread=new AcceptThread(adapter,handler);
        mAcceptThread.start();
    }

    //发出消息
    public void sendMessage(String msg)
    {
        byte[] data=mProtocol.endcodePackage(msg);
        if(mConnectThread!=null)
        {
            mConnectThread.sendData(data);
        }
        else if (mAcceptThread!=null)
        {
            mAcceptThread.sendData(data);
        }
    }

    //网络数据解码
    public String decodeMessage(byte[] data)
    {
        return mProtocol.decodePackage(data);
    }

    //停止聊天
    public void stopChat()
    {
        if(mConnectThread!=null)
        {
            mConnectThread.cancel();
        }
        else if (mAcceptThread!=null)
        {
            mAcceptThread.cancel();
        }
    }

    public void stopListening()
    {
        if(mAcceptThread!=null)
        {
            mAcceptThread.cancel();
        }
    }

    public void disconnect()
    {
        if(mConnectThread!=null)
        {
            mConnectThread.cancel();
        }
    }


    //单例的写法——意思就是一个时间只能和一个人进行chat...
    //那么我们就需要思考我们是否需要和多个外设进行交流？
    private static  class ChatControllerHolder{
        private  static ChatController mInstance=new ChatController();
    }
    public static ChatController getInstance(){
        return ChatControllerHolder.mInstance;
    }
}
