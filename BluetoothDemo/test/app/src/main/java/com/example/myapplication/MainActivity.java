package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    TextView t1;
    EditText edt1,edt2;
    Button but1,blueButton;
    BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
//获取蓝牙适配器
    public ArrayAdapter adapter;//适配器
    ListView listView=(ListView)findViewById(R.id.list);//控件列表，用来显示(这一步就已经关联/初始化了)
    //定义两个列表，存蓝牙设备的地址和名字（？）
    public ArrayList<String> arrayList=new ArrayList<>();
    public ArrayList<String> deviceName=new ArrayList<>();
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1=(TextView)findViewById(R.id.textView3);
        but1=(Button)findViewById(R.id.button);
        but1.setOnClickListener(new L2());//绑定事件
        edt1=(EditText)this.findViewById(R.id.editText);
        edt2=(EditText)this.findViewById(R.id.editText2);
        blueButton=(Button)this.findViewById(R.id.bt);
        blueButton.setOnClickListener(new BlueTooth());

        //定义适配器
        adapter=new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1, deviceName);
        //给列表添加适配器
        listView.setAdapter(adapter);

//        //定义列表Item的点击事件
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//                device = adapter.getRemoteDevice(arrayList.get(i));
//                mge = edt1.getText().toString() + "\r\n";//获得编辑文本框里的文字
//                ClientThread clientThread = new ClientThread(device, mge, context);
//                clientThread.start();
//            }
//        });
    }
    public void Jump(View view){
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, Server.class);
//        startActivity(intent);
    }/*按钮函数响应*/

    //@Override
    protected void onDestroy(){//在关闭app的时候自动注销广播
        super.onDestroy();//解除注册
        unregisterReceiver(bluetoothReceiver);
    }
//    class ClientThread extends Thread {
//
//        public void run() {
//            final BluetoothSocket socket = (BluetoothSocket) device.getClass().getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
//        }
//    }
//    private class AcceptThread extends Thread {
//    // 本地服务器套接字
//    private final BluetoothServerSocket mServerSocket;
//    public AcceptThread() {
//        BluetoothServerSocket tmp = null;
//        // 创建一个新的侦听服务器套接字
//        try {
//            tmp = blueadapter.listenUsingRfcommWithServiceRecord(
//                    BluetoothAdapter.getDefaultAdapter().getName(),
//                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//            );
//            //tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID);
//        } catch (IOException e) {
//            //Log.e(TAG, "listen() failed", e);
//        }
//        mServerSocket = tmp;
//    }
//
//    public void run() {
//        BluetoothSocket socket = null;
//        // 循环，直到连接成功
//        while (mState != STATE_CONNECTED) {
//            try {
//                // 这是一个阻塞调用 返回成功的连接
//                // mServerSocket.close()在另一个线程中调用，可以中止该阻塞
//                socket = mServerSocket.accept();
//            } catch (IOException e) {
//               // Log.e(TAG, "accept() failed", e);
//                break;
//            }
//            // 如果连接被接受
//            if (socket != null) {
//                synchronized (BluetoothChatUtil.this) {
//                    switch (mState) {
//                        case STATE_LISTEN:
//                        case STATE_CONNECTING:
//                            // 正常情况。启动ConnectedThread。
//                            connected(socket, socket.getRemoteDevice());
//                            break;
//                        case STATE_NONE:
//                        case STATE_CONNECTED:
//                            // 没有准备或已连接。新连接终止。
//                            try {
//                                socket.close();
//                            } catch (IOException e) {
//                                Log.e(TAG, "Could not close unwanted socket", e);
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//        if (D) Log.i(TAG, "END mAcceptThread");
//    }
//
//    public void cancel() {
//        if (D) Log.d(TAG, "cancel " + this);
//        try {
//            mServerSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "close() of server failed", e);
//        }
//    }
//}



    class BlueTooth implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //在这里添加蓝牙事件的代码即可
            if(blueadapter==null)
                //表示手机不支持蓝牙
                return;

            if(!blueadapter.isEnabled())//判断本机蓝牙是否打开
            {
                //如果没有，则打开蓝牙
                blueadapter.enable();
                //同理，使用disable可以关闭蓝牙
            }

            //然后是搜索蓝牙，让自己的蓝牙设备可被别人发现（？
            if(blueadapter.getScanMode()!=BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)//不在可被搜索的范围
            {
                Intent discoverableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);//设置本机蓝牙在300秒内可见
                startActivity(discoverableIntent);//开始上面的那个活动
            }

            //反过来，如果想搜索别人的设备，就可以在按钮的点击事件中调用startDiscover()来搜索蓝牙
            //写成一个函数罢
            doDiscovery();

            //定义广播以及处理广播的消息
            IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);//注册广播接收信号
            registerReceiver(bluetoothReceiver,intentFilter);//用BroadcastReceiver来取得结果，并显示

            //配对蓝牙设备
            //作为服务端


        }
    }
    public void doDiscovery()
    {
        if(blueadapter.isDiscovering())
        {
            //判断蓝牙是否正在扫描，如果是，取消扫描方法，否则开始扫描
            blueadapter.cancelDiscovery();
        }
        else{
            blueadapter.startDiscovery();
        }
    }
    private  final BroadcastReceiver bluetoothReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))//如果我们做的是found这个action
            {
                device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//获取到传过来的device
                deviceName.add("设备名："+device.getName()+"\n"+"设备地址:"+device.getAddress()+"\n");//将搜索到的蓝牙名称和地址添加到列表。
                arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到arrayList这个列表。
                adapter.notifyDataSetChanged();//更新
           }
        }
    };
    public void applypermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //检查是否已经给了权限
            int checkpermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkpermission != PackageManager.PERMISSION_GRANTED) {//没有给权限
                Log.e("permission", "动态申请");
                //参数分别是当前活动，权限字符串数组，requestcode
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "已授权", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "拒绝授权", Toast.LENGTH_SHORT).show();
        }

    }

    class L1 implements  View.OnClickListener
    {
        @Override
        public void onClick(View view)//注意这里的函数名字不是大写的嗷
        {
            t1.setText("你好，这是一个按钮的事件相应");
        }
    }
    class L2 implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            String inputText1=edt1.getText().toString();
            String inputText2=edt2.getText().toString();
            int num1=Integer.valueOf(inputText1).intValue();
            int num2=Integer.valueOf(inputText2).intValue();
            num1=num1+num2;
            inputText1=String.valueOf(num1);
            t1.setText(inputText1);
        }
    }
}
