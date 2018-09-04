package com.exoterra.exowifi.bean;

public class SensorLinkageArgs
{
    public static final int SENSOR_ARGS_MAX = 30;

    private int version;
    private int length;
    protected byte[] args;

    public SensorLinkageArgs()
    {
        args = new byte[SENSOR_ARGS_MAX];
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength( int length )
    {
        this.length = length;
    }

    public byte[] getArgs()
    {
        return args;
    }

    public void setArgs( byte[] args )
    {
        this.args = args;
    }

    public byte[] toArray()
    {
        byte[] array = new byte[length+2];
        array[0] = (byte) version;
        array[1] = (byte) length;
        System.arraycopy( args, 0, array, 2, length );
        return array;
    }
}
