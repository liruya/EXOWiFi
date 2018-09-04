package com.exoterra.exowifi.bean;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.exoterra.comman.LogUtil;
import com.exoterra.exowifi.manager.DeviceManager;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;

/**
 * Created by liruya on 2018/4/18.
 */

public class ExoLightStrip extends Device
{
    private final String TAG = "ExoLightStrip";

    private final int MAX_CHANNEL_COUNT = 8;
    private final int CUSTOM_COUNT = 4;
    private final int MAX_COMMAND_LENGTH = 32;
    private final int POINT_COUNT_MIN = 4;
    private final int POINT_COUNT_MAX = 10;

    private final int INDEX_CHANNEL_COUNT = 0;
    private final int INDEX_ZONE = 1;
    private final int INDEX_CHN1_NAME = 2;
    private final int INDEX_CHN2_NAME = 3;
    private final int INDEX_CHN3_NAME = 4;
    private final int INDEX_CHN4_NAME = 5;
    private final int INDEX_CHN5_NAME = 6;
    private final int INDEX_CHN6_NAME = 7;
    private final int INDEX_CHN7_NAME = 8;
    private final int INDEX_CHN8_NAME = 9;
    private final int INDEX_MODE = 10;
    private final int INDEX_POWER = 11;
    private final int INDEX_CHN1_BRIGHT = 12;
    private final int INDEX_CHN2_BRIGHT = 13;
    private final int INDEX_CHN3_BRIGHT = 14;
    private final int INDEX_CHN4_BRIGHT = 15;
    private final int INDEX_CHN5_BRIGHT = 16;
    private final int INDEX_CHN6_BRIGHT = 17;
    private final int INDEX_CHN7_BRIGHT = 18;
    private final int INDEX_CHN8_BRIGHT = 19;
    private final int INDEX_CUSTOM1 = 20;
    private final int INDEX_CUSTOM2 = 21;
    private final int INDEX_CUSTOM3 = 22;
    private final int INDEX_CUSTOM4 = 23;
    private final int INDEX_SUNRISE_START = 24;
    private final int INDEX_SUNRISE_END = 25;
    private final int INDEX_DAY_BRIGHT = 26;
    private final int INDEX_SUNSET_START = 27;
    private final int INDEX_SUNSET_END = 28;
    private final int INDEX_NIGHT_BRIGHT = 29;
    private final int INDEX_TURNOFF_ENABLED = 30;
    private final int INDEX_TURNOFF_TIME = 31;
    private final int INDEX_POINT_COUNT = 32;
    private final int INDEX_POINT0_TIMER = 33;
    private final int INDEX_POINT0_BRIGHT = 34;
    private final int INDEX_POINT1_TIMER = 35;
    private final int INDEX_POINT1_BRIGHT = 36;
    private final int INDEX_POINT2_TIMER = 37;
    private final int INDEX_POINT2_BRIGHT = 38;
    private final int INDEX_POINT3_TIMER = 39;
    private final int INDEX_POINT3_BRIGHT = 40;
    private final int INDEX_POINT4_TIMER = 41;
    private final int INDEX_POINT4_BRIGHT = 42;
    private final int INDEX_POINT5_TIMER = 43;
    private final int INDEX_POINT5_BRIGHT = 44;
    private final int INDEX_POINT6_TIMER = 45;
    private final int INDEX_POINT6_BRIGHT = 46;
    private final int INDEX_POINT7_TIMER = 47;
    private final int INDEX_POINT7_BRIGHT = 48;
    private final int INDEX_POINT8_TIMER = 49;
    private final int INDEX_POINT8_BRIGHT = 50;
    private final int INDEX_POINT9_TIMER = 51;
    private final int INDEX_POINT9_BRIGHT = 52;
    private final int INDEX_PREVIEW = 111;
    private final int INDEX_UPGRADE = 121;
    private final int INDEX_COMMAND = 127;

    public static final int MODE_MANUAL = 0;
    public static final int MODE_AUTO = 1;
    public static final int MODE_PRO = 2;

    @IntDef( {MODE_MANUAL, MODE_AUTO, MODE_PRO} )
    public @interface Mode
    {

    }

    public ExoLightStrip( XDevice XDevice )
    {
        super( XDevice );
    }

    public int getChannelCount()
    {
        return getByte( INDEX_CHANNEL_COUNT );
    }

    public int getZone()
    {
        return getUShort( INDEX_ZONE );
    }

    public XLinkDataPoint setZone( int zone )
    {
        return setUShort( INDEX_ZONE, (short) zone );
    }

