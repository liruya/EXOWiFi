package com.exoterra.exowifi.bean;

import com.exoterra.exowifi.manager.DeviceManager;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;

public class SingleSocket extends Device
{
    private final String TAG = "SingleSocket";

    public static final int SENSOR_COUNT_MAX = 2;
    public static final int TIMER_COUNT_MAX = 15;

    private final int INDEX_ZONE = 0;
    private final int INDEX_DP2_OPT = 1;
    private final int INDEX_DP3_OPT = 2;
    private final int INDEX_DP4_OPT = 3;
    private final int INDEX_SENSOR1_AVAILABLE = 4;
    private final int INDEX_SENSOR1_TYPE = 5;
    private final int INDEX_SENSOR1_VALUE = 6;
    private final int INDEX_SENSOR1_NOTIFY_ENABLE = 7;
    private final int INDEX_SENSOR1_LINKAGE_ENABLE = 8;
    private final int INDEX_SENSOR1_LINKAGE_ARGS = 9;
    private final int INDEX_SENSOR2_AVAILABLE = 10;
    private final int INDEX_SENSOR2_TYPE = 11;
    private final int INDEX_SENSOR2_VALUE = 12;
    private final int INDEX_SENSOR2_NOTIFY_ENABLE = 13;
    private final int INDEX_SENSOR2_LINKAGE_ENABLE = 14;
    private final int INDEX_SENSOR2_LINKAGE_ARGS = 15;
    private final int INDEX_SOCKET_NAME = 16;
    private final int INDEX_SOCKET_POWER = 17;
    private final int INDEX_SOCKET_TIMER = 18;

    public SingleSocket( XDevice xDevice )
    {
        super( xDevice );
    }

    public short getZone()
    {
        return getUShort( INDEX_ZONE );
    }

    public XLinkDataPoint setZone( short zone )
    {
        if ( zone < 0 || zone > 2399 )
        {
            return null;
        }
        return setUShort( INDEX_ZONE, zone );
    }

    public boolean getSensor1Available()
    {
        return getBoolean( INDEX_SENSOR1_AVAILABLE );
    }

    public @Sensor.SENSOR_TYPE
    int getSensor1Type()
    {
        return getByte( INDEX_SENSOR1_TYPE );
    }

    public short getSensor1Value()
    {
        return getShort( INDEX_SENSOR1_VALUE );
    }

    public boolean getSensor1NotifyEnable()
    {
        return getBoolean( INDEX_SENSOR1_NOTIFY_ENABLE );
    }

    public XLinkDataPoint setSensor1NotifyEnable( boolean enable )
    {
        return setBoolean( INDEX_SENSOR1_NOTIFY_ENABLE, enable );
    }

    public boolean getSensor1LinkageEnable()
    {
        return getBoolean( INDEX_SENSOR1_LINKAGE_ENABLE );
    }

    public XLinkDataPoint setSensor1LinkageEnable( boolean enable )
    {
        return setBoolean( INDEX_SENSOR1_LINKAGE_ENABLE, enable );
    }

    public SensorLinkageArgs getSensor1LinkageArgs()
    {
        byte[] array = getByteArray( INDEX_SENSOR1_LINKAGE_ARGS );
        if ( array == null || array.length < 2
             || array.length > SensorLinkageArgs.SENSOR_ARGS_MAX + 2
             || array[1]+2 != array.length )
        {
            return null;
        }
        SensorLinkageArgs linkageArgs = new SensorLinkageArgs();
        linkageArgs.setVersion( array[0] );
        linkageArgs.setLength( array[1] );
        System.arraycopy( array, 2, linkageArgs.getArgs(), 0, array[1] );
        return linkageArgs;
    }

    public XLinkDataPoint setSensor1LinkageArgs( SensorLinkageArgs args )
    {
        byte[] array = null;
        if ( args != null )
        {
            array = args.toArray();
        }
        return setByteArray( INDEX_SENSOR1_LINKAGE_ARGS, array );
    }

    public Sensor getSensor1()
    {
        return new Sensor( getSensor1Type(),
                           getSensor1Value(),
                           getSensor1NotifyEnable(),
                           getSensor1LinkageEnable(),
                           getSensor1LinkageArgs() );
    }

    public boolean getSensor2Available()
    {
        return getBoolean( INDEX_SENSOR2_AVAILABLE );
    }

    public @Sensor.SENSOR_TYPE int getSensor2Type()
    {
        return getByte( INDEX_SENSOR2_TYPE );
    }

    public short getSensor2Value()
    {
        return getShort( INDEX_SENSOR2_VALUE );
    }

    public boolean getSensor2NotifyEnable()
    {
        return getBoolean( INDEX_SENSOR2_NOTIFY_ENABLE );
    }

    public XLinkDataPoint setSensor2NotifyEnable( boolean enable )
    {
        return setBoolean( INDEX_SENSOR2_NOTIFY_ENABLE, enable );
    }

    public boolean getSensor2LinkageEnable()
    {
        return getBoolean( INDEX_SENSOR2_LINKAGE_ENABLE );
    }

    public XLinkDataPoint setSensor2LinkageEnable( boolean enable )
    {
        return setBoolean( INDEX_SENSOR2_LINKAGE_ENABLE, enable );
    }

