package com.exoterra.exowifi.bean;

public class SocketTimer
{
    private static final byte TIMER_AVAILABLE_FLAG = 0x55;
    private int mTimer;
    private boolean mAction;
    private boolean[] mWeek;
    private boolean mEnable;


    public SocketTimer()
    {
        mWeek = new boolean[7];
    }

    public int getTimer()
    {
        return mTimer;
    }

    public void setTimer( int timer )
    {
        if ( timer < 0 || timer > 1439 )
        {
            return;
        }
        mTimer = timer;
    }

    public boolean getAction()
    {
        return mAction;
    }

    public void setAction( boolean action )
    {
        mAction = action;
    }

    public boolean[] getWeek()
    {
        return mWeek;
    }

    public void setWeek( boolean[] week )
    {
        mWeek = week;
    }

    public void setWeek( int idx, boolean enable )
    {
        if ( idx >= 0 && idx < 7 )
        {
            mWeek[idx] = enable;
        }
    }

    public boolean isEnable()
    {
        return mEnable;
    }

    public void setEnable( boolean enable )
    {
        mEnable = enable;
    }

    public byte[] toArray()
    {
        byte[] array = new byte[]{0,0,0,TIMER_AVAILABLE_FLAG};
        array[0] = (byte) ( mTimer & 0xFF);
        array[1] = (byte) ( mTimer >> 8);
        if ( mAction )
        {
            array[1] |= 0x80;
        }
        for ( int i = 0; i < 7; i++ )
        {
            if ( mWeek[i] )
            {
                array[2] |= (1<<i);
            }
        }
        if ( mEnable )
        {
            array[2] |= 0x80;
        }
        return array;
    }

    public static class Builder
    {
        public SocketTimer creat( byte b0, byte b1, byte b2, byte b3 )
        {
            if ( b3 != TIMER_AVAILABLE_FLAG )
            {
                return null;
            }
            int tmr = ((b1&0x7F)<<8)|(b0&0xFF);
            if ( tmr > 1439 )
            {
                return null;
            }
            SocketTimer timer = new SocketTimer();
            timer.mTimer = tmr;
            timer.mAction = (b1&0x80) == 0 ? false : true;
            for ( int i = 0; i < 7; i++ )
            {
                timer.mWeek[i] = (b2&(1<<i)) == 0 ? false : true;
            }
            timer.mEnable = (b2&0x80) == 0 ? false : true;
            return timer;
        }
    }
}
