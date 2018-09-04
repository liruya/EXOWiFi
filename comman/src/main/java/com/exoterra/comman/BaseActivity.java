package com.exoterra.comman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by liruya on 2018/4/13.
 */

public abstract class BaseActivity extends AppCompatActivity
{
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Intent intent = getIntent();
        if ( intent != null )
        {
            getIntentData( intent );
        }
        LogUtil.d( TAG, "onCreate: " );
    }

    @Override
    protected void onStart ()
    {
        super.onStart();
        LogUtil.d( TAG, "onStart: " );
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        LogUtil.d( TAG, "onResume: " );
    }

    @Override
    protected void onRestart ()
    {
        super.onRestart();
        LogUtil.d( TAG, "onRestart: " );
    }

    @Override
    protected void onPause ()
    {
        super.onPause();
        LogUtil.d( TAG, "onPause: " );
    }

    @Override
    protected void onStop ()
    {
        super.onStop();
        LogUtil.d( TAG, "onStop: " );
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();
        LogUtil.d( TAG, "onDestroy: " );
    }

    protected abstract void getIntentData( @NonNull Intent intent );
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initEvent();
}
