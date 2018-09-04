package com.exoterra.exowifi.device;

import com.exoterra.comman.BasePresenter;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.manager.XlinkListenerManager;

import cn.xlink.sdk.v5.listener.XLinkDeviceStateListener;
import cn.xlink.sdk.v5.model.XDevice;

/**
 * Created by liruya on 2018/4/24.
 */

public class DevicePresenter extends BasePresenter<IDeviceView >
{
    private final String TAG = "DevicePresenter";

    private Device mDevice;
    private boolean mUpgradable;
    private String mTargetVersionUrl;
    private int mTargetVersion;
    private XLinkDeviceStateListener mStateListener;

    public DevicePresenter( IDeviceView iDeviceView, Device device )
    {
        super( iDeviceView );
        mDevice = device;
        mStateListener = new XLinkDeviceStateListener() {
            @Override
            public void onDeviceStateChanged( XDevice xDevice, XDevice.State state )
            {
                if ( mDevice.getXDevice().getMacAddress().equals( xDevice.getMacAddress() ) )
                {
                    switch ( state )
                    {
                        case CONNECTED:
                        case DISCONNECTED:
                            if ( isViewExist() )
                            {
                                getView().runOnUIThread( new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        getView().showDeviceStateChanged();
                                    }
                                } );
                            }
                            break;
                        case DETACHED:
                            break;
                        case CONNECTING:
                            break;
                    }
                }
            }

            @Override
            public void onDeviceChanged( XDevice xDevice, XDevice.Event event )
            {

            }
        };
    }

    public void addDeviceStateChangedListener()
    {
        XlinkListenerManager.getInstance().addXlinkDeviceStateListener( mStateListener );
    }

    public void removeDeviceStateChangedListener()
    {
        XlinkListenerManager.getInstance().removeXlinkDeviceStateListener( mStateListener );
    }
}