    public String getChannelName( int chn )
    {
        if ( chn < 0 || chn >= MAX_CHANNEL_COUNT )
        {
            return "";
        }
        return getString( INDEX_CHN1_NAME + chn );
    }

    public String[] getChannelNames()
    {
        String[] names = new String[getChannelCount()];
        for ( int i = 0; i < getChannelCount(); i++ )
        {
            names[i] = getChannelName( i );
        }
        return names;
    }

//    public boolean isMode()
//    {
//        return getBoolean( INDEX_MODE );
//    }
//
//    public XLinkDataPoint setMode( boolean mode )
//    {
//        return setBoolean( INDEX_MODE, mode );
//    }

    @Mode
    public int getMode()
    {
        int mode = getByte( INDEX_MODE );
        if ( mode == MODE_AUTO )
        {
            return MODE_AUTO;
        }
        else if ( mode == MODE_PRO )
        {
            return MODE_PRO;
        }
        else
        {
            return MODE_MANUAL;
        }
    }

    public XLinkDataPoint setMode( @Mode int mode )
    {
        return setByte( INDEX_MODE, (byte) mode );
    }

    public boolean isPower()
    {
        return getBoolean( INDEX_POWER );
    }

    public XLinkDataPoint setPower( boolean power )
    {
        return setBoolean( INDEX_POWER, power );
    }

    public int getBright( int chn )
    {
        if ( chn >= MAX_CHANNEL_COUNT || chn < 0 )
        {
            return 0;
        }
        return getUShort( INDEX_CHN1_BRIGHT + chn );
    }

    public XLinkDataPoint setBright( int chn, int bright )
    {
        if ( chn >= MAX_CHANNEL_COUNT || chn < 0 )
        {
            return null;
        }
        return setUShort( INDEX_CHN1_BRIGHT + chn, (short) bright );
    }

    public byte[] getCustom( int idx )
    {
        if ( idx >= CUSTOM_COUNT || idx < 0 )
        {
            return null;
        }
        return getByteArray( INDEX_CUSTOM1 + idx );
    }

    public XLinkDataPoint setCustom( int idx, byte[] custom )
    {
        if ( idx >= CUSTOM_COUNT || idx < 0 || custom == null || custom.length != getChannelCount() )
        {
            return null;
        }
        for ( int i = 0; i < custom.length; i++ )
        {
            if ( custom[i] > 100 || custom[i] < 0 )
            {
                custom[i] = 100;
            }
        }
        return setByteArray( INDEX_CUSTOM1 + idx, custom );
    }

    public int getSunriseStart()
    {
        return getUShort( INDEX_SUNRISE_START );
    }

    public XLinkDataPoint setSunriseStart( int sunriseStart )
    {
        if ( sunriseStart < 0 || sunriseStart >= 1440 )
        {
            return null;
        }
        return setUShort( INDEX_SUNRISE_START, (short) sunriseStart );
    }

    public int getSunriseEnd()
    {
        return getUShort( INDEX_SUNRISE_END );
    }

    public XLinkDataPoint setSunriseEnd( int sunriseEnd )
    {
        if ( sunriseEnd < 0 || sunriseEnd >= 1440 )
        {
            return null;
        }
        return setUShort( INDEX_SUNRISE_END, (short) sunriseEnd );
    }

    public byte[] getDayBright()
    {
        return getByteArray( INDEX_DAY_BRIGHT );
    }

    public XLinkDataPoint setDayBright( byte[] dayBright )
    {
        if ( dayBright == null || dayBright.length != getChannelCount() )
        {
            return null;
        }
        for ( int i = 0; i < dayBright.length; i++ )
        {
            if ( dayBright[i] > 100 || dayBright[i] < 0 )
            {
                dayBright[i] = 100;
            }
        }
        return setByteArray( INDEX_DAY_BRIGHT, dayBright );
    }

    public int getSunsetStart()
    {
        return getUShort( INDEX_SUNSET_START );
    }

    public XLinkDataPoint setSunsetStart( int sunsetStart )
    {
        if ( sunsetStart < 0 || sunsetStart >= 1440 )
        {
            return null;
        }
        return setUShort( INDEX_SUNSET_START, (short) sunsetStart );
    }

    public int getSunsetEnd()
    {
        return getUShort( INDEX_SUNSET_END );
    }

