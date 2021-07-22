package com.example.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class TestView  extends View {//自定义view类
    public TestView(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_background);
        canvas.drawBitmap(bitmap,0,0,paint);
        if (bitmap.isRecycled()){
            bitmap.recycle();
        }
    }
}
