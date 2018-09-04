package com.exoterra.exowifi.register;

import android.text.TextUtils;

import com.exoterra.comman.BasePresenter;
import com.exoterra.comman.CommanUtil;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.xlink.restful.XLinkRestful;
import cn.xlink.restful.XLinkRestfulEnum;
import cn.xlink.restful.api.app.UserAuthApi;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liruya on 2018/4/14.
 */

public class RegisterPresenter extends BasePresenter<IRegisterView>
{

    public RegisterPresenter( IRegisterView iRegisterView )
    {
        super( iRegisterView );
    }

    public void register()
    {
        final String email = getView().getAccount();
        String nickname = getView().getNickname();
        String password = getView().getPassword();
        if ( !CommanUtil.checkEmail( email ) )
        {
            getView().toast( getString( R.string.error_email_format ) );
            return;
        }
        if ( TextUtils.isEmpty( nickname ) )
        {
            getView().toast( getString( R.string.error_nickname_empty ) );
            return;
        }
        if ( TextUtils.isEmpty( password ) || password.length() < 6 )
        {
            getView().toast( getString( R.string.error_password_format ) );
            return;
        }
        getView().showProgress();
        UserAuthApi.EmailRegisterRequest request = new UserAuthApi.EmailRegisterRequest();
        request.corpId = Constant.CORPORATION_ID;
        request.email = email;
        request.nickname = nickname;
        request.password = password;
        request.source = XLinkRestfulEnum.UserSource.ANDROID;
        Call<UserAuthApi.EmailRegisterResponse > call = XLinkRestful.getApplicationApi().registEmailAccount( request );
        call.enqueue( new Callback< UserAuthApi.EmailRegisterResponse >() {
            @Override
            public void onResponse( Call< UserAuthApi.EmailRegisterResponse > call, Response< UserAuthApi.EmailRegisterResponse > response )
            {
                if ( response.isSuccessful() )
                {
                    UserAuthApi.EmailRegisterResponse emailRegisterResponse = response.body();
                    final String msg;
                    final boolean success;
                    if ( emailRegisterResponse != null && emailRegisterResponse.email.equals( email ) )
                    {
                        msg = getString( R.string.regiser_success );
                        success = true;
                    }
                    else
                    {
                        msg = getErrorMessage( response, null );
                        success = false;
                    }
                    if ( isViewExist() )
                    {
                        getView().runOnUIThread( new Runnable() {
                            @Override
                            public void run()
                            {
                                getView().toast( msg );
                                getView().dismissProgress();
                                if ( success )
                                {
                                    getView().closeWithResult();
                                }
                            }
                        } );
                    }
                }
            }

            @Override
            public void onFailure( Call< UserAuthApi.EmailRegisterResponse > call, final Throwable t )
            {
                if ( t != null && t.getMessage() != null && isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().toast( t.getMessage() );
                            getView().dismissProgress();
                        }
                    } );

                }
            }
        } );
    }

    private String getErrorMessage( Response response, Throwable t )
    {
        String msg = getString( R.string.error_request );
        if ( t != null )
        {
            t.printStackTrace();
        }
        if ( response.errorBody() != null )
        {
            String error = getString( R.string.error_unknown );
            try
            {
                error = response.errorBody().string();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            if ( !"null".equals( error ) )
            {
                try
                {
                    JSONObject object = new JSONObject( error );
                    if ( object.has( "error" ) )
                    {
                        JSONObject errorObj = object.getJSONObject( "error" );
                        if ( errorObj != null && errorObj.has( "code" ) )
                        {
                            int code = errorObj.getInt( "code" );
                            if ( code == XLinkErrorCode.ERROR_API_CORP_NOT_EXISTS.getValue() )
                            {
                                msg = getString( R.string.error_corp_not_exist );
                            }
                            else if ( code == XLinkErrorCode.ERROR_API_REGISTER_PHONE_EXISTS.getValue() )
                            {
                                msg = getString( R.string.error_phone_exist );
                            }
                            else if ( code == XLinkErrorCode.ERROR_API_REGISTER_EMAIL_EXISTS.getValue() )
                            {
                                msg = getString( R.string.error_email_exist );
                            }
                        }
                    }
                }
                catch ( JSONException e )
                {
                    e.printStackTrace();
                }
            }
        }
        return msg;
    }
}
