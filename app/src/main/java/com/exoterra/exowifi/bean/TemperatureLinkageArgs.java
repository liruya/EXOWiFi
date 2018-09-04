package com.exoterra.exowifi.bean;

import java.text.DecimalFormat;

public class TemperatureLinkageArgs extends SensorLinkageArgs
{
    public TemperatureLinkageArgs(SensorLinkageArgs args)
    {
        setVersion( args.getVersion() );
        setLength( args.getLength() );
        setArgs( args.getArgs() );
    }

    @Override
    public int getLength()
    {
        setLength( 7 );
        return super.getLength();
    }

    public int getThreshold()
    {
        return args[0];
    }

    public void setThreshold( int threshold )
    {
        args[0] = (byte) threshold;
    }

    public boolean isNightModeEnabled()
    {
        return (args[1] == 0 ? false : true);
    }

    public void setNightModeEnabled( boolean enable )
    {
        args[1] = (byte) ( enable ? 1 : 0);
    }

    public int getNightStart()
    {
        return ((args[3]&0xFF)<<8)|(args[2]&0xFF);
    }

    public void setNightStart( int night_start )
    {
        args[2] = (byte) ( night_start & 0xFF);
        args[3] = (byte) ( night_start >> 8);
    }

    public int getNightEnd()
    {
        return ((args[5]&0xFF)<<8)|(args[4]&0xFF);
    }

    public void setNightEnd( int night_end )
    {
        args[4] = (byte) ( night_end & 0xFF);
        args[5] = (byte) ( night_end >> 8);
    }

    public int getNightThreshold()
    {
        return args[6];
    }

    public void setNightThreshold( int night_threshold )
    {
        args[6] = (byte) night_threshold;
    }

    @Override
    public String toString()
    {
        DecimalFormat df = new DecimalFormat( "00" );
        StringBuffer sb = new StringBuffer( "Thermostat linkage: version-" );
        sb.append( getVersion() )
          .append( " length-" )
          .append( getLength() )
          .append( " threshold-" )
          .append( getThreshold() )
          .append( " night_mode_enable-" )
          .append( isNightModeEnabled() )
          .append( " night_start-" )
          .append( df.format( getNightStart()/60 ) )
          .append( ':' )
          .append( getNightStart()%60 )
          .append( " night_end-" )
          .append( df.format( getNightEnd()/60 ) )
          .append( ':' )
          .append( getNightEnd()%60 )
          .append( " night_threshold-" )
          .append( getNightThreshold() )
          .append( '\n' );
        return new String( sb );
    }
}
