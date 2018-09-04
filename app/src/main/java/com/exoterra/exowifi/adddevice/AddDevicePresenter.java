package com.exoterra.exowifi.adddevice;

import com.exoterra.comman.BasePresenter;
import com.exoterra.comman.LogUtil;
import com.exoterra.exowifi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.xlink.restful.XLinkRestful;
import cn.xlink.sdk.v5.listener.XLinkScanDeviceListener;
import cn.xlink.sdk.v5.listener.XLinkTaskListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.module.connection.XLinkScanDeviceTask;
import cn.xlink.sdk.v5.module.main.XLinkErrorCode;
import cn.xlink.sdk.v5.module.main.XLinkSDK;
import cn.xlink.sdk.v5.module.subscription.XLinkAddDeviceTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by liruya on 2018/4/17.
 */

public class AddDevicePresenter extends BasePresenter<IAddDeviceView>
{
    private static final String TAG = "AddDevicePresenter";

    private static final int SCAN_DEVICE_TIMEOUT = 15000;
    private static final int SCAN_DEVICE_INTERVAL = 500;

    private ArrayList<XDevice> mScannedDevices;
    private LinkedList<XDevice> mSelectedDevices;
    private XLinkScanDeviceTask mXLinkScanDeviceTask;

    private boolean mScanning = false;
    private boolean mSubcribing = false;

    public AddDevicePresenter( IAddDeviceView iAddDeviceView )
    {
        super( iAddDeviceView );
        mScannedDevices = new ArrayList<>();
        mSelectedDevices = new LinkedList<>(  );
    }

    public ArrayList<XDevice> getScannedDevices()
    {
        return mScannedDevices;
    }

    private boolean containsDevice( String mac )
    {
        for ( XDevice device : mScannedDevices )
        {
            if ( device.getMacAddress().equals( mac ) )
            {
                return true;
            }
        }
        return false;
    }

