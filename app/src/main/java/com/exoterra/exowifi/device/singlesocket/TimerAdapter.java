package com.exoterra.exowifi.device.singlesocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.SocketTimer;

import java.text.DecimalFormat;
import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder>
{
    private final String TAG = "TimerAdapter";

    private Context mContext;
    private List<SocketTimer> mTimers;
    private RecyclerItemListener mListener;

    public TimerAdapter( Context context, List< SocketTimer > timers )
    {
        mContext = context;
        mTimers = timers;
    }

    public void setListener( RecyclerItemListener listener )
    {
        mListener = listener;
    }

    @Override
    public TimerViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
        TimerViewHolder holder = new TimerViewHolder( LayoutInflater.from( mContext )
                                                                    .inflate( R.layout.item_timer, parent, false ) );
        return holder;
    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public void onBindViewHolder( final TimerViewHolder holder, int position )
    {
        SocketTimer timer = mTimers.get( holder.getAdapterPosition() );
        DecimalFormat df = new DecimalFormat( "00" );
        holder.cib_action.setChecked( timer.getAction() );
        holder.tv_tmr.setText( df.format( timer.getTimer()/60 ) + ":" + df.format( timer.getTimer()%60 ) );
        StringBuffer sb = new StringBuffer();
        String[] wk = new String[]{ mContext.getString( R.string.week_sun ),
                                    mContext.getString( R.string.week_mon ),
                                    mContext.getString( R.string.week_tue ),
                                    mContext.getString( R.string.week_wed ),
                                    mContext.getString( R.string.week_thu ),
                                    mContext.getString( R.string.week_fri ),
                                    mContext.getString( R.string.week_sat ) };
        for ( int i = 0; i < 7; i++ )
        {
            if ( timer.getWeek()[i] )
            {
                sb.append( wk[i] ).append( "  " );
            }
        }
        if ( TextUtils.isEmpty( sb ) )
        {
            holder.tv_week.setText( mContext.getString( R.string.one_time ) );
        }
        else
        {
            holder.tv_week.setText( sb );
        }
        holder.sw_enable.setChecked( timer.isEnable() );
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                if ( mListener != null )
                {
                    mListener.onItemClick( holder.getAdapterPosition() );
                }
            }
        } );
        holder.itemView.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick( View v )
            {
                if ( mListener != null )
                {
                    mListener.onItemLongClick( holder.getAdapterPosition() );
                }
                return true;
            }
        } );
    }

    @Override
    public int getItemCount()
    {
        return mTimers == null ? 0 : mTimers.size();
    }

    class TimerViewHolder extends RecyclerView.ViewHolder
    {
        private CheckableImageButton cib_action;
        private TextView tv_tmr;
        private TextView tv_week;
        private Switch sw_enable;

        public TimerViewHolder( View itemView )
        {
            super( itemView );
            cib_action = itemView.findViewById( R.id.item_timer_action );
            tv_tmr = itemView.findViewById( R.id.item_timer_tmr );
            tv_week = itemView.findViewById( R.id.item_timer_week );
            sw_enable = itemView.findViewById( R.id.item_timer_enable );
        }
    }
}
