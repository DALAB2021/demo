package com.example.animation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.animation.SelfDefineViews.BrokenLineChart;
import com.example.animation.SelfDefineViews.DrawLineChart;
import com.example.animation.SelfDefineViews.MyView;

import java.util.Random;

public class DrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        init();

    }
    private void init()
    {
        FrameLayout frameLayout=(FrameLayout)findViewById(R.id.layout);//获取帧布局管理器，自己新建一个就可以了


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
        chart.setBrokenLineLTRB(30,10,10,5);
        chart.setRadius(2.5f);
        chart.setCircleWidth(1f);
        chart.setBorderTextSize(50);//修改文字大小
        chart.setBrokenLineTextSize(10);
        chart.setMaxVlaue(600);
        chart.setMinValue(0);
        chart.setNumberLine(5);//5根线
        chart.setBorderWidth(1f);
        chart.setBrokenLineWidth(1.5f);
        chart.setBorderTransverseLineWidth(0.3f);
        chart.setUpper(600.0f);
        chart.setLower(200.0f);

        Random random=new Random();
        float[] floats=new float[24];
        for (int i = 0; i < floats.length; i++) {
            float f=  random.nextFloat();
            floats[i]=f*60-10;
            Log.i("onCreate", "onCreate: f"+f);
        }
        chart.setValue(floats);
    }
    public void change(View view)
    {
        DrawLineChart chart = findViewById(R.id.chart);
        Random random=new Random();
        float[] floats=new float[30];//如果修改常长度？确实可以完全地显示出来
        for (int i = 0; i < floats.length; i++) {
            float f=  random.nextFloat();
            floats[i]=f*600;
            Log.i("onCreate", "onCreate: f"+f);
        }
        chart.setValue(floats);
        chart.invalidate();//重绘
    }
}