    public void scanDevice( String... pid )
    {
        if ( mScanning )
        {
            return;
        }
        mScannedDevices.clear();
        getView().clearScannedDevice();
        XLinkScanDeviceTask.Builder builder = XLinkScanDeviceTask.newBuilder();
        builder.setProductIds( pid );
        builder.setTimeout( SCAN_DEVICE_TIMEOUT );
        builder.setRetryInterval( SCAN_DEVICE_INTERVAL );
        builder.setScanDeviceListener( new XLinkScanDeviceListener() {
            @Override
            public void onScanResult( XDevice xDevice )
            {
                if ( xDevice != null )
                {
                    if ( !containsDevice( xDevice.getMacAddress() ) )
                    {
                        mScannedDevices.add( xDevice );
                        getView().runOnUIThread( new Runnable() {
                            @Override
                            public void run()
                            {
                                getView().updateScannedDevice();
                            }
                        } );
                    }
                }
            }

            @Override
            public void onError( XLinkErrorCode xLinkErrorCode )
            {
                mScanning = false;
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showCompleteScan();
                        }
                    } );
                }
            }

            @Override
            public void onStart()
            {
                mScanning = true;
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showStartScan();
                        }
                    } );
                }
            }

            @Override
            public void onComplete( Void aVoid )
            {
                mScanning = false;
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showCompleteScan();
                        }
                    } );
                }
            }
        } );
        mXLinkScanDeviceTask = builder.build();
        XLinkSDK.startTask( mXLinkScanDeviceTask );
    }

    public void stopScan()
    {
        if ( mXLinkScanDeviceTask != null )
        {
            mXLinkScanDeviceTask.cancel();
            mXLinkScanDeviceTask = null;
        }
    }

    private String getErrorMessage( int errCode )
    {
        return "" + errCode;
    }

    private String getErrorMessage( Response response, Throwable t )
    {
        String msg = getString( R.string.error_request );
        if ( t != null )
        {
            t.printStackTrace();
        }
        if ( response.errorBody() != null )
        {
            String error = getString( R.string.error_unknown );
            try
            {
                error = response.errorBody().string();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            if ( !"null".equals( error ) )
            {
                try
                {
                    JSONObject object = new JSONObject( error );
                    if ( object.has( "error" ) )
                    {
                        JSONObject errorObj = object.getJSONObject( "error" );
                        if ( errorObj != null && errorObj.has( "code" ) )
                        {
                            int code = errorObj.getInt( "code" );
                            if ( code == XLinkErrorCode.ERROR_API_CORP_NOT_EXISTS.getValue() )
                            {
                                msg = getString( R.string.error_corp_not_exist );
                            }
                            else if ( code == XLinkErrorCode.ERROR_API_REGISTER_PHONE_EXISTS.getValue() )
                            {
                                msg = getString( R.string.error_phone_exist );
                            }
                            else if ( code == XLinkErrorCode.ERROR_API_REGISTER_EMAIL_EXISTS.getValue() )
                                {
                                    msg = getString( R.string.error_email_exist );
                                }
                        }
                    }
                }
                catch ( JSONException e )
                {
                    e.printStackTrace();
                }
            }
        }
        return msg;
    }

    private boolean registerDevice( final XDevice device )
    {
        final boolean[] state = new boolean[]{ false, false};
        Map<String, String> params = new HashMap<>();
        params.put( "product_id", device.getProductId() );
        params.put( "mac", device.getMacAddress() );
//        if ( TextUtils.isEmpty( device.getDeviceName() ) )
//        {
//            params.put( "name", "ExoLightStrip" );
//        }
        String json = new JSONObject( params ).toString();
        RequestBody requestBody = RequestBody.create( MediaType.parse( "application/json; charset=utf-8" ), json );
        Request request = new Request.Builder()
                          .url( "https://api2.xlink.cn/v2/user/" + XLinkSDK.getUser().getUid() + "/register_device" )
                          .header( "Access-Token", XLinkSDK.getAccessToken() )
                          .post( requestBody )
                          .build();
        XLinkRestful.getApiHttpClient().newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( Call call, final IOException e )
            {
                LogUtil.e( TAG, "onFailure: " + e.getMessage() );
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showRegisterDeviceFailed( device, e.getMessage() );
                        }
                    } );
                }
                state[0] = true;
            }

            @Override
            public void onResponse( Call call, final okhttp3.Response response ) throws IOException
            {
//                LogUtil.e( TAG, "onResponse: " + response.isSuccessful() + response.body().string() );
                if ( response.isSuccessful() )
                {
                    state[1] = true;
                }
                else
                {
                    try
                    {
                        JSONObject object = new JSONObject( response.body().string() );
                        if ( object != null && object.has( "error" ) )
                        {
                            JSONObject error = object.getJSONObject( "error" );
                            if ( error != null && error.has( "code" ) && error.has( "msg" ) )
                            {
                                int code = error.getInt( "code" );
                                final String msg = error.getString( "msg" );
                                if ( code == XLinkErrorCode.ERROR_API_DEVICE_MAC_ADDRESS_EXISTS.getValue() )
                                {
                                    state[1] = true;
                                }
                                else
                                {
                                    if ( isViewExist() )
                                    {
                                        getView().runOnUIThread( new Runnable() {
                                            @Override
                                            public void run()
                                            {
                                                getView().showRegisterDeviceFailed( device, msg );
                                            }
                                        } );
                                    }
                                }
                            }
                        }
                    }
                    catch ( JSONException e )
                    {
                        e.printStackTrace();
                    }
                }
                state[0] = true;
            }
        } );
        if ( isViewExist() )
        {
            getView().runOnUIThread( new Runnable() {
                @Override
                public void run()
                {
                    getView().showStartSubcribeDevice( device );
                }
            } );
        }
        while ( !state[0] );
        return state[1];
    }