    public XLinkDataPoint setSunsetEnd( int sunsetEnd )
    {
        if ( sunsetEnd < 0 || sunsetEnd >= 1440 )
        {
            return null;
        }
        return setUShort( INDEX_SUNSET_END, (short) sunsetEnd );
    }

    public byte[] getNightBright()
    {
        return getByteArray( INDEX_NIGHT_BRIGHT );
    }

    public XLinkDataPoint setNightBright( byte[] nightBright )
    {
        if ( nightBright == null || nightBright.length != getChannelCount() )
        {
            return null;
        }
        for ( int i = 0; i < nightBright.length; i++ )
        {
            if ( nightBright[i] > 100 || nightBright[i] < 0 )
            {
                nightBright[i] = 100;
            }
        }
        return setByteArray( INDEX_NIGHT_BRIGHT, nightBright );
    }

    public boolean isTurnoffEnabled()
    {
        return getBoolean( INDEX_TURNOFF_ENABLED );
    }

    public XLinkDataPoint setTurnoffEnabled( boolean turnoffEnabled )
    {
        return setBoolean( INDEX_TURNOFF_ENABLED, turnoffEnabled );
    }

    public int getTurnoffTime()
    {
        return getUShort( INDEX_TURNOFF_TIME );
    }

    public XLinkDataPoint setTurnoffTime( int turnoffTime )
    {
        if ( turnoffTime < 0 || turnoffTime >= 1440 )
        {
            return null;
        }
        return setUShort( INDEX_TURNOFF_TIME, (short) turnoffTime );
    }

    public int getPointCount()
    {
        return getByte( INDEX_POINT_COUNT );
    }

    public XLinkDataPoint setPointCount( int count )
    {
        if ( count >= POINT_COUNT_MIN && count <= POINT_COUNT_MAX )
        {
            return setByte( INDEX_POINT_COUNT, (byte) count );
        }
        return null;
    }

    public int getPointTimer( int idx )
    {
        if ( idx >= 0 && idx < POINT_COUNT_MAX )
        {
            return getUShort( INDEX_POINT0_TIMER + idx * 2 );
        }
        return 0;
    }

    public int[] getPointTimers()
    {
        int[] timers = new int[getPointCount()];
        for ( int i = 0; i < getPointCount(); i++ )
        {
            timers[i] = getPointTimer( i );
        }
        return timers;
    }

    public XLinkDataPoint setPointTimer( int idx, int tmr )
    {
        if ( idx < 0 || idx >= POINT_COUNT_MAX || tmr < 0 || tmr >= 1440 )
        {
            return null;
        }
        return setUShort( INDEX_POINT0_TIMER + idx * 2, (short) tmr );
    }

    public byte[] getPointBright( int idx )
    {
        if ( idx >= 0 && idx < POINT_COUNT_MAX )
        {
            return getByteArray( INDEX_POINT0_BRIGHT + idx * 2 );
        }
        return null;
    }

    public byte[][] getPointBrights()
    {
        byte[][] brights = new byte[getPointCount()][getChannelCount()];
        for ( int i = 0; i < getPointCount(); i++ )
        {
            for ( int j = 0; j < getChannelCount(); j++ )
            {
                brights[i][j] = getPointBright( i )[j];
            }
        }
        return brights;
    }

    public XLinkDataPoint setPointBright( int idx, byte[] bright )
    {
        if ( idx < 0 || idx >= POINT_COUNT_MAX || bright == null || bright.length != getChannelCount() )
        {
            return null;
        }
        for ( int i = 0; i < bright.length; i++ )
        {
            if ( bright[i] > 100 || bright[i] < 0 )
            {
                bright[i] = 100;
            }
        }
        return setByteArray( INDEX_POINT0_BRIGHT + idx * 2, bright );
    }

    public boolean getPreviewFlag()
    {
        return getBoolean( INDEX_PREVIEW );
    }

    public XLinkDataPoint setPreviewFlag( boolean flag )
    {
        return setBoolean( INDEX_PREVIEW, flag );
    }

    public String getUpgrade()
    {
        return getString( INDEX_UPGRADE );
    }

    public XLinkDataPoint setUpgrade( String url )
    {
        if ( TextUtils.isEmpty( url ) )
        {
            return null;
        }
        return setString( INDEX_UPGRADE, url );
    }

    public byte[] getCommand()
    {
        return getByteArray( INDEX_COMMAND );
    }

