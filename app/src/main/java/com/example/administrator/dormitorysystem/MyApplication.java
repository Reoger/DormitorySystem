package com.example.administrator.dormitorysystem;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.example.administrator.dormitorysystem.other.GlideImageLoader;
import com.imnjh.imagepicker.PickerConfig;
import com.imnjh.imagepicker.SImagePicker;
import com.zxy.tiny.Tiny;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2017/12/3.
 */

public class MyApplication extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"b499fe8d6326dbc2623632d893910526");
        mContext=getApplicationContext();
        Tiny.getInstance().init(this);
        SImagePicker.init(new PickerConfig.Builder().setAppContext(this)
                .setImageLoader(new GlideImageLoader())
                .setToolbaseColor(Color.parseColor("#108de8"))
                .build());

    }

}