//    private boolean registerDevice( final XDevice device )
//    {
//        final boolean[] state = new boolean[]{ false, false};
//        final DeviceApi.RegisterDeviceRequest request = new DeviceApi.RegisterDeviceRequest();
//        request.firmwareMod = device.getFirmwareMod();
//        request.firmwareVersion = device.getFirmwareVersion();
//        request.mac = device.getMacAddress();
//        request.mcuMod = device.getMcuMod();
//        request.mcuVersion = device.getMcuVersion();
//        request.name = device.getDeviceName();
//        request.productId = device.getProductId();
//        request.accessKey = XLinkSDK.getAccessToken();
//        Call<DeviceApi.RegisterDeviceResponse> responseCall = XLinkRestful.getApplicationApi()
//                                                                          .userRegisterDevice( XLinkSDK.getUser().getUid(), request );
//        LogUtil.e( TAG, "registerDevice: " + device.getMacAddress() + "  " + request.productId + "  " + XLinkSDK.getAccessToken() );
//        responseCall.enqueue( new Callback< DeviceApi.RegisterDeviceResponse >() {
//            @Override
//            public void onResponse( Call< DeviceApi.RegisterDeviceResponse > call, Response< DeviceApi.RegisterDeviceResponse > response )
//            {
//                LogUtil.e( TAG, "onResponse: " + response.isSuccessful() + "  " + response.code() + "  " + response.toString() );
//                if ( response.isSuccessful() )
//                {
//                    DeviceApi.RegisterDeviceResponse deviceResponse = response.body();
//                    if ( deviceResponse != null && deviceResponse.deviceId > 0 )
//                    {
//                        state[1] = true;
//                        if ( isViewExist() )
//                        {
//                            getView().runOnUIThread( new Runnable() {
//                                @Override
//                                public void run()
//                                {
//                                    getView().showRegisterDeviceSuccess( device );
//                                }
//                            } );
//                        }
//                    }
//                }
//                state[0] = true;
//            }
//
//            @Override
//            public void onFailure( Call< DeviceApi.RegisterDeviceResponse > call, final Throwable t )
//            {
//                state[0] = true;
//                state[1] = false;
//                if ( isViewExist() )
//                {
//                    getView().runOnUIThread( new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getView().showRegisterDeviceFailed( device, t.getMessage() );
//                        }
//                    } );
//                }
//            }
//        } );
//        while ( !state[0] );
//        return state[1];
//    }

    private boolean subcribeDevice( final XDevice device )
    {
        final boolean[] status = new boolean[]{false, false};
        XLinkAddDeviceTask.Builder builder = XLinkAddDeviceTask.newBuilder();
        builder.setXDevice( device );
        builder.setListener( new XLinkTaskListener< XDevice >() {
            @Override
            public void onError( final XLinkErrorCode xLinkErrorCode )
            {
                status[1] = false;
                LogUtil.e( TAG, "onError: " + device.getMacAddress() + "\t\t" + xLinkErrorCode.getValue() );
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showSubcribeFailed( device, getErrorMessage( xLinkErrorCode.getValue() ) );
                        }
                    } );
                }
                status[0] = true;
            }

            @Override
            public void onStart()
            {
                status[0] = false;
                LogUtil.e( TAG, "onStart: " );
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showStartSubcribeDevice( device );
                        }
                    } );
                }
            }

            @Override
            public void onComplete( final XDevice device )
            {
                status[1] = true;
                LogUtil.e( TAG, "onComplete: " );
                if ( isViewExist() )
                {
                    getView().runOnUIThread( new Runnable() {
                        @Override
                        public void run()
                        {
                            getView().showSubcribeSuccess( device );
                        }
                    } );
                }
                status[0] = true;
            }
        } );
        XLinkAddDeviceTask addDeviceTask = builder.build();
        XLinkSDK.startTask( addDeviceTask );
        while ( !status[0] );
        return status[1];
    }

    private boolean registerAndSubcribeDevice( XDevice device )
    {
        if ( registerDevice( device ) )
        {
            return subcribeDevice( device );
        }
        return false;
    }

    public void registerAndSubcribeDevices( final List<XDevice> devices )
    {
        if ( devices == null || devices.size() == 0 || mSubcribing )
        {
            return;
        }
        mSelectedDevices.clear();
        mSelectedDevices.addAll( devices );
        Runnable subcribeRunnable = new Runnable() {
            @Override
            public void run()
            {
                mSubcribing = true;
                while ( true )
                {
                    XDevice device = mSelectedDevices.poll();
                    boolean success = registerAndSubcribeDevice( device );
                    if ( !success )
                    {
                        break;
                    }
                    if ( mSelectedDevices.size() == 0 )
                    {
                        if ( isViewExist() )
                        {
                            getView().showSubcribeComplete();
                        }
                        break;
                    }
                }
                mSubcribing = false;
            }
        };
        new Thread( subcribeRunnable ).start();
    }
}