    public XLinkDataPoint setCommand( byte[] command )
    {
        if ( command == null || command.length > MAX_COMMAND_LENGTH || command.length == 0 )
        {
            return null;
        }
        return setByteArray( INDEX_COMMAND, command );
    }

//    public int[] getColors( Map<String, Integer> colorMap )
//    {
//        int[] colors = new int[getChannelCount()];
//        for ( int i = 0; i < colors.length; i++ )
//        {
//            colors[i] = 0xFFFFFFFF;
//            String name = getChannelName( i );
//            if ( !TextUtils.isEmpty( name ) )
//            {
//                if ( name.endsWith( "\0" ) )
//                {
//                    name = name.substring( 0, name.length() - 1 );
//                }
//                name = name.toLowerCase();
//                if ( colorMap != null && colorMap.containsKey( name ) )
//                {
//                    colors[i] = colorMap.get( name );
//                }
//            }
//        }
//        return colors;
//    }

//    public void setLightMode( boolean mode )
//    {
//        List<XLinkDataPoint> dps = new ArrayList<>();
//        XLinkDataPoint dp = setMode( mode );
//        if ( dp != null )
//        {
//            dps.add( dp );
//        }
//        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
//    }

    public void setLightMode( @Mode int mode )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setMode( mode );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setLightPower( boolean power )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setPower( power );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setLightCustom( int idx, byte[] progress )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setCustom( idx, progress );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setLightBright( int chn, int value )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setBright( chn, value );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setAllBrights( int[] values )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        if ( values != null && values.length == getChannelCount() )
        {
            for ( int i = 0; i < values.length; i++ )
            {
                XLinkDataPoint dp = setBright( i, values[i] );
                if ( dp != null )
                {
                    dps.add( dp );
                }
            }
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightSunrise( int start, int end )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setSunriseStart( start );
        XLinkDataPoint dp2 = setSunriseEnd( end );
        if ( dp1 != null && dp2 != null )
        {
            dps.add( dp1 );
            dps.add( dp2 );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightDayBright( byte[] brights )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setDayBright( brights );
        if ( dp != null )
        {
            dps.add( dp );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightSunset( int start, int end )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setSunsetStart( start );
        XLinkDataPoint dp2 = setSunsetEnd( end );
        if ( dp1 != null && dp2 != null )
        {
            dps.add( dp1 );
            dps.add( dp2 );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightNightBright( byte[] brights )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setNightBright( brights );
        if ( dp != null )
        {
            dps.add( dp );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightTurnoff( boolean enable, int time )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setTurnoffEnabled( enable );
        XLinkDataPoint dp2 = setTurnoffTime( time );
        if ( dp1 != null && dp2 != null )
        {
            dps.add( dp1 );
            dps.add( dp2 );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightPointCount( int count )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setPointCount( count );
        if ( dp != null )
        {
            dps.add( dp );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightPointTimer( int idx, int tmr )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setPointTimer( idx, tmr );
        if ( dp != null )
        {
            dps.add( dp );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightPointBright( int idx, byte[] bright )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setPointBright( idx, bright );
        if ( dp != null )
        {
            dps.add( dp );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightPoint( int pointCount, int[] pointTimers, byte[][] pointBrights )
    {
        if ( pointCount < POINT_COUNT_MIN || pointCount > POINT_COUNT_MAX
             || pointTimers.length != pointCount || pointBrights.length != pointCount )
        {
            return;
        }
        LogUtil.e( "PRO", "setLightPoint: " );
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setPointCount( pointCount );
        if ( dp == null )
        {
            return;
        }
        dps.add( dp );
        for ( int i = 0; i < pointCount; i++ )
        {
            if ( pointTimers[i] < 0 || pointTimers[i] > 1439 || pointBrights[i].length != getChannelCount() )
            {
                return;
            }
            XLinkDataPoint dp1 = setPointTimer( i, pointTimers[i] );
            XLinkDataPoint dp2 = setPointBright( i, pointBrights[i] );
            if ( dp1 == null || dp2 == null )
            {
                return;
            }
            dps.add( dp1 );
            dps.add( dp2 );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

//    public void startPreview()
//    {
//        List<XLinkDataPoint> dps = new ArrayList<>();
//        XLinkDataPoint dp1 = setPreviewFlag( true );
//        if ( dp1 != null )
//        {
//            dps.add( dp1 );
//            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
//        }
//    }

    public void setLightPreview( boolean flag )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setPreviewFlag( flag );
        if ( dp1 != null )
        {
            dps.add( dp1 );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }

    public void setLightUpgrade( String url )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setUpgrade( url );
        if ( dp1 != null )
        {
            dps.add( dp1 );
            DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
        }
    }
}
