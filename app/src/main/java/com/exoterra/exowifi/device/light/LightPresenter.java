package com.exoterra.exowifi.device.light;

import com.exoterra.comman.BasePresenter;
import com.exoterra.comman.LogUtil;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.manager.XlinkListenerManager;
import com.exoterra.exowifi.manager.XlinkUpgradeManager;

import java.util.List;

import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.listener.XLinkDeviceStateListener;
import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;
import cn.xlink.sdk.v5.module.datapoint.XLinkGetDataPointTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class LightPresenter extends BasePresenter<ILightView>
{
    private final String TAG = "DevicePresenter";

    private ExoLightStrip mLight;
    private boolean mUpgradable;
    private String mTargetVersionUrl;
    private int mTargetVersion;
    private XLinkDeviceStateListener mStateListener;
    private XLinkDataListener mDataListener;

    public LightPresenter( ILightView iLightView, ExoLightStrip light )
    {
        super( iLightView );
        mLight = light;
        mStateListener = new XLinkDeviceStateListener() {
            @Override
            public void onDeviceStateChanged( XDevice xDevice, XDevice.State state )
            {
                if ( mLight.getXDevice().getMacAddress().equals( xDevice.getMacAddress() ) )
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
        mDataListener = new XLinkDataListener() {
            @Override
            public void onDataPointUpdate( XDevice xDevice, List< XLinkDataPoint > list )
            {
                if ( mLight.getXDevice().getMacAddress().equals( xDevice.getMacAddress() ) )
                {
                    for ( XLinkDataPoint dp : list )
                    {
                        mLight.setDataPoint( dp );
                    }
                    if ( isViewExist() )
                    {
                        getView().runOnUIThread( new Runnable() {
                            @Override
                            public void run()
                            {
                                getView().showDataChanged();
                            }
                        } );
                    }
                }
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

    public void addDataChangedListener()
    {
        XlinkListenerManager.getInstance().addXlinkDataListener( mDataListener );
    }

    public void removeDataChangedListener()
    {
        XlinkListenerManager.getInstance().removeXlinkDataListener( mDataListener );
    }

    private String getErrorMessage( XLinkErrorCode code )
    {
        return "" + code.getValue();
    }

    public void getDeviceDataPoints()
    {
        XLinkGetDataPointTask.Builder builder = XLinkGetDataPointTask.newBuilder();
        builder.setXDevice( mLight.getXDevice() );
        builder.setListener( new XLinkTaskListener< List< XLinkDataPoint > >() {
            @Override
            public void onError( final XLinkErrorCode xLinkErrorCode )
            {
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showGetDeviceDataPointError( getErrorMessage( xLinkErrorCode ) );
                        }
                    } );
                }
            }

            @Override
            public void onStart()
            {
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showStartGetDeviceDataPoint();
                        }
                    } );
                }
            }

            @Override
            public void onComplete( List< XLinkDataPoint > xLinkDataPoints )
            {
                mLight.getDataPointList().clear();
                mLight.getDataPointList().addAll( xLinkDataPoints );
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showDataChanged();
                        }
                    } );
                }
            }
        } );
        XLinkGetDataPointTask task = builder.build();
        XLinkSDK.startTask( task );
    }

    public void setMode( @ExoLightStrip.Mode int mode )
    {
        mLight.setLightMode( mode );
    }

    public void setUpgradeInfo( String url, int version )
    {
        mUpgradable = true;
        mTargetVersionUrl = url;
        mTargetVersion = version;
    }

    public void checkUpdate()
    {
        XlinkUpgradeManager.checkUpgrade( mLight, new XlinkUpgradeManager.CheckUpgradeCallback()
        {
            @Override
            public void onGetUpgradeInfoSuccess( String target_version_url, int target_versioin )
            {
                LogUtil.e( TAG, "onGetUpgradeInfoSuccess: " + target_version_url + "\t" + target_versioin + "\t" + target_version_url.length() );
                mLight.setLightUpgrade( target_version_url );
            }

            @Override
            public void onGetUpgradeInfoFailed( String msg )
            {
                LogUtil.e( TAG, "onGetUpgradeInfoFailed: " + msg );
            }
        } );
    }
}
