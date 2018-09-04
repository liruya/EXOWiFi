package com.inledco.esptouch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exoterra.comman.BaseActivity;
import com.exoterra.comman.LogUtil;
import com.inledco.esptouch.util.EspWifiAdminSimple;

import java.util.List;

public class EsptouchActivity extends BaseActivity
{
    private Toolbar esptouch_toolbar;
    private EditText esptouch_et_ssid;
    private EditText esptouch_et_psw;
    private Button esptouch_btn_start;
    private TextView esptouch_tv_result;
    private ProgressBar esptouch_pb;

    private BroadcastReceiver mWifiStateChangedReceiver;

    private EspWifiAdminSimple mEspWifiAdminSimple;
    private IEsptouchLinkListener mEsptouchLinkListener;
    private EsptouchLinker mEsptouchLinker;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_esptouch );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if ( mEsptouchLinker != null )
        {
            mEsptouchLinker.stop();
            mEsptouchLinker.setEsptouchLinkListener( null );
            mEsptouchLinker = null;
        }
        if ( mWifiStateChangedReceiver != null )
        {
            unregisterReceiver( mWifiStateChangedReceiver );
        }
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {

    }

    @Override
    protected void initView()
    {
        esptouch_toolbar = findViewById( R.id.esptouch_toolbar );
        esptouch_et_ssid = findViewById( R.id.esptouch_et_ssid );
        esptouch_et_psw = findViewById( R.id.esptouch_et_psw );
        esptouch_btn_start = findViewById( R.id.esptouch_btn_start );
        esptouch_tv_result = findViewById( R.id.esptouch_tv_result );
        esptouch_pb = findViewById( R.id.esptouch_pb );

        setSupportActionBar( esptouch_toolbar );
    }

    @Override
    protected void initData()
    {
        mEspWifiAdminSimple = new EspWifiAdminSimple( this );
        mEsptouchLinkListener = new IEsptouchLinkListener() {
            @Override
            public void onLinked( final IEsptouchResult result )
            {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run()
                    {
                        esptouch_tv_result.setText( result.getBssid() + "\t\t" + result.getInetAddress().getHostAddress() );
                    }
                } );
            }

            @Override
            public void onCompleted( List< IEsptouchResult > results )
            {
                mEsptouchLinker.stop();
                mEsptouchLinker.setEsptouchLinkListener( null );
                mEsptouchLinker = null;
                runOnUiThread( new Runnable() {
                    @Override
                    public void run()
                    {
                        esptouch_pb.setVisibility( View.GONE );
                    }
                } );
            }
        };

        String ssid = mEspWifiAdminSimple.getWifiConnectedSsid();
        esptouch_et_ssid.setText( TextUtils.isEmpty( ssid ) ? getString( R.string.no_wifi_connected ) : ssid );

        mWifiStateChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent )
            {
                LogUtil.e( TAG, "onReceive: " + getResultData() );
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
                if ( networkInfo != null && networkInfo.isConnected() )
                {
                    esptouch_et_ssid.setText( mEspWifiAdminSimple.getWifiConnectedSsid() );
                    esptouch_et_psw.requestFocus();
                    esptouch_btn_start.setEnabled( true );
                }
                else
                {
                    esptouch_et_ssid.setText( getString( R.string.no_wifi_connected ) );
                    esptouch_et_psw.setText( "" );
                    esptouch_btn_start.setEnabled( false );
                }
            }
        };
        registerReceiver( mWifiStateChangedReceiver, new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION ) );
    }

    @Override
    protected void initEvent()
    {
        esptouch_toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                finish();
            }
        } );

        esptouch_btn_start.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                String psw = esptouch_et_psw.getText().toString().trim();
                mEsptouchLinker = new EsptouchLinker( EsptouchActivity.this );
                mEsptouchLinker.setEsptouchLinkListener( mEsptouchLinkListener );
                esptouch_pb.setVisibility( View.VISIBLE );
                mEsptouchLinker.start( mEspWifiAdminSimple.getWifiConnectedSsid(),
                                       mEspWifiAdminSimple.getWifiConnectedBssid(),
                                       psw,
                                       false,
                                       1 );
            }
        } );
    }
}
