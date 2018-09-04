package com.exoterra.exowifi.device.light.auto;

import com.exoterra.comman.BasePresenter;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.manager.XlinkListenerManager;

import java.util.List;

import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;

/**
 * Created by liruya on 2018/4/26.
 */

public class AutoPresenter extends BasePresenter<IAutoView>
{
    private final String TAG = "AutoPresenter";
    private final int PREVIEW_INTERVAL = 50;
    private final int LOCAL_PORT = 9587;
    private final int REMOTE_PORT = 8266;

    private ExoLightStrip mLight;
    private XLinkDataListener mDataListener;

    public AutoPresenter( IAutoView iAutoView, ExoLightStrip light )
    {
        super( iAutoView );
        mLight = light;
        mDataListener = new XLinkDataListener() {
            @Override
            public void onDataPointUpdate( XDevice xDevice, List< XLinkDataPoint > list )
            {
                if ( mLight.getXDevice().getMacAddress().equals( xDevice.getMacAddress() ) )
                {
                    for ( XLinkDataPoint dp : list )
                    {
                        mLight.setDataPoint( dp );
                    }
                    if ( isViewExist() )
                    {
                        getView().runOnUIThread( new Runnable() {
                            @Override
                            public void run()
                            {
                                getView().showDataChanged();
                            }
                        } );
                    }
                }
            }
        };
    }

    public void addDataListener()
    {
        XlinkListenerManager.getInstance().addXlinkDataListener( mDataListener );
    }

    public void removeDataListener()
    {
        XlinkListenerManager.getInstance().removeXlinkDataListener( mDataListener );
    }

    public void setSunrise( int start, int end )
    {
        mLight.setLightSunrise( start, end );
    }

    public void setDayBright( byte[] brights )
    {
        mLight.setLightDayBright( brights );
    }

    public void setSunset( int start, int end )
    {
        mLight.setLightSunset( start, end );
    }

    public void setNightBright( byte[] brights )
    {
        mLight.setLightNightBright( brights );
    }

    public void setTurnoff( boolean enable, int time )
    {
        mLight.setLightTurnoff( enable, time );
    }

    /**
     * get auto mode brights @ time
     * @param time [0,1439]
     * @return
     */
    private int[] getBrights( final int time )
    {
        if ( time < 0 || time > 1439 )
        {
            return null;
        }
        int[] result = new int[mLight.getChannelCount()];
        final int count = mLight.isTurnoffEnabled() ? 6 : 4;
        int[] tms = new int[count];
        int[][] vals = new int[count][mLight.getChannelCount()];
        tms[0] = mLight.getSunriseStart();
        tms[1] = mLight.getSunriseEnd();
        tms[2] = mLight.getSunsetStart();
        tms[3] = mLight.getSunsetEnd();
        if ( count == 6 )
        {
            tms[4] = mLight.getTurnoffTime();
            tms[5] = mLight.getTurnoffTime();
            for ( int i = 0; i < mLight.getChannelCount(); i++ )
            {
                vals[0][i] = 0;
                vals[1][i] = mLight.getDayBright()[i];
                vals[2][i] = mLight.getDayBright()[i];
                vals[3][i] = mLight.getNightBright()[i];
                vals[4][i] = mLight.getNightBright()[i];
                vals[5][i] = 0;
            }
        }
        else
        {
            for ( int i = 0; i < mLight.getChannelCount(); i++ )
            {
                vals[0][i] = mLight.getNightBright()[i];
                vals[1][i] = mLight.getDayBright()[i];
                vals[2][i] = mLight.getDayBright()[i];
                vals[3][i] = mLight.getNightBright()[i];
            }
        }
        for ( int i = 0; i < count; i++ )
        {
            final int j = (i+1)%count;
            int ts = tms[i];
            int te = tms[j];
            int dt;
            int duration = te - ts;
            if ( duration == 0 )
            {
                continue;
            }
            else if ( duration < 0 )
            {
                duration += 1440;
                if ( time >= ts )
                {
                    dt = time - ts;
                }
                else if ( time < te )
                {
                    dt = 1440 - ts + time;
                }
                else
                {
                    continue;
                }
            }
            else
            {
                if ( time >= ts && time < te )
                {
                    dt = time - ts;
                }
                else
                {
                    continue;
                }
            }
            for ( int k = 0; k < mLight.getChannelCount(); k++ )
            {
                final int dbrt = vals[j][k] - vals[i][k];
                result[k] = vals[i][k] * 10 + dbrt * dt * 10 / duration;
            }
        }
        return result;
    }

    public synchronized void preview()
    {
//        mLight.startPreview();
//        if ( WifiUtil.isWiFiConnected( getContext() ) )
//        {
//            final int[] previewCount = new int[]{ 0 };
//            byte[] ip = mLight.getXDevice().getInetAddress().getAddress();
//            LogUtil.e( TAG, "preview: " + mLight.getXDevice().getDeviceIp() + "  " + ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3] );
//            mUdpClient = new UdpClient( WifiUtil.getLocalIp( getContext() ), LOCAL_PORT, mLight.getXDevice().getDeviceIp(), REMOTE_PORT );
//            mUdpClient.start();
//            mPreviewTimer = new Timer();
//            mPreviewTask = new TimerTask() {
//                @Override
//                public void run()
//                {
//                    previewCount[0]++;
//                    if ( previewCount[0] > 1439 )
//                    {
//                        stopPreview();
//                        return;
//                    }
//                    if ( isViewExist() )
//                    {
//                        getView().runOnUIThread( new TimerTask() {
//                            @Override
//                            public void run()
//                            {
//                                getView().showPreviewUpdate( previewCount[0] );
//                            }
//                        } );
//                    }
//                    int[] brights = getBrights( previewCount[0] );
//                    LogUtil.e( TAG, "run: " + brights[0] + " " + brights[1] + " " + brights[2] + " " + brights[3] + " " + brights[4] );
//                    byte[] value = new byte[mLight.getChannelCount()*2+3];
//                    byte xor = 0;
//                    value[0] = 0x68;
//                    value[1] = 0x0B;
//                    xor ^= value[0];
//                    xor ^= value[1];
//                    for ( int i = 0; i < mLight.getChannelCount(); i++ )
//                    {
//                        value[2+i*2] = (byte) ( ( brights[i] & 0xFFFF) >> 8);
//                        value[3+i*2] = (byte) ( brights[i] & 0xFF );
//                        xor ^= value[2+i*2];
//                        xor ^= value[3+i*2];
//                    }
//                    value[value.length-1] = xor;
//                    mUdpClient.send( value );
//                }
//            };
//            mPreviewTimer.schedule( mPreviewTask, 0, PREVIEW_INTERVAL );
//        }
    }

    public synchronized void setPreview( boolean flag )
    {
        mLight.setLightPreview( flag );
//        if ( mPreviewTimer != null && mPreviewTask != null )
//        {
//            mPreviewTask.cancel();
//            mPreviewTimer.cancel();
//            mPreviewTimer = null;
//            mPreviewTask = null;
//            byte[] value = new byte[]{ 0x68, 0x0C, 0x64 };
//            mUdpClient.send( value );
//            if ( isViewExist() )
//            {
//                getView().runOnUIThread( new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        getView().showPreviewStopped();
//                    }
//                } );
//            }
//        }
//        if ( mUdpClient != null )
//        {
//            mUdpClient.stop();
//        }
    }
}
