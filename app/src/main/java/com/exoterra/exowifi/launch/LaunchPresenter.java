package com.exoterra.exowifi.launch;

import android.text.TextUtils;

import com.exoterra.comman.BasePresenter;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.manager.UserManager;

import cn.xlink.restful.api.app.UserApi;
import cn.xlink.restful.api.app.UserAuthApi;
import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.module.http.XLinkRefreshTokenTask;
import cn.xlink.sdk.v5.module.http.XLinkUserAuthorizeTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

/**
 * Created by liruya on 2018/4/13.
 */

public class LaunchPresenter extends BasePresenter<ILaunchView>
{
    private static final String TAG = "LaunchPresenter";

    public LaunchPresenter( ILaunchView iLaunchView )
    {
        super( iLaunchView );
    }

    private void login( String account, String password )
    {
        XLinkUserAuthorizeTask.Builder builder = XLinkUserAuthorizeTask.newBuilder();
        builder.setCorpId( Constant.CORPORATION_ID );
        builder.setEmail( account, password );
        builder.setListener( new XLinkTaskListener< UserAuthApi.UserAuthResponse >() {
            @Override
            public void onError ( XLinkErrorCode xLinkErrorCode )
            {
                UserManager.removePassword( getContext() );
                UserManager.setLogin( false );
                getView().toLoginActivity();
            }

            @Override
            public void onStart ()
            {

            }

            @Override
            public void onComplete ( UserAuthApi.UserAuthResponse userAuthResponse )
            {
                UserManager.setUserId( getContext(), userAuthResponse.userId );
                UserManager.setUserAccessToken( getContext(), userAuthResponse.accessToken );
                UserManager.setUserRefreshToken( getContext(), userAuthResponse.refreshToken );
                UserManager.setUserAuthorize( getContext(), userAuthResponse.authorize );
                UserManager.setUserExpireIn( getContext(), userAuthResponse.expireIn );
                UserManager.setUserLastRefreshTime( getContext(), System.currentTimeMillis() );
                UserManager.setLogin( true );
                getView().toMainActivity();
            }
        } );
        XLinkUserAuthorizeTask task = builder.build();
        XLinkSDK.startTask( task );
    }

    private void refreshAccessToken( String token )
    {
        XLinkRefreshTokenTask.Builder builder = XLinkRefreshTokenTask.newBuilder();
        builder.setRefreshToken( token );
        builder.setListener( new XLinkTaskListener< UserApi.TokenRefreshResponse >() {
            @Override
            public void onError ( XLinkErrorCode xLinkErrorCode )
            {
                UserManager.setLogin( false );
                getView().toLoginActivity();
            }

            @Override
            public void onStart ()
            {

            }

            @Override
            public void onComplete ( UserApi.TokenRefreshResponse tokenRefreshResponse )
            {
                int expire_in = Integer.parseInt( tokenRefreshResponse.expireIn );
                UserManager.setUserAccessToken( getContext(), tokenRefreshResponse.accessToken );
                UserManager.setUserRefreshToken( getContext(), tokenRefreshResponse.refreshToken );
                UserManager.setUserExpireIn( getContext(), expire_in );
                UserManager.setUserLastRefreshTime( getContext(), System.currentTimeMillis() );
                UserManager.setLogin( true );
                getView().toMainActivity();
            }
        } );
        XLinkRefreshTokenTask task = builder.build();
        XLinkSDK.startTask( task );
    }

    public void getAuthorize()
    {
        String account = UserManager.getUserAccount( getContext() );
        String password = UserManager.getUserPassword( getContext() );
        if ( TextUtils.isEmpty( account ) || TextUtils.isEmpty( password ) )
        {
            UserManager.setLogin( false );
            getView().toLoginActivity();
            return;
        }
        int userid = UserManager.getUserId( getContext() );
        String accessToken = UserManager.getUserAccessToken( getContext() );
        String refreshToken = UserManager.getUserRefreshToken( getContext() );
        int expire_in = UserManager.getUserExpireIn( getContext() );
        long last_refresh_time = UserManager.getUserLastRefreshTime( getContext() );
        if ( userid <= 0 || last_refresh_time + expire_in * 1000 <= System.currentTimeMillis()
             || TextUtils.isEmpty( accessToken ) || TextUtils.isEmpty( refreshToken ) )
        {
            login( account, password );
        }
        else if ( last_refresh_time + expire_in * 1000 < System.currentTimeMillis() + 300000 )
        {
            refreshAccessToken( refreshToken );
        }
        else
        {
            UserManager.setLogin( true );
            getView().toMainActivity();
        }
    }
}
