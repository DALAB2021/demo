package com.example.animation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class VisualizationActivity extends AppCompatActivity {

    private ImageView wound;
    private ImageView position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        ReleaseMission();
    }
    private void InitUI()
    {
        //一开始应该大家都是隐藏起来的——这里就先手动设置，不显示地用代码设置
//        findViewById(R.id.blood1).setVisibility(View.INVISIBLE);
//        findViewById(R.id.location1).setVisibility(View.INVISIBLE);
    }
    private void ReleaseMission()
    {
        //if mission1如果是第一组任务，那么就给上述两个成员元素赋予对应的值
        wound=findViewById(R.id.blood1);
        position=findViewById(R.id.location1);
        //endif
        wound.setVisibility(View.VISIBLE);
        position.setVisibility(View.VISIBLE);
        //然后还要对position进行一个旋转的处理
        //这里的旋转是顺时针旋转
        Animation rotateAnimation = new RotateAnimation(0f, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(0);
        rotateAnimation.setRepeatCount(0);
//        rotateAnimation.setInterpolator(new LinearInterpolator());
        position.startAnimation(rotateAnimation);
        //之后，随着血流量的减少伤口会逐渐地变淡
        //实际上的逻辑实现应该是每次血量刷新之后就开始一个动画进行切换？动画的duration也就是两次更新的间隔（频率或许可以是固定的）
        Animation fade=new AlphaAnimation(1.0f,0.3f);
        fade.setDuration(3000);//1s
        fade.setFillAfter(true);
        fade.setFillBefore(true);
        wound.startAnimation(fade);
    }
}
