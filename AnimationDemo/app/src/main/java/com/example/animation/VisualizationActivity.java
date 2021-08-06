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
    private ImageView left_white;
    private ImageView left_up_broken;
    private ImageView right_white;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        InitUI();
//        ReleaseMission();
    }
    private void InitUI()
    {
        left_white=findViewById(R.id.left_white);
//        left_white.setAlpha(0.0f);
        right_white=findViewById(R.id.right_white);
//        right_white.setAlpha(0.0f);
        //一开始应该大家都是隐藏起来的——这里就先手动设置，不显示地用代码设置
//        findViewById(R.id.blood1).setVisibility(View.INVISIBLE);
//        findViewById(R.id.location1).setVisibility(View.INVISIBLE);
        left_up_broken=findViewById(R.id.left_up_broken);

    }
    public void LeftUpper(View view)//可能会有比较大的冗余，但是也不太方便参数化...或者我可以通过修改图片资源的方式来实现不同的流血量？
    {
        if(wound!=null)
        {
            wound.setVisibility(View.INVISIBLE);
        }
        if(position!=null)
        {
            position.setVisibility(View.INVISIBLE);
        }
        //首先要随机生成出血情况
        int blood=ArbitaryBlood();//1表示毛细血管，2表示静脉，3表示动脉
        switch(blood)
        {   case 1:
                //其实需要根据不同的case去唤醒不同的外设...
                wound=findViewById(R.id.blood_cap_upper_left3);
                position=findViewById(R.id.blood_location_upper_left);
                break;
            case 2:
                //其实需要根据不同的case去唤醒不同的外设...
                wound=findViewById(R.id.blood_vein_upper_left);
                position=findViewById(R.id.blood_location_upper_left);//如果是静脉的话？这个position还要不要呢？或者或不会发生变化呢？
                break;
            case 3:
                wound=findViewById(R.id.blood_artery_upper_left2);
                //其实或者也可以通过修改组件的图片来源的方式来实现（如果不涉及位置变化的话——这就需要看性能如何处理了
                position=findViewById(R.id.blood_location_upper_left);
                break;
            default:
                break;
        }

//        //旋转的处理,只要这里是从70到70,并且fill before,那么就可以不会有延迟的效果了
        Animation rotateAnimation = new RotateAnimation(70f, 70, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setDuration(0);
        rotateAnimation.setRepeatCount(0);
        position.startAnimation(rotateAnimation);
        wound.setVisibility(View.VISIBLE);
//        position.setVisibility(View.VISIBLE);其实上面的动画放完了之后就自动保持visible了

    }

    private int ArbitaryBlood()//随机生成出血情况，返回int代表三种不同的血量情况
    {
        int max=4,min=1;
        int rand=(int)(Math.random()*(max-min)+min);
        System.out.println(rand);
        return rand;
    }

    private void ReleaseMission()
    {
        //if mission1如果是第一组任务，那么就给上述两个成员元素赋予对应的值
        wound=findViewById(R.id.blood_vein_upper_left);//blood1就是静脉血样，需要区分左右...
        position=findViewById(R.id.blood_location_upper_left);
        //endif
        wound.setVisibility(View.VISIBLE);
        position.setVisibility(View.VISIBLE);
        //然后还要对position进行一个旋转的处理
        //这里的旋转是顺时针旋转
        //如果是左臂，就旋转70°？
        Animation rotateAnimation = new RotateAnimation(0f, 70, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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
        //实际上可以通过直接setalpha来做。

        //手变白
        left_white.setVisibility(View.VISIBLE);
        Animation merge=new AlphaAnimation(0.0f,0.7f);
        merge.setDuration(3000);//1s
        merge.setFillAfter(true);
        merge.setFillBefore(true);
        left_white.startAnimation(merge);
    }
    public void adjustAlpha(View view)
    {
        wound.setAlpha(0.5f);
    }
    public void blink(View view)
    {
        //左上肢坏死
        left_up_broken.setVisibility(View.VISIBLE);
        Animation merge=new AlphaAnimation(0.4f,0.9f);
        merge.setDuration(1000);//1s
        merge.setFillAfter(true);
        merge.setFillBefore(true);
        merge.setRepeatCount(-1);
        merge.setRepeatMode(Animation.REVERSE);
        left_up_broken.startAnimation(merge);



//        Animation blur=new AlphaAnimation(1.0f,0.85f);
//        blur.setDuration(interval);//1s
//        blur.setFillAfter(true);
//        blur.setFillBefore(true);
//        blur.setRepeatCount(-1);//-1就是无穷次
//        blur.setRepeatMode(Animation.REVERSE);
    }
}
