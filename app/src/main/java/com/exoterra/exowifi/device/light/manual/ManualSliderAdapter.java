package com.exoterra.exowifi.device.light.manual;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.device.LightUtil;

import java.text.DecimalFormat;

/**
 * Created by liruya on 2018/4/24.
 */

public class ManualSliderAdapter extends RecyclerView.Adapter<ManualSliderAdapter.ManualSliderViewHolder>
{
    private final int INTERVAL = 4;
    private Context mContext;
    private ExoLightStrip mLight;
    private long tmr;
    private ISeekbarListener mListener;

    public ManualSliderAdapter( Context context, ExoLightStrip light )
    {
        mContext = context;
        mLight = light;
    }

    public void setListener( ISeekbarListener listener )
    {
        mListener = listener;
    }

    @Override
    public ManualSliderViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
        ManualSliderViewHolder holder = new ManualSliderViewHolder( LayoutInflater.from( mContext )
                                                                                  .inflate( R.layout.item_slider, parent, false ) );
        return holder;
    }

    @Override
    public void onBindViewHolder( final ManualSliderViewHolder holder, int position )
    {
        if ( position >= 0 && position < mLight.getChannelCount() )
        {
            int value = mLight.getBright( position );
            String chnName = mLight.getChannelName( position );
            value = value > 1000 ? 1000 : value;
            value = value < 0 ? 0 : value;
            holder.iv_color.setImageResource( LightUtil.getIconRes( chnName ) );
            Drawable progressDrawable = mContext.getResources()
                                                .getDrawable( LightUtil.getProgressRes( chnName ) );
            holder.sb_progress.setProgressDrawable( progressDrawable );
            Drawable thumbDrawable = mContext.getResources()
                                                .getDrawable( LightUtil.getThumbRes( chnName ) );
            holder.sb_progress.setThumb( thumbDrawable );
            holder.sb_progress.setProgress( value );
            DecimalFormat df = new DecimalFormat( "##0" );
            holder.tv_percent.setText( df.format( value/10 ) + "%" );
            holder.sb_progress.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged( SeekBar seekBar, int i, boolean b )
                {
                }

                @Override
                public void onStartTrackingTouch( SeekBar seekBar )
                {
                }

                @Override
                public void onStopTrackingTouch( final SeekBar seekBar )
                {
                    if ( mListener != null )
                    {
                        mListener.setBright( holder.getAdapterPosition(), seekBar.getProgress() );
                    }
                }
            } );
        }

    }

    @Override
    public int getItemCount()
    {
        return mLight.getChannelCount();
    }

    public byte[] getBrights()
    {
        byte[] brights = new byte[mLight.getChannelCount()];
        for ( int i = 0; i < mLight.getChannelCount(); i++ )
        {
            brights[i] = (byte) ( mLight.getBright( i ) / 10);
        }
        return brights;
    }

    class ManualSliderViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iv_color;
        private SeekBar sb_progress;
        private TextView tv_percent;

        public ManualSliderViewHolder( View itemView )
        {
            super( itemView );
            iv_color = itemView.findViewById( R.id.item_slider_color );
            sb_progress = itemView.findViewById( R.id.item_slider_progress );
            tv_percent = itemView.findViewById( R.id.item_slider_percent );
        }
    }
}
