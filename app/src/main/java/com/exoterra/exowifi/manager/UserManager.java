package com.exoterra.exowifi.manager;

import android.content.Context;

import com.exoterra.comman.PrefUtil;
import com.exoterra.exowifi.Constant;

/**
 * Created by liruya on 2018/4/14.
 */

public class UserManager
{
    private static boolean mLogin;

    public static boolean isLogin ()
    {
        return mLogin;
    }

    public static void setLogin ( boolean mLogin )
    {
        UserManager.mLogin = mLogin;
    }

    public static void setUserAccount ( Context context, String account )
    {
        PrefUtil.putString( context, Constant.FILE_USER, Constant.KEY_USER_ACCOUNT, account );
    }

    public static String getUserAccount( Context context )
    {
        return PrefUtil.getString( context, Constant.FILE_USER, Constant.KEY_USER_ACCOUNT, "" );
    }

    public static void setUserPassword( Context context, String password )
    {
        PrefUtil.putString( context, Constant.FILE_USER, Constant.KEY_USER_PASSWORD, password );
    }

    public static String getUserPassword( Context context )
    {
        return PrefUtil.getString( context, Constant.FILE_USER, Constant.KEY_USER_PASSWORD, "" );
    }

    public static void setUserId( Context context, int id )
    {
        PrefUtil.putInt( context, Constant.FILE_USER, Constant.KEY_USER_ID, id );
    }

    public static int getUserId( Context context )
    {
        return PrefUtil.getInt( context, Constant.FILE_USER, Constant.KEY_USER_ID, 0 );
    }

    public static void setUserAccessToken( Context context, String token )
    {
        PrefUtil.putString( context, Constant.FILE_USER, Constant.KEY_USER_ACCESS_TOKEN, token );
    }

    public static String getUserAccessToken( Context context )
    {
        return PrefUtil.getString( context, Constant.FILE_USER, Constant.KEY_USER_ACCESS_TOKEN, "" );
    }

    public static void setUserRefreshToken( Context context, String token )
    {
        PrefUtil.putString( context, Constant.FILE_USER, Constant.KEY_USER_REFRESH_TOKEN, token );
    }

    public static String getUserRefreshToken( Context context )
    {
        return PrefUtil.getString( context, Constant.FILE_USER, Constant.KEY_USER_REFRESH_TOKEN, "" );
    }

    public static void setUserAuthorize( Context context, String authorize )
    {
        PrefUtil.putString( context, Constant.FILE_USER, Constant.KEY_USER_AUTHORIZE, authorize );
    }

    public static String getUserAuthorize( Context context )
    {
        return PrefUtil.getString( context, Constant.FILE_USER, Constant.KEY_USER_AUTHORIZE, "" );
    }

    public static void setUserExpireIn( Context context, int expire_in )
    {
        PrefUtil.putInt( context, Constant.FILE_USER, Constant.KEY_USER_EXPIRE_IN, expire_in );
    }

    public static int getUserExpireIn( Context context )
    {
        return PrefUtil.getInt( context, Constant.FILE_USER, Constant.KEY_USER_EXPIRE_IN, 0 );
    }

    public static void setUserLastRefreshTime( Context context, long time )
    {
        PrefUtil.putLong( context, Constant.FILE_USER, Constant.KEY_USER_LAST_REFRESH_TIME, time );
    }

    public static long getUserLastRefreshTime( Context context )
    {
        return PrefUtil.getLong( context, Constant.FILE_USER, Constant.KEY_USER_LAST_REFRESH_TIME, 0 );
    }

    public static void removePassword( Context context )
    {
        PrefUtil.remove( context, Constant.FILE_USER, Constant.KEY_USER_PASSWORD );
    }
}
