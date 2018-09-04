package com.exoterra.exowifi.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exoterra.comman.BaseActivity;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.main.MainActivity;
import com.exoterra.exowifi.register.RegisterActivity;

import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class LoginActivity extends BaseActivity implements ILoginView
{
    private Toolbar login_toolbar;
    private EditText login_et_email;
    private EditText login_et_password;
    private Button login_btn_signin;
    private Button login_btn_skip;
    private TextView login_tv_forget;
    private TextView login_tv_signup;
    private ProgressBar login_pb;

    private LoginPresenter mPresenter;
    private boolean mBackwardEnabled;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {
        mBackwardEnabled = intent.getBooleanExtra( "allow_backward", false );
    }

    @Override
    protected void initView()
    {
        login_toolbar = findViewById( R.id.login_toolbar );
        login_et_email = findViewById( R.id.login_et_email );
        login_et_password = findViewById( R.id.login_et_password );
        login_btn_signin = findViewById( R.id.login_btn_signin );
        login_btn_skip = findViewById( R.id.login_btn_skip );
        login_tv_forget = findViewById( R.id.login_tv_forget );
        login_tv_signup = findViewById( R.id.login_tv_signup );
        login_pb = findViewById( R.id.login_pb );

        login_toolbar.setTitle( R.string.sign_in );
        if ( mBackwardEnabled )
        {
            login_toolbar.setNavigationIcon( R.drawable.ic_arrow_back_white_36dp );
        }
    }

    @Override
    protected void initData()
    {
        XLinkSDK.start();
        mPresenter = new LoginPresenter( this );
    }

    @Override
    protected void initEvent()
    {
        if ( mBackwardEnabled )
        {
            login_toolbar.setNavigationOnClickListener( new View.OnClickListener() {
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

        login_btn_signin.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                mPresenter.login();
            }
        } );

        login_btn_skip.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                toMainActivity();
            }
        } );

        login_tv_forget.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {

            }
        } );

        login_tv_signup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                toRegisterActivity();
            }
        } );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
        if ( data != null && requestCode == 0 && resultCode == 0 )
        {
            String email = data.getStringExtra( "account" );
            String password = data.getStringExtra( "password" );
            setAccount( email );
            setPassword( password );
        }
    }

    @Override
    public void setAccount( String account )
    {
        login_et_email.setText( account );
    }

    @Override
    public void setPassword( String password )
    {
        login_et_password.setText( password );
    }

    @Override
    public String getAccount()
    {
        return login_et_email.getText()
                             .toString();
    }

    @Override
    public String getPassword()
    {
        return login_et_password.getText()
                                .toString();
    }

    @Override
    public void showProgress()
    {
        login_pb.setVisibility( View.VISIBLE );
    }

    @Override
    public void dismissProgress()
    {
        login_pb.setVisibility( View.GONE );
    }

    @Override
    public boolean isProgressShowing()
    {
        return login_pb.getVisibility() == View.VISIBLE;
    }

    @Override
    public void toMainActivity()
    {
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        finish();
    }

    @Override
    public void toRegisterActivity()
    {
        Intent intent = new Intent( this, RegisterActivity.class );
        startActivityForResult( intent, 0 );
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
