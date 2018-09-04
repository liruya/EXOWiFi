package com.exoterra.exowifi.launch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.exoterra.comman.BaseActivity;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.login.LoginActivity;
import com.exoterra.exowifi.main.MainActivity;

import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class LaunchActivity extends BaseActivity implements ILaunchView
{
    private LaunchPresenter mPresenter;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_launch );

        initData();
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {

    }

    @Override
    protected void initView ()
    {

    }

    @Override
    protected void initData ()
    {
        XLinkSDK.start();
        mPresenter = new LaunchPresenter( this );
        mPresenter.getAuthorize();
    }

    @Override
    protected void initEvent ()
    {

    }

    @Override
    public void toMainActivity()
    {
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run()
            {
                Intent intent = new Intent( LaunchActivity.this, MainActivity.class );
                startActivity( intent );
                finish();
            }
        }, 1500 );
    }

    @Override
    public void toLoginActivity()
    {
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run()
            {
                Intent intent = new Intent( LaunchActivity.this, LoginActivity.class );
                intent.putExtra( "allow_backward", false );
                startActivity( intent );
                finish();
            }
        }, 1500 );
    }

    @Override
    public Context getMvpContext()
    {
        return this;
    }

    @Override
    public void toast( String msg )
    {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT )
             .show();
    }

    @Override
    public void runOnUIThread( Runnable runnable )
    {
        runOnUiThread( runnable );
    }
}
