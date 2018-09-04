package com.exoterra.tcp_udp;

import com.exoterra.comman.CommanUtil;
import com.exoterra.comman.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liruya on 2018/5/9.
 */

public class UdpClient
{
    private final String TAG = "UdpClient";
    private final int UDP_SEND_BUFFER_SIZE = 1024;
    private final int UDP_RECEIVE_BUFFER_SIZE = 1024;
    private Object mObject;

    private DatagramSocket mSocket;
    private DatagramPacket mReceivePacket;
    private String mLocalIp;
    private int mLocalPort;
    private String mRemoteIp;
    private int mRemotePort;
    private byte[] mReceiveBuffer;
    private boolean mListennig;
    private ExecutorService mExecutorService;
    private UdpClientListener mListener;

    public UdpClient( String localIp, int localPort, String remoteIp, int remotePort )
    {
        mLocalIp = localIp;
        mLocalPort = localPort;
        mRemoteIp = remoteIp;
        mRemotePort = remotePort;
        mObject = new Object();
        LogUtil.e( TAG, "UdpClient: Local-" + localIp + ":" + localPort + "\t\tRemote-" + mRemoteIp +":" + mRemotePort );
        mExecutorService = Executors.newCachedThreadPool();
        mReceiveBuffer = new byte[UDP_RECEIVE_BUFFER_SIZE];
    }

    public String getRemoteIp()
    {
        return mRemoteIp;
    }

    public void setRemoteIp( String remoteIp )
    {
        mRemoteIp = remoteIp;
    }

    public int getLocalPort()
    {
        return mLocalPort;
    }

    public void setLocalPort( int localPort )
    {
        mLocalPort = localPort;
    }

    public int getRemotePort()
    {
        return mRemotePort;
    }

    public void setRemotePort( int remotePort )
    {
        mRemotePort = remotePort;
    }

    public void setListener( UdpClientListener listener )
    {
        mListener = listener;
    }

    public synchronized void start()
    {
        if ( !CommanUtil.isIpAddress( mLocalIp ) || mLocalPort < 0 || mLocalPort > 65535 )
        {
            LogUtil.e( TAG, "start: invalid local ip or port" );
            return;
        }
        if ( !CommanUtil.isIpAddress( mRemoteIp ) || mRemotePort < 0 || mRemotePort > 65535 )
        {
            LogUtil.e( TAG, "start: invalid remote ip or port" );
            return;
        }
        try
        {
            InetAddress address = InetAddress.getByName( mLocalIp );
            mSocket = new DatagramSocket( mLocalPort, address );
            mListennig = true;
        }
        catch ( SocketException e )
        {
            LogUtil.e( TAG, "start: Socket  " + e.getMessage() );
            e.printStackTrace();
            if ( mListener != null )
            {
                mListener.onError( Error.ERROR_SOCKET );
            }
        }
        catch ( UnknownHostException e )
        {
            LogUtil.e( TAG, "start: UnknwonHost  " + e.getMessage() );
            e.printStackTrace();
            if ( mListener != null )
            {
                mListener.onError( Error.ERROR_UNKNOWN_HOST );
            }
        }
    }

    public synchronized void stop()
    {
        if ( mSocket != null )
        {
            synchronized ( mObject )
            {
                mExecutorService.shutdown();
                mSocket.close();
                mListennig = false;
                mSocket = null;
            }
        }
    }

    public synchronized void send( final byte[] bytes )
    {
        if ( !mListennig || bytes == null || bytes.length == 0 )
        {
            return;
        }
        if ( CommanUtil.isIpAddress( mRemoteIp ) && mRemotePort >= 0 && mRemotePort <= 65535 )
        {
            mExecutorService.execute( new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        InetAddress address = InetAddress.getByName( mRemoteIp );
                        DatagramPacket packet = new DatagramPacket( bytes, bytes.length, address, mRemotePort );
                        synchronized ( mObject )
                        {
                            if ( mSocket != null )
                            {
                                mSocket.send( packet );
                                if ( mListener != null )
                                {
                                    mListener.onSend();
                                }
                            }
                        }
                    }
                    catch ( SocketException e )
                    {
                        e.printStackTrace();
                        if ( mListener != null )
                        {
                            mListener.onError( Error.ERROR_SOCKET );
                        }
                    }
                    catch ( UnknownHostException e )
                    {
                        e.printStackTrace();
                        if ( mListener != null )
                        {
                            mListener.onError( Error.ERROR_UNKNOWN_HOST );
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

    public synchronized void receive()
    {
        mExecutorService.execute( new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    InetAddress address = InetAddress.getByName( mRemoteIp );
                    mReceivePacket = new DatagramPacket( mReceiveBuffer, mReceiveBuffer.length );
                    while ( mListennig )
                    {
                        mSocket.receive( mReceivePacket );
                        if ( mReceivePacket.getAddress()
                                           .equals( address ) && mReceivePacket.getLength() > 0 )
                        {
                            final byte[] bytes = Arrays.copyOfRange( mReceivePacket.getData(), mReceivePacket.getOffset(), mReceivePacket.getLength() );
                            if ( mListener != null )
                            {
                                mListener.onReceive( bytes );
                            }
                        }
                    }
                }
                catch ( SocketException e )
                {
                    e.printStackTrace();
                    if ( mListener != null )
                    {
                        mListener.onError( Error.ERROR_SOCKET );
                    }
                }
                catch ( UnknownHostException e )
                {
                    e.printStackTrace();
                    if ( mListener != null )
                    {
                        mListener.onError( Error.ERROR_UNKNOWN_HOST);
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
