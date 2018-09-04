package com.exoterra.tcp_udp;

import android.support.annotation.IntDef;

/**
 * Created by liruya on 2018/5/10.
 */

public class Error
{
//    public static final int ERROR_NONE = 0;
//    public static final int ERROR_INVALID_IP_PORT = 1;
//    public static final int ERROR_CONNECT_SOCKET = 2;
//    public static final int ERROR_CONNECT_IO = 3;
//    public static final int ERROR_SEND_SOCKET = 4;
//    public static final int ERROR_SEND_IO = 5;
//    public static final int ERROR_SEND_UNKNOWN_HOST = 6;
//    public static final int ERROR_RECEIVE_SOCKET = 7;
//    public static final int ERROR_RECEIVE_IO = 8;
//    public static final int ERROR_RECEIVE_UNKNOWN_HOST = 9;
    public static final int ERROR_NONE =  0;
    public static final int ERROR_INVALID_IP_PORT = 1;
    public static final int ERROR_SOCKET = 2;
    public static final int ERROR_IO = 3;
    public static final int ERROR_UNKNOWN_HOST = 4;

    @IntDef( { ERROR_NONE,
               ERROR_INVALID_IP_PORT,
               ERROR_SOCKET,
               ERROR_IO,
               ERROR_UNKNOWN_HOST } )
    public @interface Code
    {

    }
}
