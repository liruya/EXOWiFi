package com.exoterra.tcp_udp;

/**
 * Created by liruya on 2018/5/10.
 */

public interface TcpClientListener
{
    void onConnected();

    void onDisconnected();

    void onError( @Error.Code int code );

    void onSend();

    void onReceive( final byte[] bytes );
}
