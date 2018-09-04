package com.exoterra.exowifi.device.singlesocket;

import com.exoterra.comman.BasePresenter;
import com.exoterra.exowifi.bean.SingleSocket;
import com.exoterra.exowifi.manager.XlinkListenerManager;

import java.util.List;

import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.listener.XLinkDeviceStateListener;
import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;
import cn.xlink.sdk.v5.module.datapoint.XLinkGetDataPointTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class SingleSocketPresenter extends BasePresenter<ISingleSocketView>
{
    private final String TAG = "SingleSocketPresenter";

    private SingleSocket mSocket;
    private XLinkDeviceStateListener mStateListener;
    private XLinkDataListener mDataListener;

    public SingleSocketPresenter( ISingleSocketView iSingleSocketView, SingleSocket socket )
    {
        super( iSingleSocketView );
        mSocket = socket;
        mStateListener = new XLinkDeviceStateListener() {
            @Override
            public void onDeviceStateChanged( XDevice xDevice, XDevice.State state )
            {
                if ( mSocket.getXDevice().getMacAddress().equals( xDevice.getMacAddress() ) )
                {
                    switch ( state )
                    {
                        case CONNECTED:
                        case DISCONNECTED:
                            if ( isViewExist() )
                            {
//                                getView().runOnUIThread( new Runnable() {
//                                    @Override
//                                    public void run()
//                                    {
//                                        getView().showDeviceStateChanged();
//                                    }
//                                } );
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
                if ( mSocket.getXDevice().getMacAddress().equals( xDevice.getMacAddress() ) )
                {
                    for ( XLinkDataPoint dp : list )
                    {
                        mSocket.setDataPoint( dp );
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

    public void getDeviceDataPoints()
    {
        if ( mSocket == null )
        {
            return;
        }
        XLinkGetDataPointTask.Builder builder = XLinkGetDataPointTask.newBuilder();
        builder.setXDevice( mSocket.getXDevice() );
        builder.setListener( new XLinkTaskListener< List< XLinkDataPoint > >() {
            @Override
            public void onError( final XLinkErrorCode xLinkErrorCode )
            {
                if ( isViewExist() )
                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showGetDeviceDataPointError( getErrorMessage( xLinkErrorCode ) );
//                        }
//                    } );
                }
            }

            @Override
            public void onStart()
            {
                if ( isViewExist() )
                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showStartGetDeviceDataPoint();
//                        }
//                    } );
                }
            }

            @Override
            public void onComplete( List< XLinkDataPoint > xLinkDataPoints )
            {
                mSocket.getDataPointList().clear();
                mSocket.getDataPointList().addAll( xLinkDataPoints );
                if ( isViewExist() )
                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showDataChanged();
//                        }
//                    } );
                }
            }
        } );
        XLinkGetDataPointTask task = builder.build();
        XLinkSDK.startTask( task );
    }

    public void setPower( boolean power )
    {
        if ( mSocket != null )
        {
            mSocket.setSocketPower( power );
        }
    }
}
