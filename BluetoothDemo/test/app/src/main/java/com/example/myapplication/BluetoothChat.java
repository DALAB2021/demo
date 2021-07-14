//package com.example.myapplication;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.inputmethod.EditorInfo;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class BluetoothChat extends Activity {
//
//    public static final int MESSAGE_STATE_CHANGE = 1;
//    public static final int MESSAGE_READ = 2;
//    public static final int MESSAGE_WRITE = 3;
//    public static final int MESSAGE_DEVICE_NAME = 4;
//    public static final int MESSAGE_TOAST = 5;
//    public static final String DEVICE_NAME = "device_name";
//    public static final String TOAST = "toast";
//    private static final int REQUEST_C0NNECT_DEVICE = 1;
//    private static final int REQUEST_ENABLE_BT = 2;
//    private TextView mTitle;
//    private ListView mConversationView;
//    private EditText mOutEditText;
//    private Button mSendButton;
//    private String mConnectedDeviceName = null;
//    private ArrayAdapter<String> mConversationArrayAdapter;
//    private StringBuffer mOutStringBuffer;
//    private BluetoothAdapter mBluetoothAdapter = null;
//    private ChatService mChatService = null;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //设置窗口布局为自定义标题
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//        setContentView(R.layout.main);
//
//        //设置窗口标题为布局文件
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
//        mTitle = (TextView) findViewById(R.id.title_left_text);
//        mTitle.setText(R.string.app_name);
//        mTitle = (TextView) findViewById(R.id.title_right_text);
//
//        // 得到本地蓝牙适配器
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        //若当前设备不支持蓝牙功能
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//        } else {
//            if (mChatService == null)
//                setupChat();
//        }
//    }
//
//    @Override
//    public synchronized void onResume() {
//        super.onResume();
//        if (mChatService != null) {
//            if (mChatService.getState() == ChatService.STATE_NONE) {
//                mChatService.start();
//            }
//        }
//    }
//
//    private void setupChat() {
//        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
//        mConversationView = (ListView) findViewById(R.id.in);
//        mConversationView.setAdapter(mConversationArrayAdapter);
//        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
//        mOutEditText.setOnEditorActionListener(mWriteListener);
//        mSendButton = (Button) findViewById(R.id.button_send);
//        mSendButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                TextView view = (TextView) findViewById(R.id.edit_text_out);
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//        });
//
//        mChatService = new ChatService(this, mHandler);
//        mOutStringBuffer = new StringBuffer("");
//    }
//
//    @Override
//    public synchronized void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mChatService != null)
//            mChatService.stop();
//    }
//
//    private void ensureDiscoverable() {
//        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
//        }
//    }
//
//    private void sendMessage(String message) {
//        if (mChatService.getState() != ChatService.STATE_CONNECTED) {
//            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (message.length() > 0) {
//            byte[] send = message.getBytes();
//            mChatService.write(send);
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
//        }
//    }
//
//    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
//
//        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//            return true;
//        }
//    };
//
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_STATE_CHANGE:
//
//                    switch (msg.argl) {
//                        case ChatService.STATE_CONNECTED:
//                            mTitle.setText(R.string.title_connected_to);
//                            mTitle.append(mConnectedDeviceName);
//                            mConversationArrayAdapter.clear();
//                            break;
//                        case ChatService.STATE_CONNECTING:
//                            mTitle.setText(R.string.title_connecting);
//                            break;
//                        case ChatService.STATE_LISTEN:
//                        case ChatService.STATE_NONE:
//                            mTitle.setText(R.string.title_not_connected);
//                            break;
//                    }
//                    break;
//
//                case MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("我: " + writeMessage);
//                    break;
//
//                case MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    String readMessage = new String(readBuf, 0, msg.argl);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":" + readMessage);
//                    break;
//
//                case MESSAGE_DEVICE_NAME:
//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "链接到" + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    break;
//                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                if (resultCode == Activity.RESULT_OK) {
//                    String address = data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS);
//                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//                    mChatService.connect(device);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                if (resultCode == Activity.RESULT_OK) {
//                    setupChat();
//                } else {
//                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//        }
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.scan:
//                Intent serverIntent = new Intent(this, DeviceList.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                return true;
//            case R.id.discoverable:
//                ensureDiscoverable();
//                return true;
//            case R.id.back:
//                finish();
//                System.exit(0);
//                return true;
//        }
//        return false;
//    }
//}
