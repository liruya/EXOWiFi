package com.exoterra.exowifi.bean;

import android.support.annotation.IntDef;

import java.util.Arrays;

public class Sensor
{
    public static final int SENSOR_NONE = 0;
    public static final int SENSOR_UNKOWN = 1;
    public static final int SENSOR_TEMPERATURE = 2;
    public static final int SENSOR_HUMIDITY = 3;
    public static final int SENSOR_WATERLEVEL = 4;
    public static final int SENSOR_INTENSITY = 5;
    public static final int SENSOR_THERMOSTAT = 6;

    @IntDef ( { SENSOR_UNKOWN,
                SENSOR_TEMPERATURE,
                SENSOR_HUMIDITY,
                SENSOR_WATERLEVEL,
                SENSOR_INTENSITY,
                SENSOR_THERMOSTAT} )
    public @interface SENSOR_TYPE
    {

    }

    private @SENSOR_TYPE int mType;
    private short mValue;
    private boolean mNotifyEnable;
    private boolean mLinkageEnable;
    private SensorLinkageArgs mLinkageArgs;

    public Sensor( @SENSOR_TYPE int type, short value, boolean notifyEnable, boolean linkageEnable, SensorLinkageArgs linkageArgs )
    {
        mType = type;
        mValue = value;
        mNotifyEnable = notifyEnable;
        mLinkageEnable = linkageEnable;
        mLinkageArgs = linkageArgs;
    }

    public @SENSOR_TYPE int getType()
    {
        return mType;
    }

    public void setType( @SENSOR_TYPE int type )
    {
        mType = type;
    }

    public short getValue()
    {
        return mValue;
    }

    public void setValue( short value )
    {
        mValue = value;
    }

    public boolean isNotifyEnable()
    {
        return mNotifyEnable;
    }

    public void setNotifyEnable( boolean notifyEnable )
    {
        mNotifyEnable = notifyEnable;
    }

    public boolean isLinkageEnable()
    {
        return mLinkageEnable;
    }

    public void setLinkageEnable( boolean linkageEnable )
    {
        mLinkageEnable = linkageEnable;
    }

    public SensorLinkageArgs getLinkageArgs()
    {
        return mLinkageArgs;
    }

    public void setLinkageArgs( SensorLinkageArgs linkageArgs )
    {
        mLinkageArgs = linkageArgs;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "Type: " ).append( mType )
          .append( "\nValue: " ).append( mValue )
          .append( "\nNotify: " ).append( mNotifyEnable )
          .append( "\nLinkage: " ).append( mLinkageEnable )
          .append( "\nVersion:" ).append( mLinkageArgs.getVersion() )
          .append( "\tlength:" ).append( mLinkageArgs.getLength() )
          .append( "\tArgs: " ).append( Arrays.toString( mLinkageArgs.getArgs() ) );
        return new String( sb );
    }
}
