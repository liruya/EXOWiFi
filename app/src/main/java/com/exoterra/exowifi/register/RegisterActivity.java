package com.exoterra.exowifi.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.exoterra.comman.BaseActivity;
import com.exoterra.exowifi.R;

import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class RegisterActivity extends BaseActivity implements IRegisterView
{
    private Toolbar register_toolbar;
    private EditText register_et_email;
    private EditText register_et_nickname;
    private EditText register_et_password;
    private Button register_btn_signup;
    private ProgressBar register_pb;

    private RegisterPresenter mPresenter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {

    }

    @Override
    protected void initView()
    {
        register_toolbar = findViewById( R.id.register_toolbar );
        register_et_email = findViewById( R.id.register_et_email );
        register_et_nickname = findViewById( R.id.register_et_nickname );
        register_et_password = findViewById( R.id.register_et_password );
        register_btn_signup = findViewById( R.id.register_btn_signup );
        register_pb = findViewById( R.id.register_pb );

        register_toolbar.setTitle( R.string.signup );
        register_toolbar.setNavigationIcon( R.drawable.ic_arrow_back_white_36dp );
        setSupportActionBar( register_toolbar );
    }

    @Override
    protected void initData()
    {
        XLinkSDK.start();
        mPresenter = new RegisterPresenter( this );
    }

    @Override
    protected void initEvent()
    {
        register_btn_signup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                mPresenter.register();
            }
        } );
        register_toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                if ( !isProgressShowing() )
                {
                    finish();
                }
            }
        } );
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

    @Override
    public String getAccount()
    {
        return register_et_email.getText().toString();
    }

    @Override
    public String getNickname()
    {
        return register_et_nickname.getText().toString();
    }

    @Override
    public String getPassword()
    {
        return register_et_password.getText().toString();
    }

    @Override
    public void showProgress()
    {
        register_pb.setVisibility( View.VISIBLE );
    }

    @Override
    public void dismissProgress()
    {
        register_pb.setVisibility( View.GONE );
    }

    @Override
    public boolean isProgressShowing()
    {
        return register_pb.getVisibility() == View.VISIBLE;
    }

    @Override
    public void closeWithResult()
    {
        Intent intent = new Intent();
        intent.putExtra( "account", getAccount() );
        intent.putExtra( "password", getPassword() );
        setResult( 0, intent );
        finish();
    }
}
