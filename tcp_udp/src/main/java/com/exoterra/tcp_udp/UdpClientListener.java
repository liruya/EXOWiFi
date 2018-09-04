package com.exoterra.tcp_udp;

import android.support.annotation.NonNull;

/**
 * Created by liruya on 2018/5/10.
 */

public interface UdpClientListener
{
    void onSend();

    void onReceive( @NonNull final byte[] bytes );

    void onError( @Error.Code int code );
}
