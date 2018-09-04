package com.exoterra.exowifi.manager;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;

public class DeviceUtil
{
    public static String getDefaultName(String pid)
    {
        if ( TextUtils.isEmpty( pid ) )
        {
            return "";
        }
        if ( pid.equals( Constant.PRODUCT_EXOSTRIP_ID ) )
        {
            return "EXO Light Strip";
        }
        if ( pid.equals( Constant.PRODUCT_EXOSOCKET_ID ) )
        {
            return "EXO Socket";
        }
        return "";
    }

    public static @DrawableRes int getProductIcon( String pid )
    {
        if ( TextUtils.isEmpty( pid ) )
        {
            return 0;
        }
        if ( pid.equals( Constant.PRODUCT_EXOSTRIP_ID ) )
        {
            return R.drawable.ic_light_white_64dp;
        }
        if ( pid.equals( Constant.PRODUCT_EXOSOCKET_ID ) )
        {
            return R.drawable.ic_socket_white_64dp;
        }
        return R.drawable.ic_device_white_64dp;
    }

    public static String getProductType(String pid)
    {
        if ( TextUtils.isEmpty( pid ) )
        {
            return "";
        }
        if ( pid.equals( Constant.PRODUCT_EXOSTRIP_ID ) )
        {
            return "Exo Light Strip";
        }
        if ( pid.equals( Constant.PRODUCT_EXOSOCKET_ID ) )
        {
            return "EXO Socket";
        }
        return pid;
    }
}
