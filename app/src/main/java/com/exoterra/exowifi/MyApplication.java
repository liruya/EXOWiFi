package com.exoterra.exowifi;

import android.app.Application;

import com.exoterra.comman.LogUtil;
import com.exoterra.exowifi.manager.UserManager;
import com.exoterra.exowifi.manager.XlinkListenerManager;

import java.util.List;

import cn.xlink.sdk.v5.listener.XLinkCloudListener;
import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.listener.XLinkDeviceStateListener;
import cn.xlink.sdk.v5.listener.XLinkUserListener;
import cn.xlink.sdk.v5.manager.CloudConnectionState;
import cn.xlink.sdk.v5.manager.XLinkUser;
import cn.xlink.sdk.v5.model.EventNotify;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;
import cn.xlink.sdk.v5.module.main.XLinkConfig;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

/**
 * Created by liruya on 2018/4/16.
 */

public class MyApplication extends Application
{
    private final String TAG = "MyApplication";

    private XLinkDataListener mXLinkDataListener;
    private XLinkUserListener mXLinkUserListener;
    private XLinkCloudListener mXLinkCloudListener;
    private XLinkDeviceStateListener mXLinkDeviceStateListener;
    @Override
    public void onCreate()
    {
        super.onCreate();
        init();
    }

    private void init()
    {
        mXLinkDataListener = new XLinkDataListener() {
            @Override
            public void onDataPointUpdate( XDevice xDevice, List< XLinkDataPoint > list )
            {
                XlinkListenerManager.getInstance().onDataPointUpdate( xDevice, list );
            }
        };

        mXLinkUserListener = new XLinkUserListener() {
            @Override
            public void onUserLogout( LogoutReason logoutReason )
            {
                if ( logoutReason == LogoutReason.TOKEN_EXPIRED || logoutReason == LogoutReason.SINGLE_SIGN_KICK_OFF )
                {
                    XLinkSDK.logoutAndStop();
                }
                UserManager.setLogin( false );
                UserManager.setUserAccessToken( getApplicationContext(), "" );
                UserManager.setUserRefreshToken( getApplicationContext(), "" );
                UserManager.setUserLastRefreshTime( getApplicationContext(), 0 );
                XlinkListenerManager.getInstance().onUserLogout( logoutReason );
            }
        };

        mXLinkCloudListener = new XLinkCloudListener() {
            @Override
            public void onCloudStateChanged( CloudConnectionState cloudConnectionState )
            {
                XlinkListenerManager.getInstance().onCloudStateChanged( cloudConnectionState );
            }

            @Override
            public void onEventNotify( EventNotify eventNotify )
            {
                LogUtil.e( TAG, "onEventNotify: " + new String( eventNotify.payload ));
            }
        };

        mXLinkDeviceStateListener = new XLinkDeviceStateListener() {
            @Override
            public void onDeviceStateChanged( XDevice xDevice, XDevice.State state )
            {
                XlinkListenerManager.getInstance().onDeviceStateChanged( xDevice, state );
            }

            @Override
            public void onDeviceChanged( XDevice xDevice, XDevice.Event event )
            {
                XlinkListenerManager.getInstance().onDeviceChanged( xDevice, event );
            }
        };

        XLinkUser user = new XLinkUser();
        user.setUid( UserManager.getUserId( getApplicationContext() ) );
        user.setAccessToken( UserManager.getUserAccessToken( getApplicationContext() ) );
        user.setRefreshToken( UserManager.getUserRefreshToken( getApplicationContext() ) );
        user.setAuthString( UserManager.getUserAuthorize( getApplicationContext() ) );
        XLinkConfig.Builder builder = new XLinkConfig.Builder();
        builder.setXLinkUser( user );
        builder.setDebug( false );
        builder.setAutoDumpCrash( true );
        builder.setDataListener( mXLinkDataListener );
        builder.setUserListener( mXLinkUserListener );
        builder.setXLinkCloudListener( mXLinkCloudListener );
        builder.setDeviceStateListener( mXLinkDeviceStateListener );
        XLinkConfig config = builder.build();
        XLinkSDK.init( this, config );
        XLinkSDK.debugMQTT( false );
        XLinkSDK.debugGateway( false );
    }


}
