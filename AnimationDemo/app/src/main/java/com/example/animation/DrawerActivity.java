package com.example.animation;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.widget.Toolbar;


public class DrawerActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉系统自带的action bar（目前是empty的）
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_drawer);

        initView();
        //设置自定义的actionBar
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.alpha, R.string.app_name);
        toggle.syncState();
        drawer.addDrawerListener(toggle);

    }
    private void initView()
    {
        toolbar=findViewById(R.id.toolbar);
        drawer=findViewById(R.id.drawer);
    }
}
