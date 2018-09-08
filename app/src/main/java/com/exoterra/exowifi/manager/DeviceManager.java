package com.exoterra.exowifi.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.exoterra.comman.LogUtil;
import com.exoterra.exowifi.bean.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;
import cn.xlink.sdk.v5.module.datapoint.XLinkSetDataPointTask;
import cn.xlink.sdk.v5.module.http.XLinkSyncDeviceListTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

/**
 * Created by liruya on 2018/4/18.
 */

public class DeviceManager
{
    private static final String TAG = "DeviceManager";

    private Map<String, Device> mSubcribedDevices;

    private static class LazyHolder
    {
        private static final DeviceManager INSTANCE = new DeviceManager();
    }

    private DeviceManager()
    {
        mSubcribedDevices = new HashMap<>();
    }

    public static DeviceManager getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public void addDevice( @NonNull XDevice device )
    {
        if ( device != null )
        {
            mSubcribedDevices.put( device.getMacAddress(), new Device( device ) );
        }
    }

    public void addDevice( @NonNull Device device )
    {
        if ( device != null )
        {
            mSubcribedDevices.put( device.getXDevice().getMacAddress(), device );
        }
    }

    public void removeDevice( @NonNull String key )
    {
        if ( key != null )
        {
            if ( mSubcribedDevices.containsKey( key ) )
            {
                mSubcribedDevices.remove( key );
            }
        }
    }

    public void removeDevice( @NonNull XDevice device )
    {
        if ( device != null )
        {
            String key = device.getMacAddress();
            removeDevice( key );
        }
    }

    public void removeDevice( @NonNull Device device )
    {
        if ( device != null )
        {
            String key = device.getXDevice().getMacAddress();
            removeDevice( key );
        }
    }

    public void clear()
    {
        mSubcribedDevices.clear();
    }

    public boolean contains( String key )
    {
        if ( key != null )
        {
            return mSubcribedDevices.containsKey( key );
        }
        return false;
    }

    public boolean contains( XDevice device )
    {
        if ( device != null )
        {
            String key = device.getMacAddress();
            return contains( key );
        }
        return false;
    }

    public boolean contains( Device device )
    {
        if ( device != null )
        {
            String key = device.getXDevice().getMacAddress();
            return contains( key );
        }
        return false;
    }

    public Device getDevice( @NonNull String key )
    {
        if ( TextUtils.isEmpty( key ) || !mSubcribedDevices.containsKey( key ) )
        {
            return null;
        }
        return mSubcribedDevices.get( key );
    }

    public Device getDevice( @NonNull XDevice xDevice )
    {
        if ( xDevice != null )
        {
            String key = xDevice.getMacAddress();
            return getDevice( key );
        }
        return null;
    }

    public List<Device> getAllDevices()
    {
        return new ArrayList<>( mSubcribedDevices.values() );
    }

    public Set<String> getAllDeviceAddress()
    {
        return new HashSet<>( mSubcribedDevices.keySet() );
    }

    public void updateDevice( XDevice xDevice )
    {
        Device device = getDevice( xDevice.getMacAddress() );
        if ( device == null )
        {
            device = new Device( xDevice );
            addDevice( device );
        }
        else
        {
            device.setXDevice( xDevice );
        }
    }

    public void refreshSubcribeDevices( final XLinkTaskListener<List<Device>> listener )
    {
        XLinkSyncDeviceListTask.Builder builder = XLinkSyncDeviceListTask.newBuilder();
        builder.setListener( new XLinkTaskListener< List< XDevice > >() {
            @Override
            public void onError( XLinkErrorCode xLinkErrorCode )
            {
                LogUtil.e( TAG, "onError: " + xLinkErrorCode.getValue() );
                if ( listener != null )
                {
                    listener.onError( xLinkErrorCode );
                }
            }

            @Override
            public void onStart()
            {
                if ( listener != null )
                {
                    listener.onStart();
                }
            }

            @Override
            public void onComplete( List< XDevice > xDevices )
            {
                if ( xDevices == null || xDevices.size() == 0 )
                {
                    mSubcribedDevices.clear();
                }
                else
                {
                    mSubcribedDevices.clear();
                    Set<String> keys = new HashSet<>();
                    for ( XDevice device : xDevices )
                    {
                        if ( device != null )
                        {
                            if ( !TextUtils.isEmpty( device.getMacAddress() ) )
                            {
                                keys.add( device.getMacAddress() );
                                updateDevice( device );
                            }
                        }
                    }
                }

                if ( listener != null )
                {
                    listener.onComplete( getAllDevices() );
                }
            }
        } );
        XLinkSyncDeviceListTask task = builder.build();
        XLinkSDK.startTask( task );
    }

//    public void syncDataPoint( final XDevice xDevice )
//    {
//        if ( xDevice == null )
//        {
//            return;
//        }
//        XLinkGetDataPointMetaInfoTask.Builder builder = XLinkGetDataPointMetaInfoTask.newBuilder();
//        builder.setXDevice( xDevice );
//        builder.setListener( new XLinkTaskListener< List< XLinkDataPoint > >() {
//            @Override
//            public void onError( XLinkErrorCode xLinkErrorCode )
//            {
//
//            }
//
//            @Override
//            public void onStart()
//            {
//
//            }
//
//            @Override
//            public void onComplete( List< XLinkDataPoint > xLinkDataPoints )
//            {
//                Device device = getDevice( xDevice );
//                if ( device != null )
//                {
//                    device.setDataPointList( xLinkDataPoints );
//                }
//            }
//        } );
//        XLinkGetDataPointMetaInfoTask task = builder.build();
//        XLinkSDK.startTask( task );
//    }

    public void setDataPoints( XDevice device, List<XLinkDataPoint > dps, XLinkTaskListener<XDevice> listener )
    {
        if ( dps == null || dps.size() == 0 )
        {
            return;
        }
        XLinkSetDataPointTask task = XLinkSetDataPointTask.newBuilder()
                                                          .setXDevice( device )
                                                          .setDataPoints( dps )
                                                          .setListener( listener)
                                                          .build();
        XLinkSDK.startTask( task );
    }
}
