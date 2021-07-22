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

import java.util.ArrayList;
import java.util.Random;


public class DrawActivity extends AppCompatActivity {

    ArrayList<Float> Values=new ArrayList<Float>();
    int sampleDistance=5;//采样的间距，在demo里面体现为每点击n次按钮才会显示一次
    ArrayList<Float> storage=new ArrayList<Float>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        init();
        Values.add(0.0f);
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
        chart.setBrokenLineLTRB(50,15,10,5);
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
        float f= random.nextFloat();
        Values.add(f*600);
        float[] floats=new float[Values.size()];
        int index = 0;
        for (final Float value: Values) {
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
    public void changeWithSample(View view)
    {
        DrawLineChart chart = findViewById(R.id.chart);
        Random random=new Random();
        float f= random.nextFloat();
        float data=f*600;//这个是生成的data，之后当然是用传递过来的数据
        storage.add(data);
        if(storage.size()==sampleDistance)
        {
            float average=0;
            for (final Float value: storage) {
                average+=value;
            }
            average/=sampleDistance;
            System.out.println("average："+average);
            Values.add(average);
            float[] floats=new float[Values.size()];
            int index = 0;
            for (final Float value: Values) {
                floats[index++] = value;
            }
            chart.setValue(floats);
            chart.invalidate();//重绘
            storage.clear();
        }
    }
}