    public SensorLinkageArgs getSensor2LinkageArgs()
    {
        byte[] array = getByteArray( INDEX_SENSOR2_LINKAGE_ARGS );
        if ( array == null || array.length < 2
             || array.length > SensorLinkageArgs.SENSOR_ARGS_MAX + 2
             || array[1]+2 != array.length )
        {
            return null;
        }
        SensorLinkageArgs linkageArgs = new SensorLinkageArgs();
        linkageArgs.setVersion( array[0] );
        linkageArgs.setLength( array[1] );
        System.arraycopy( array, 2, linkageArgs.getArgs(), 0, array[1] );
        return linkageArgs;
    }

    public XLinkDataPoint setSensor2LinkageArgs( SensorLinkageArgs args )
    {
        byte[] array = null;
        if ( args != null )
        {
            array = args.toArray();
        }
        return setByteArray( INDEX_SENSOR1_LINKAGE_ARGS, array );
    }

    public Sensor getSensor2()
    {
        return new Sensor( getSensor2Type(),
                           getSensor2Value(),
                           getSensor2NotifyEnable(),
                           getSensor2LinkageEnable(),
                           getSensor2LinkageArgs() );
    }

    public String getName()
    {
        return getString( INDEX_SOCKET_NAME );
    }

    public XLinkDataPoint setName( String name )
    {
        return setString( INDEX_SOCKET_NAME, name );
    }

    public boolean getPower()
    {
        return getBoolean( INDEX_SOCKET_POWER );
    }

    public XLinkDataPoint setPower( boolean power )
    {
        return setBoolean( INDEX_SOCKET_POWER, power );
    }

    public List<SocketTimer> getTimer()
    {
        List<SocketTimer> timers = new ArrayList<>();
        byte[] array = getByteArray( INDEX_SOCKET_TIMER );
        if ( array == null || array.length < 4 || array.length > 64 || array.length%4 != 0 )
        {
            return timers;
        }
        else
        {
            if ( array[3] != 0 || array[2] != 0 || array[1] != 0 || array[0] < 0 || array[0] > TIMER_COUNT_MAX )
            {
                return timers;
            }
            int count = array[0];
            if ( count > (array.length-4)/4 )
            {
                count = (array.length-4)/4;
            }
            for ( int i = 4; i < 4 + count * 4; i += 4 )
            {
                SocketTimer tmr = new SocketTimer.Builder().creat( array[i], array[i+1], array[i+2], array[i+3] );
                if ( tmr == null )
                {
                    break;
                }
                timers.add( tmr );
            }

        }
        return timers;
    }

    public XLinkDataPoint setTimer( List<SocketTimer> timers )
    {
        byte[] array;
        if ( timers == null || timers.size() == 0 )
        {
            array = new byte[]{0, 0, 0, 0};
        }
        else
        {
            array = new byte[timers.size()*4+4];
            array[0] = (byte) timers.size();
            array[1] = 0;
            array[2] = 0;
            array[3] = 0;
            for ( int i = 0; i < timers.size(); i++ )
            {
                if ( timers.get( i ) == null )
                {
                    array[0] = (byte) i;
                    break;
                }
                byte[] bts = timers.get( i ).toArray();
                System.arraycopy( bts, 0, array, 4+i*4, 4 );
            }
        }
        return setByteArray( INDEX_SOCKET_TIMER, array );
    }

    public void setSocketZone( short zone )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setZone( zone );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor1NotifyEnable( boolean enable )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setSensor1NotifyEnable( enable );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor1LinkageEnable( boolean enable )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setSensor1LinkageEnable( enable );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor1LinkageArgs( SensorLinkageArgs args )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setSensor1LinkageArgs( args );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor1Linkage(boolean notify, boolean linkage, SensorLinkageArgs args)
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setSensor1NotifyEnable( notify );
        XLinkDataPoint dp2 = setSensor1LinkageEnable( linkage );
        XLinkDataPoint dp3 = setSensor1LinkageArgs( args );
        if ( dp1 != null && dp2 != null && dp3 != null )
        {
            dps.add( dp1 );
            dps.add( dp2 );
            dps.add( dp3 );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor2NotifyEnable( boolean enable )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setSensor2NotifyEnable( enable );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor2LinkageEnable( boolean enable )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setSensor2LinkageEnable( enable );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor2LinkageArgs( SensorLinkageArgs args )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setSensor2LinkageArgs( args );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketSensor2Linkage(boolean notify, boolean linkage, SensorLinkageArgs args)
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp1 = setSensor2NotifyEnable( notify );
        XLinkDataPoint dp2 = setSensor2LinkageEnable( linkage );
        XLinkDataPoint dp3 = setSensor2LinkageArgs( args );
        if ( dp1 != null && dp2 != null && dp3 != null )
        {
            dps.add( dp1 );
            dps.add( dp2 );
            dps.add( dp3 );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketName( String name )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setName( name );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketPower( boolean power )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setPower( power );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }

    public void setSocketTimer( List<SocketTimer> timers )
    {
        List<XLinkDataPoint> dps = new ArrayList<>();
        XLinkDataPoint dp = setTimer( timers );
        if ( dp != null )
        {
            dps.add( dp );
        }
        DeviceManager.getInstance().setDataPoints( getXDevice(), dps, null );
    }
}
