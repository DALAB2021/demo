package com.example.animation.SelfDefineViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.example.animation.R;

public class MyView extends View {//自定义view类
    //这里的R和文件夹有关系，如果是外面的一层那么就可以...导入包？
    Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
    Paint paint=new Paint();
    public MyView(Context context){
        super(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.RED);
        canvas.drawText("画圆：", 10, 20,paint);// 画文本
        canvas.drawCircle(60, 20, 10, paint);// 小圆
        canvas.drawBitmap(bitmap, 250,360, paint);

//        canvas.drawBitmap(bitmap,0,0,paint);
//        if (bitmap.isRecycled()){
//            bitmap.recycle();
//        }
    }
}
