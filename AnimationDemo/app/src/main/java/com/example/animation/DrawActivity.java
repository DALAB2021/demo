package com.example.animation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.animation.SelfDefineViews.BrokenLineChart;
import com.example.animation.SelfDefineViews.DrawLineChart;
import com.example.animation.SelfDefineViews.MyView;

import java.util.ArrayList;
import java.util.Random;


public class DrawActivity extends AppCompatActivity {

    ArrayList<Float> Values = new ArrayList<Float>();
    int sampleDistance = 5;//采样的间距，在demo里面体现为每点击n次按钮才会显示一次
    ArrayList<Float> storage = new ArrayList<Float>();
    EditText lowerBound;
    EditText upperBound;
    TextView textView;
    int lowerValue = 200;
    int upperValue = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        init();
        Values.add(0.0f);
        String get = getIntent().getStringExtra("percent");
        System.out.println("get:" + get);//可以收到,但是万一没有呢？
        int upper=getIntent().getIntExtra("upper",200);//果然，是有默认值的，那么字符串的默认值是不是“”？_null
        int lower=getIntent().getIntExtra("lower",600);
        System.out.println("upper:" + upper);
        System.out.println("lower:" + lower);

        textView = findViewById(R.id.textView2);
        textView.setText("ok");

        /*下面这一部分是上下限的出入框的逻辑*/
        lowerBound = findViewById(R.id.lowerBound);
//        lowerBound.setInputType(InputType.TYPE_CLASS_NUMBER);//只能输入数字//这个在布局文件里面设置

        upperBound = findViewById(R.id.upperBound);
//        upperBound.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);//只能输入数字,DECIMAL的意思是可以小数
        //我可以自己写一个函数来限制两者的输入范围...

        System.out.println("string" + String.valueOf(lowerValue));
        String s1 = String.valueOf(lowerValue);
        lowerBound.setText(s1);
        upperBound.setText(String.valueOf(upperValue));
        setFoucus(lowerBound);
        setFoucus(upperBound);
        setRegion(lowerBound);
        setRegion(upperBound);
    }
    private void setFoucus(final EditText et)
    {
        et.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    if (lowerValue > upperValue || lowerValue == upperValue) {
                        upperValue = lowerValue + 1;
                        upperBound.setText(String.valueOf(upperValue));
                    }
                }
            }
        });
    }
    private void setRegion(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et.removeTextChangedListener(this);//需要先解除绑定，然后修改text ，然后再绑定，否则会死循环
                if (lowerBound.getText() != null && !lowerBound.getText().toString().equals("")) {
                    lowerValue = Integer.parseInt(lowerBound.getText().toString());

                    lowerBound.setText(String.valueOf(lowerValue));
                } else {
                    lowerValue = 0;
//                    lowerBound.setText(String.valueOf(lowerValue));
                    lowerBound.setText("");

                }
                if (upperBound.getText() != null && !upperBound.getText().toString().equals("")) {
                    upperValue = Integer.parseInt(upperBound.getText().toString());
                    upperBound.setText(String.valueOf(upperValue));
                } else {
                    upperValue = 1;
//                    upperBound.setText(String.valueOf(upperValue));
                    upperBound.setText("");

                }
//
//                //主要是限制上下限的大小范围，不能下限≥上限
//                if(lowerValue>upperValue||lowerValue==upperValue)
//                {
//                    upperValue=lowerValue+1;
//                    upperBound.setText(String.valueOf(upperValue));
//                }
                String content = et.getText().toString();
                et.setSelection(content.length());//将光标移至文字末尾
                et.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.layout);//获取帧布局管理器，自己新建一个就可以了


//        final MyView myView=new MyView(DrawActivity.this);
////        myView.setOnTouchListener(new View.OnTouchListener() {//触摸监听事件
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                myView.invalidate();
////                return true;
////            }
////        });
////        myView.invalidate();//通知view组件重绘
//        TextView testView=new TextView(this);
//        testView.setTextSize(20);
//        testView.setTextColor(getResources().getColor(R.color.colorAccent));
//        testView.setText("ok");
//        frameLayout.addView(testView);
//        frameLayout.addView(myView);
//        final BrokenLineChart brokenchart=new BrokenLineChart(DrawActivity.this);//final定义了之后就不能修改了...但是我想要动态修改？
//        frameLayout.addView(brokenchart);
        DrawLineChart chart = findViewById(R.id.chart);
        chart.setBrokenLineLTRB(50, 15, 10, 5);
        chart.setRadius(2.5f);
        chart.setCircleWidth(1f);
        chart.setBorderTextSize(15);//修改边框文字大小
        chart.setBrokenLineTextSize(10);//修改这线上文字大小
        chart.setMaxVlaue(600);
        chart.setMinValue(0);
        chart.setNumberLine(5);//5根线
        chart.setBorderWidth(1f);
        chart.setBrokenLineWidth(1.5f);
        chart.setBorderTransverseLineWidth(1.0f);//中间横线的宽度
        chart.setUpper(600.0f);
        chart.setLower(200.0f);

        Random random = new Random();
        float[] floats = new float[24];
        for (int i = 0; i < floats.length; i++) {
            float f = random.nextFloat();
            floats[i] = f * 60 - 10;
            Log.i("onCreate", "onCreate: f" + f);
        }
        chart.setValue(floats);
    }

    public void change(View view) {
        DrawLineChart chart = findViewById(R.id.chart);
        Random random = new Random();
        float f = random.nextFloat();
        Values.add(f * 600);
        float[] floats = new float[Values.size()];
        int index = 0;
        for (final Float value : Values) {
            floats[index++] = value;
        }
//        float[] floats=new float[30];//如果修改常长度？确实可以完全地显示出来
//        for (int i = 0; i < floats.length; i++) {
//            float f=  random.nextFloat();
//            floats[i]=f*600;
//            Log.i("onCreate", "onCreate: f"+f);
//        }
        chart.setValue(floats);
        chart.invalidate();//重绘
    }

    public void changeWithSample(View view) {
        DrawLineChart chart = findViewById(R.id.chart);
        Random random = new Random();
        float f = random.nextFloat();
        float data = f * 600;//这个是生成的data，之后当然是用传递过来的数据
        storage.add(data);
        if (storage.size() == sampleDistance) {
            float average = 0;
            for (final Float value : storage) {
                average += value;
            }
            average /= sampleDistance;
            System.out.println("average：" + average);
            Values.add(average);
            float[] floats = new float[Values.size()];
            int index = 0;
            for (final Float value : Values) {
                floats[index++] = value;
            }
            chart.setValue(floats);
            chart.invalidate();//重绘
            storage.clear();
        }
    }
}
