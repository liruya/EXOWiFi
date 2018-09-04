package com.exoterra.exowifi.login;

import android.text.TextUtils;

import com.exoterra.comman.BasePresenter;
import com.exoterra.comman.CommanUtil;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.manager.UserManager;

import cn.xlink.restful.api.app.UserAuthApi;
import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.module.http.XLinkUserAuthorizeTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

/**
 * Created by liruya on 2018/4/14.
 */

public class LoginPresenter extends BasePresenter<ILoginView>
{

    public LoginPresenter( ILoginView iLoginView )
    {
        super( iLoginView );
    }

    public void login()
    {
        final String email = getView().getAccount();
        final String password = getView().getPassword();
        if ( !CommanUtil.checkEmail( email ) )
        {
            getView().toast( getString( R.string.error_email_format ) );
            return;
        }
        if ( TextUtils.isEmpty( password ) || password.length() > 16 || password.length() < 6 )
        {
            getView().toast( getString( R.string.error_password_format ) );
            return;
        }
        XLinkUserAuthorizeTask.Builder builder = XLinkUserAuthorizeTask.newBuilder();
        builder.setCorpId( Constant.CORPORATION_ID );
        builder.setEmail( email, password );
        builder.setListener( new XLinkTaskListener< UserAuthApi.UserAuthResponse >() {
            @Override
            public void onError ( XLinkErrorCode xLinkErrorCode )
            {
                getView().runOnUIThread( new Runnable() {
                    @Override
                    public void run()
                    {
                        getView().dismissProgress();
                        getView().toast( getString( R.string.error_login_failure ) );
                    }
                } );
            }

            @Override
            public void onStart ()
            {
                getView().showProgress();
            }

            @Override
            public void onComplete( final UserAuthApi.UserAuthResponse userAuthResponse )
            {
                getView().runOnUIThread( new Runnable() {
                    @Override
                    public void run()
                    {
                        getView().dismissProgress();
                        UserManager.setUserAccount( getContext(), email );
                        UserManager.setUserPassword( getContext(), password );
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
            }
        } );
        XLinkUserAuthorizeTask task = builder.build();
        XLinkSDK.startTask( task );
    }
}
