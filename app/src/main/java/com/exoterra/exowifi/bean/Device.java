package com.exoterra.exowifi.bean;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.sdk.core.model.DataPointValueType;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;

/**
 * Created by liruya on 2018/4/18.
 */

public class Device
{
    private XDevice mXDevice;
    private List<XLinkDataPoint> mDataPointList;

    public Device( XDevice xDevice )
    {
        mXDevice = xDevice;
        mDataPointList = new ArrayList<>();
    }

    public XDevice getXDevice()
    {
        return mXDevice;
    }

    public void setXDevice( XDevice XDevice )
    {
        mXDevice = XDevice;
    }

    public List< XLinkDataPoint > getDataPointList()
    {
        return mDataPointList;
    }

    public void setDataPointList( List< XLinkDataPoint > dataPointList )
    {
        mDataPointList = dataPointList;
    }

    public void setDataPoint( XLinkDataPoint dataPoint )
    {
        for ( XLinkDataPoint dp : mDataPointList )
        {
            if ( dp.getIndex() == dataPoint.getIndex() && dp.getType() == dataPoint.getType() )
            {
                dp.setValue( dataPoint.getValue() );
                return;
            }
        }
    }

    public XLinkDataPoint getDataPoint( int index, DataPointValueType type )
    {
        for ( XLinkDataPoint dp : mDataPointList )
        {
            if ( dp.getIndex() == index && dp.getType() == type )
            {
                return dp;
            }
        }
        return null;
    }

    public XLinkDataPoint getDataPoint( int index )
    {
        for ( XLinkDataPoint dp : mDataPointList )
        {
            if ( dp.getIndex() == index )
            {
                return dp;
            }
        }
        return null;
    }

    public XLinkDataPoint setValue( int index, DataPointValueType type, Object value )
    {
        for ( XLinkDataPoint dp : mDataPointList )
        {
            if ( dp.getIndex() == index && dp.getType() == type )
            {
                if ( !dp.isWritable() )
                {
                    return null;
                }
                dp.setValue( value );
                return dp;
            }
        }
        XLinkDataPoint dataPoint = new XLinkDataPoint( index, type );
        dataPoint.setValue( value );
        mDataPointList.add( dataPoint );
        return dataPoint;
    }

    public Object getValue( int index, DataPointValueType type )
    {
        for ( XLinkDataPoint dp : mDataPointList )
        {
            if ( dp.getIndex() == index && dp.getType() == type )
            {
                return dp.getValue();
            }
        }
        return null;
    }

    protected XLinkDataPoint setBoolean( int index, boolean value )
    {
        return setValue( index, DataPointValueType.BOOL, value );
    }

    protected boolean getBoolean( int index )
    {
        Object value = getValue( index, DataPointValueType.BOOL );
        if ( value != null && value instanceof Boolean )
        {
            return (boolean) value;
        }
        return false;
    }

    protected XLinkDataPoint setByte( int index, byte value )
    {
        return setValue( index, DataPointValueType.BYTE, value );
    }

    protected byte getByte( int index )
    {
        Object value = getValue( index, DataPointValueType.BYTE );
        if ( value != null && value instanceof Byte )
        {
            return (byte) value;
        }
        return 0;
    }

    protected XLinkDataPoint setShort( int index, short value )
    {
        return setValue( index, DataPointValueType.SHORT, value );
    }

    protected short getShort( int index )
    {
        Object value = getValue( index, DataPointValueType.SHORT );
        if ( value != null && value instanceof Short )
        {
            return (short) value;
        }
        return 0;
    }

    protected XLinkDataPoint setUShort( int index, short value )
    {
        return setValue( index, DataPointValueType.USHORT, value );
    }

    protected short getUShort( int index )
    {
        Object value = getValue( index, DataPointValueType.USHORT );
        if ( value != null && value instanceof Short )
        {
            return (short) value;
        }
        return 0;
    }

    protected XLinkDataPoint setInt( int index, int value )
    {
        return setValue( index, DataPointValueType.INT, value );
    }

    protected int getInt( int index )
    {
        Object value = getValue( index, DataPointValueType.INT );
        if ( value != null && value instanceof Integer )
        {
            return (int) value;
        }
        return 0;
    }

    protected XLinkDataPoint setUInt( int index, int value )
    {
        return setValue( index, DataPointValueType.UINT, value );
    }

    protected int getUInt( int index )
    {
        Object value = getValue( index, DataPointValueType.UINT );
        if ( value != null && value instanceof Integer )
        {
            return (int) value;
        }
        return 0;
    }

    protected XLinkDataPoint setLong( int index, long value )
    {
        return setValue( index, DataPointValueType.LONG, value );
    }

    protected long getLong( int index )
    {
        Object value = getValue( index, DataPointValueType.LONG );
        if ( value != null && value instanceof Long )
        {
            return (long) value;
        }
        return 0;
    }

    protected XLinkDataPoint setULong( int index, long value )
    {
        return setValue( index, DataPointValueType.ULONG, value );
    }

    protected long getULong( int index )
    {
        Object value = getValue( index, DataPointValueType.ULONG );
        if ( value != null && value instanceof Long )
        {
            return (long) value;
        }
        return 0;
    }

    protected XLinkDataPoint setFloat( int index, float value )
    {
        return setValue( index, DataPointValueType.FLOAT, value );
    }

    protected float getFloat( int index )
    {
        Object value = getValue( index, DataPointValueType.FLOAT );
        if ( value != null && value instanceof Float )
        {
            return (float) value;
        }
        return 0.0f;
    }

    protected XLinkDataPoint setDouble( int index, double value )
    {
        return setValue( index, DataPointValueType.DOUBLE, value );
    }

    protected double getDouble( int index )
    {
        Object value = getValue( index, DataPointValueType.DOUBLE );
        if ( value != null && value instanceof Double )
        {
            return (double) value;
        }
        return 0;
    }

    protected XLinkDataPoint setString( int index, String value )
    {
        return setValue( index, DataPointValueType.STRING, value );
    }

    protected String getString( int index )
    {
        Object value = getValue( index, DataPointValueType.STRING );
        if ( value != null && value instanceof String )
        {
            return (String) value;
        }
        return "";
    }

    protected XLinkDataPoint setByteArray( int index, byte[] value )
    {
        return setValue( index, DataPointValueType.BYTE_ARRAY, value );
    }

    protected byte[] getByteArray( int index )
    {
        Object value = getValue( index, DataPointValueType.BYTE_ARRAY );
        if ( value != null && value instanceof byte[] )
        {
            return (byte[]) value;
        }
        return null;
    }
}
