package com.exoterra.comman;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liruya on 2018/4/14.
 */

public class CommanUtil
{
    private static final String TAG = "CommanUtil";

    public static boolean checkEmail( String email )
    {
        if ( TextUtils.isEmpty( email ) || email.length() > 30 ) {
            return false;
        }

        boolean flag;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile( check );
            Matcher matcher = regex.matcher( email );
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }

        boolean flag;
        try {
            String check = "^[1][3|5|7|8][0-9]{9}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(phoneNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isIpAddress( String ip )
    {
        if ( TextUtils.isEmpty( ip ) )
        {
            return false;
        }
        String[] adrs = ip.split( "\\." );
        if ( adrs.length == 4 )
        {
            for ( int i = 0; i < 4; i++ )
            {
                try
                {
                    int val = Integer.parseInt( adrs[i] );
                    if ( val < 0 || val > 255 )
                    {
                        return false;
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isMulticastIp( String ip )
    {
        return "255.255.255.255".equals( ip );
    }
}
