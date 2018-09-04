package com.exoterra.exowifi.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.xlink.sdk.v5.listener.XLinkCloudListener;
import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.listener.XLinkDeviceStateListener;
import cn.xlink.sdk.v5.listener.XLinkUserListener;
import cn.xlink.sdk.v5.manager.CloudConnectionState;
import cn.xlink.sdk.v5.model.EventNotify;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;

/**
 * Created by liruya on 2018/4/25.
 */

public class XlinkListenerManager
{
    private List<XLinkUserListener> mUserListeners;
    private List<XLinkDeviceStateListener> mDeviceStateListeners;
    private List<XLinkCloudListener> mCloudListeners;
    private List<XLinkDataListener> mDataListeners;

    private XlinkListenerManager()
    {
        mUserListeners = new CopyOnWriteArrayList<>();
        mDeviceStateListeners = new CopyOnWriteArrayList<>();
        mCloudListeners = new CopyOnWriteArrayList<>();
        mDataListeners = new CopyOnWriteArrayList<>();
    }

    public static XlinkListenerManager getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public void addXlinkUserListener( XLinkUserListener listener )
    {
        mUserListeners.add( listener );
    }

    public void removeXlinkUserListener( XLinkUserListener listener )
    {
        if ( listener != null && mUserListeners.contains( listener ) )
        {
            mUserListeners.remove( listener );
        }
    }

    public void addXlinkDeviceStateListener( XLinkDeviceStateListener listener )
    {
        mDeviceStateListeners.add( listener );
    }

    public void removeXlinkDeviceStateListener( XLinkDeviceStateListener listener )
    {
        if ( listener != null && mDeviceStateListeners.contains( listener ) )
        {
            mDeviceStateListeners.remove( listener );
        }
    }

    public void addXlinkCloudListener( XLinkCloudListener listener )
    {
        mCloudListeners.add( listener );
    }

    public void removeXlinkCloudListener( XLinkCloudListener listener )
    {
        if ( listener != null && mCloudListeners.contains( listener ) )
        {
            mCloudListeners.remove( listener );
        }
    }

    public void addXlinkDataListener( XLinkDataListener listener )
    {
        mDataListeners.add( listener );
    }

    public void removeXlinkDataListener( XLinkDataListener listener )
    {
        if ( listener != null && mDataListeners.contains( listener ) )
        {
            mDataListeners.remove( listener );
        }
    }

    public void onUserLogout( XLinkUserListener.LogoutReason logoutReason )
    {
        for ( XLinkUserListener listener : mUserListeners )
        {
            listener.onUserLogout( logoutReason );
        }
    }

    public void onDeviceStateChanged( XDevice xDevice, XDevice.State state )
    {
        for ( XLinkDeviceStateListener listener : mDeviceStateListeners )
        {
            listener.onDeviceStateChanged( xDevice, state );
        }
    }

    public void onDeviceChanged( XDevice xDevice, XDevice.Event event )
    {
        for ( XLinkDeviceStateListener listener : mDeviceStateListeners )
        {
            listener.onDeviceChanged( xDevice, event );
        }
    }

    public void onCloudStateChanged( CloudConnectionState cloudConnectionState )
    {
        for ( XLinkCloudListener listener : mCloudListeners )
        {
            listener.onCloudStateChanged( cloudConnectionState );
        }
    }

    public void onEventNotify( EventNotify eventNotify )
    {
        for ( XLinkCloudListener listener : mCloudListeners )
        {
            listener.onEventNotify( eventNotify );
        }
    }

    public void onDataPointUpdate( XDevice xDevice, List< XLinkDataPoint > list )
    {
        for ( XLinkDataListener listener : mDataListeners )
        {
            listener.onDataPointUpdate( xDevice, list );
        }
    }

    private static class LazyHolder
    {
        private static final XlinkListenerManager INSTANCE = new XlinkListenerManager();
    }
}
