package com.exoterra.comman;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

/**
 * Created by liruya on 2018/5/10.
 */

public class WifiUtil
{
    private static final String TAG = "WifiUtil";

    private static String getIp( int ip )
    {
        StringBuffer sb = new StringBuffer();
        sb.append( ip&0xFF ).append( '.' );
        sb.append( (ip>>8)&0xFF ).append( "." );
        sb.append( (ip>>16)&0xFF ).append( "." );
        sb.append( (ip>>24)&0xFF );
        String result = new String( sb );
        return result;
    }

    private static NetworkInfo getWifiNetworkInfo( @NonNull Context context ) {
        if ( context == null )
        {
            return null;
        }
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                                                                         .getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo;
    }

    private static WifiInfo getConnectedWiFiInfo( Context context )
    {
        WifiManager mWifiManager = (WifiManager) context
                                                 .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }

    public static boolean isWiFiConnected( Context context )
    {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo( context );
        boolean isWifiConnected = false;
        if (mWiFiNetworkInfo != null) {
            isWifiConnected = mWiFiNetworkInfo.isConnected();
        }
        return isWifiConnected;
    }

    public static String getGatewayMacAddress( Context context ) {
        WifiInfo wifiInfo = getConnectedWiFiInfo( context );
        String bssid = null;
        if ( wifiInfo != null && isWiFiConnected( context ) ) {
            bssid = wifiInfo.getBSSID();
        }
        return bssid;
    }

    public static String getGatewaySsid( Context context )
    {
        WifiInfo wifiInfo = getConnectedWiFiInfo( context );
        String ssid = null;
        if ( wifiInfo != null && isWiFiConnected( context ) ) {
            ssid = wifiInfo.getSSID();
        }
        return ssid;
    }

    public static String getGatewayIp( Context context )
    {
        WifiManager wifiManager = (WifiManager) context
                                                 .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if ( dhcpInfo != null )
        {
            return getIp( dhcpInfo.gateway );
        }
        return null;
    }

    public static String getLocalMacAddress( Context context )
    {
        WifiInfo wifiInfo = getConnectedWiFiInfo( context );
        String mac = null;
        if ( wifiInfo != null && isWiFiConnected( context ) ) {
            mac = wifiInfo.getMacAddress();
        }
        return mac;
    }

    public static String getLocalIp( Context context )
    {
        WifiInfo wifiInfo = getConnectedWiFiInfo( context );
        String ip = null;
        if ( wifiInfo != null && isWiFiConnected( context ) ) {
            int a = wifiInfo.getIpAddress();
            return getIp( a );
        }
        return ip;
    }
}
