package com.exoterra.exowifi.device.light.manual;

import com.exoterra.comman.BasePresenter;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.manager.XlinkListenerManager;

import java.util.List;

import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;

/**
 * Created by liruya on 2018/4/24.
 */

public class ManualPresenter extends BasePresenter<IManualView>
{
    private final String TAG = "ManualPresenter";
    private ExoLightStrip mLight;
    private XLinkDataListener mDataListener;

    public ManualPresenter( IManualView iManualView, ExoLightStrip light )
    {
        super( iManualView );
        mLight = light;
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

    public void addDataListener()
    {
        XlinkListenerManager.getInstance().addXlinkDataListener( mDataListener );
    }

    public void removeDataListener()
    {
        XlinkListenerManager.getInstance().removeXlinkDataListener( mDataListener );
    }


    private String getErrorMessage( XLinkErrorCode code )
    {
        return "" + code.getValue();
    }

//    private void setDeviceDataPoints( List<XLinkDataPoint > dps )
//    {
//        if ( dps == null || dps.size() == 0 )
//        {
//            return;
//        }
//        XLinkSetDataPointTask.Builder builder = XLinkSetDataPointTask.newBuilder();
//        builder.setXDevice( mLight.getXDevice() );
//        builder.setDataPoints( dps );
//        builder.setListener( new XLinkTaskListener< XDevice >() {
//            @Override
//            public void onError( final XLinkErrorCode xLinkErrorCode )
//            {
//                if ( isViewExist() )
//                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showSetDeviceDataPointError( getErrorMessage( xLinkErrorCode ) );
//                        }
//                    } );
//                }
//            }
//
//            @Override
//            public void onStart()
//            {
//                if ( isViewExist() )
//                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showStartSetDeviceDataPoint();
//                        }
//                    } );
//                }
//            }
//
//            @Override
//            public void onComplete( XDevice device )
//            {
//                if ( isViewExist() )
//                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showSetDeviceDataPointSuccess();
//                        }
//                    } );
//                }
//            }
//        } );
//        XLinkSetDataPointTask task = builder.build();
//        XLinkSDK.startTask( task );
//    }
//
//    private void setDeviceDataPoints( XLinkDataPoint... dps )
//    {
//        List<XLinkDataPoint> dpList = Arrays.asList( dps );
//        setDeviceDataPoints( dpList );
//    }

    public void setPower( boolean power )
    {
        mLight.setLightPower( power );
    }

    public void setCustom( int idx, byte[] progress )
    {
        mLight.setLightCustom(  idx, progress );
    }

    public void setBright( int chn, int value )
    {
        mLight.setLightBright( chn, value );
    }

    public void setAllBrights( int[] values )
    {
        mLight.setAllBrights( values );
    }
}
