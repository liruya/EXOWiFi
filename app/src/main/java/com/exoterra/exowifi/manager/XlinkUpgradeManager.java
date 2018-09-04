package com.exoterra.exowifi.manager;

import com.exoterra.exowifi.bean.Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.xlink.restful.XLinkRestful;
import cn.xlink.sdk.v5.module.main.XLinkSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class XlinkUpgradeManager
{
    private static final String TAG = "XlinkUpgradeManager";

    private static final String XLINK_UPGRADE_URL = "https://api2.xlink.cn/v2/upgrade/firmware/check/{device_id}";
    private static final String PLACE_HOLDER_DEVICEID = "{device_id}";

    public static void checkUpgrade( final Device device, CheckUpgradeCallback callback )
    {
        if ( device == null || device.getXDevice().getDeviceId() == 0 )
        {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put( "product_id", device.getXDevice().getProductId() );
        params.put( "type", "1" );
        params.put( "current_version", "" + device.getXDevice().getFirmwareVersion() );
        params.put( "identify", "0" );
        String json = new JSONObject( params ).toString();
        final String url = XLINK_UPGRADE_URL.replace( PLACE_HOLDER_DEVICEID, "" + device.getXDevice().getDeviceId() );
        RequestBody requestBody = RequestBody.create( MediaType.parse( "application/json; charset=utf-8" ), json );
        Request request = new Request.Builder()
                          .url( url )
                          .header( "Access-Token", XLinkSDK.getAccessToken() )
                          .post( requestBody )
                          .build();
        XLinkRestful.getApiHttpClient().newCall( request ).enqueue( callback );
    }

    public abstract static class CheckUpgradeCallback implements Callback
    {

        @Override
        public void onFailure( Call call, IOException e )
        {
            onGetUpgradeInfoFailed( e.getMessage() );
        }

        @Override
        public void onResponse( Call call, Response response ) throws IOException
        {
            if ( response.isSuccessful() )
            {
                try
                {
                    JSONObject object = new JSONObject( response.body().string() );
                    if ( object != null && object.has( "target_version" ) && object.has( "target_version_url" ) )
                    {
                        String target_version_url = object.getString( "target_version_url" );
                        int target_version = object.getInt( "target_version" );
                        onGetUpgradeInfoSuccess( target_version_url, target_version );
                    }
                    else
                    {
                        onGetUpgradeInfoFailed( "invalide response format" );
                    }
                }
                catch ( JSONException e )
                {
                    e.printStackTrace();
                    onGetUpgradeInfoFailed( "invalide response format" );
                }
            }
            else
            {
                onGetUpgradeInfoFailed( response.body().string() );
            }
        }

        public abstract void onGetUpgradeInfoSuccess( String target_version_url, int target_versioin );

        public abstract void onGetUpgradeInfoFailed( String msg );
    }
}
