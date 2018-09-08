package com.exoterra.exowifi.main.devicelist;

import android.text.TextUtils;

import com.exoterra.comman.BasePresenter;
import com.exoterra.comman.LogUtil;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.manager.DeviceManager;
import com.exoterra.exowifi.manager.XlinkListenerManager;

import java.util.List;

import cn.xlink.restful.XLinkRestful;
import cn.xlink.restful.api.app.DeviceApi;
import cn.xlink.sdk.v5.listener.XLinkDeviceStateListener;
import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.module.http.XLinkRemoveDeviceTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liruya on 2018/4/18.
 */

public class DevicelistPresenter extends BasePresenter<IDevicelistView >
{
    private final String TAG = "DevicelistPresenter";
    private XLinkDeviceStateListener mStateListener;

    public DevicelistPresenter( IDevicelistView iDevicelistView )
    {
        super( iDevicelistView );
        mStateListener = new XLinkDeviceStateListener() {
            @Override
            public void onDeviceStateChanged( XDevice xDevice, XDevice.State state )
            {
                if (DeviceManager.getInstance().contains(xDevice))
                {
                    DeviceManager.getInstance().updateDevice( xDevice );
                }
                switch ( state )
                {
                    case DISCONNECTED:
                    case CONNECTED:
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
                    case CONNECTING:
                        break;
                    case DETACHED:
                        break;
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

    private String getErrorMessage( XLinkErrorCode code )
    {
        return "" + code.getValue();
    }

    public void refreshSubcribedDevices()
    {
        DeviceManager.getInstance().refreshSubcribeDevices( new XLinkTaskListener< List< Device > >() {
            @Override
            public void onError( final XLinkErrorCode xLinkErrorCode )
            {
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showRefreshSubcribedDevicesError( getErrorMessage( xLinkErrorCode ) );
                        }
                    } );
                }
            }

            @Override
            public void onStart()
            {
//                if ( isViewExist() )
//                {
//                    getView().startRefresh();
//                }
            }

            @Override
            public void onComplete( List< Device > devices )
            {
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showRefreshSuccess();
                        }
                    } );
                }
            }
        } );
    }

    public void unsubcribeDevice( final XDevice xDevice )
    {
        XLinkRemoveDeviceTask.Builder builder = XLinkRemoveDeviceTask.newBuilder();
        builder.setXDevice( xDevice );
        builder.setListener( new XLinkTaskListener< String >() {
            @Override
            public void onError( final XLinkErrorCode xLinkErrorCode )
            {
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showUnsubcribeDeviceError( getErrorMessage( xLinkErrorCode ) );
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
                            getView().startUnsubcribe();
                        }
                    } );
                }
            }

            @Override
            public void onComplete( String s )
            {
                DeviceManager.getInstance().removeDevice( xDevice );
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showUnsubcribeSuccess();
                        }
                    } );
                }
            }
        } );
        XLinkRemoveDeviceTask task = builder.build();
        XLinkSDK.startTask( task );
    }

    public void renameDevice( XDevice device, String name )
    {
        if ( device == null || TextUtils.isEmpty( name ) )
        {
            return;
        }
        DeviceApi.DeviceRequest request = new DeviceApi.DeviceRequest();
        request.name = name;
        Call<DeviceApi.DeviceResponse> responseCall = XLinkRestful.getApplicationApi()
                                                                  .updateDeviceInfo( device.getProductId(), device.getDeviceId(), request );
        responseCall.enqueue( new Callback< DeviceApi.DeviceResponse >() {
            @Override
            public void onResponse( Call< DeviceApi.DeviceResponse > call, Response< DeviceApi.DeviceResponse > response )
            {
                LogUtil.e( TAG, "onResponse: " + response.isSuccessful() + "  " + response.body().name );
            }

            @Override
            public void onFailure( Call< DeviceApi.DeviceResponse > call, Throwable t )
            {
                LogUtil.e( TAG, "onFailure: " + t.getMessage() );
            }
        } );
    }

}
