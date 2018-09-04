package com.exoterra.tcp_udp;

import com.exoterra.comman.CommanUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liruya on 2018/5/9.
 */

public class TcpClient
{
    private static final String TAG = "TcpClient";

    private final int TCP_SEND_MAX_LENGTH = 1460;
    private final int TCP_SEND_BUFFER_SIZE = 2048;
    private final int TCP_RECEIVE_BUFFER_SIZE = 2048;
    private final int TCP_CONNECT_TIMEOUT_DEFAULT = 2000;

    private ExecutorService mExecutorService = null;
    private Socket mSocket;
    private String mRemoteIp;
    private int mRemotePort;
    private int mConnectTimeout;
    private BufferedInputStream mInputStream;
    private BufferedOutputStream mOutputStream;
    private byte[] mRxBuffer;
    private boolean mListening;
    private TcpClientListener mListener;

    public TcpClient( String remoteIp, int remotePort, int connectTimeout )
    {
        mRemoteIp = remoteIp;
        mRemotePort = remotePort;
        mConnectTimeout = connectTimeout;
        mRxBuffer = new byte[TCP_RECEIVE_BUFFER_SIZE];
        mExecutorService = Executors.newCachedThreadPool();
    }

    public TcpClient( String remoteIp, int remotePort )
    {
        mRemoteIp = remoteIp;
        mRemotePort = remotePort;
        mConnectTimeout = TCP_CONNECT_TIMEOUT_DEFAULT;
        mRxBuffer = new byte[TCP_RECEIVE_BUFFER_SIZE];
        mExecutorService = Executors.newCachedThreadPool();
    }

    public String getRemoteIp()
    {
        return mRemoteIp;
    }

    public void setRemoteIp( String remoteIp )
    {
        if ( !mRemoteIp.equals( remoteIp ) )
        {
            disconnect();
            mRemoteIp = remoteIp;
        }
    }

    public int getRemotePort()
    {
        return mRemotePort;
    }

    public void setRemotePort( int remotePort )
    {
        if ( mRemotePort != remotePort )
        {
            disconnect();
            mRemotePort = remotePort;
        }
    }

    public int getConnectTimeout()
    {
        return mConnectTimeout;
    }

    public void setConnectTimeout( int connectTimeout )
    {
        mConnectTimeout = connectTimeout;
    }

    public void setListener( TcpClientListener listener )
    {
        mListener = listener;
    }

    public void connect()
    {
        if ( CommanUtil.isIpAddress( mRemoteIp ) && mRemotePort >= 0 && mRemotePort <= 65535 )
        {
            disconnect();
            mSocket = new Socket();
            mExecutorService.execute( new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        mSocket.setTcpNoDelay( true );
                        mSocket.setKeepAlive( true );
                        mSocket.setReceiveBufferSize( TCP_RECEIVE_BUFFER_SIZE );
                        mSocket.setSendBufferSize( TCP_SEND_BUFFER_SIZE );
                        mSocket.connect( new InetSocketAddress( mRemoteIp, mRemotePort ), mConnectTimeout );
                        mInputStream = new BufferedInputStream( mSocket.getInputStream() );
                        mOutputStream = new BufferedOutputStream( mSocket.getOutputStream() );
                        mListening = true;
                        if ( mListener != null )
                        {
                            mListener.onConnected();
                        }

                        receive();
                    }
                    catch ( SocketException e )
                    {
                        e.printStackTrace();
                        if ( mListener != null )
                        {
                            mListener.onError( Error.ERROR_SOCKET );
                        }
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                        if ( mListener != null )
                        {
                            mListener.onError( Error.ERROR_IO );
                        }
                    }
                }
            } );
        }
        else
        {
            if ( mListener != null )
            {
                mListener.onError( Error.ERROR_INVALID_IP_PORT );
            }
        }
    }

    public synchronized void disconnect()
    {
        mExecutorService.execute( new Runnable() {
            @Override
            public void run()
            {
                if ( mListening )
                {
                    mListening = false;
                    try
                    {
                        if ( mInputStream != null )
                        {
                            mInputStream.close();
                            mInputStream = null;
                        }
                        if ( mOutputStream != null )
                        {
                            mOutputStream.close();
                            mOutputStream = null;
                        }
                        if ( mSocket != null )
                        {
                            mSocket.close();
                            mSocket = null;
                            if ( mListener != null )
                            {
                                mListener.onDisconnected();
                            }
                        }
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
        } );
    }

    public synchronized void send( final byte[] bytes )
    {
        if ( mSocket == null || mSocket.isClosed() || mOutputStream == null )
        {
            return;
        }
        if ( bytes != null && bytes.length > 0 )
        {
            mExecutorService.execute( new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        int index = 0;
                        while ( index < bytes.length )
                        {
                            int len = bytes.length - index;
                            if ( len > TCP_SEND_MAX_LENGTH )
                            {
                                len = TCP_SEND_MAX_LENGTH;
                            }
                            mOutputStream.write( bytes, index, len );
                            mOutputStream.flush();
                            index += len;
                        }
                        if ( mListener != null )
                        {
                            mListener.onSend();
                        }
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                        if ( mListener != null )
                        {
                            mListener.onError( Error.ERROR_IO );
                        }
                    }
                }
            } );
        }
    }

    private synchronized void receive()
    {
        while ( mListening )
        {
            try
            {
                int len = mInputStream.read( mRxBuffer );
                if ( len > 0 )
                {
                    byte[] bytes = Arrays.copyOf( mRxBuffer, len );
                    if ( mListener != null )
                    {
                        mListener.onReceive( bytes );
                    }
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                if ( mListener != null )
                {
                    mListener.onError( Error.ERROR_IO );
                }
            }
        }
    }
}
