package com.exoterra.comman;

import android.util.Log;

/**
 * Created by liruya on 2018/4/13.
 */

public class LogUtil
{
    private static boolean mDebugEnabled = true;

    public static void setDebugEnabled( boolean enable )
    {
        mDebugEnabled = enable;
    }

    public static boolean getDebugEnabled()
    {
        return mDebugEnabled;
    }

    public static void v( String tag, String msg )
    {
        if ( mDebugEnabled )
        {
            Log.v( tag, msg );
        }
    }

    public static void d( String tag, String msg )
    {
        if ( mDebugEnabled )
        {
            Log.d( tag, msg );
        }
    }

    public static void i( String tag, String msg )
    {
        if ( mDebugEnabled )
        {
            Log.i( tag, msg );
        }
    }

    public static void w( String tag, String msg )
    {
        if ( mDebugEnabled )
        {
            Log.w( tag, msg );
        }
    }

    public static void e( String tag, String msg )
    {
        if ( mDebugEnabled )
        {
            Log.e( tag, msg );
        }
    }
}