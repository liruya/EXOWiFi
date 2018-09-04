package com.exoterra.exowifi.device.singlesocket;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Sensor;
import com.exoterra.exowifi.bean.SingleSocket;
import com.exoterra.exowifi.bean.TemperatureLinkageArgs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final String TAG = "SensorAdapter";

    private Context mContext;
    private SingleSocket mSocket;
    private List<Sensor> mSensors;

    public SensorAdapter( Context context, SingleSocket socket )
    {
        mContext = context;
        mSocket = socket;
        refreshData();
    }

    public void refreshData()
    {
        if ( mSensors == null )
        {
            mSensors = new ArrayList<>();
        }
        mSensors.clear();
        if ( mSocket != null )
        {
            if ( mSocket.getSensor1Available() )
            {
                mSensors.add( mSocket.getSensor1() );
            }
            if ( mSocket.getSensor2Available() )
            {
                mSensors.add( mSocket.getSensor2() );
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
        RecyclerView.ViewHolder holder = null;
        switch ( viewType )
        {
            case Sensor.SENSOR_NONE:
                holder = new NosensorViewHolder( LayoutInflater.from( mContext )
                                                               .inflate( R.layout.item_no_sensor, parent, false ) );
                break;
            case Sensor.SENSOR_TEMPERATURE:
                holder = new TemperatureViewHolder( LayoutInflater.from( mContext )
                                                                  .inflate( R.layout.item_sensor_thermostat, parent, false ));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, final int position )
    {
        if ( holder == null )
        {
            return;
        }
        switch ( holder.getItemViewType() )
        {
            case Sensor.SENSOR_NONE:
                break;
            case Sensor.SENSOR_TEMPERATURE:
                TemperatureViewHolder thermostatHolder = (TemperatureViewHolder) holder;
                final Sensor sensor = mSensors.get( position );
                final TemperatureLinkageArgs args = new TemperatureLinkageArgs( sensor.getLinkageArgs() );
                thermostatHolder.tv_temp.setText( sensor.getValue()/10 + "." + sensor.getValue()%10 + " ℃" );
                if ( sensor.isLinkageEnable() )
                {
                    thermostatHolder.tv_day_thrd.setText( args.getThreshold() + " ℃" );
                    thermostatHolder.tv_night_thrd.setText( args.getNightThreshold() + " ℃" );
                    if ( args.isNightModeEnabled() )
                    {
                        thermostatHolder.tv_day_thrd.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_sun_white_24dp, 0, 0, 0 );
                        thermostatHolder.tv_night_thrd.setVisibility( View.VISIBLE );
                    }
                    else
                    {
                        thermostatHolder.tv_day_thrd.setCompoundDrawables( null, null, null, null );
                        thermostatHolder.tv_night_thrd.setVisibility( View.GONE );
                    }
                }
                else
                {
                    thermostatHolder.tv_day_thrd.setText( R.string.linkage_disabled );
                    thermostatHolder.tv_day_thrd.setCompoundDrawablesWithIntrinsicBounds( null, null, null, null );
                    thermostatHolder.tv_night_thrd.setVisibility( View.GONE );
                }
                thermostatHolder.tv_set.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v )
                    {
                        showTempDialog(position, args, sensor.isNotifyEnable(), sensor.isLinkageEnable());
                    }
                } );
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return (mSensors.size() == 0) ? 1 : mSensors.size();
    }

    @Override
    public int getItemViewType( int position )
    {
        if ( mSensors.size() == 0 )
        {
            return Sensor.SENSOR_NONE;
        }
        return mSensors.get( position ).getType();
    }

    private boolean checkTextValidDigital(String text, int min, int max)
    {
        if ( TextUtils.isEmpty( text ) )
        {
            return false;
        }
        int value = Integer.parseInt( text );
        if ( value < min || value > max )
        {
            return false;
        }
        return true;
    }

    private void showTempDialog( final int idx, final TemperatureLinkageArgs args, final boolean notify, boolean linkage )
    {
        if ( mSocket == null || idx < 0 || idx > 1 || args == null )
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
        final AlertDialog dialog = builder.create();
        dialog.setTitle( mContext.getString( R.string.set_linkage_args ) );
        dialog.setCanceledOnTouchOutside( false );
        View view = LayoutInflater.from( mContext ).inflate( R.layout.dialog_sensor_args, null );
        final EditText day_thrd = view.findViewById( R.id.dialog_temp_thrd );
        final Switch night_enable = view.findViewById( R.id.dialog_temp_night_enable );
        final EditText night_start_hour = view.findViewById( R.id.dialog_temp_start_hour );
        final EditText night_start_minute = view.findViewById( R.id.dialog_temp_start_minute );
        final EditText night_end_hour = view.findViewById( R.id.dialog_temp_end_hour );
        final EditText night_end_minute = view.findViewById( R.id.dialog_temp_end_minute );
        final EditText night_thrd = view.findViewById( R.id.dialog_temp_night_thrd );
        final Switch sw_notify = view.findViewById( R.id.dialog_temp_notify );
        final Switch sw_linkage = view.findViewById( R.id.dialog_temp_linkage_enable );
        Button btn_cancel = view.findViewById( R.id.dialog_temp_cancel );
        Button btn_ok = view.findViewById( R.id.dialog_temp_ok );
        DecimalFormat df = new DecimalFormat( "00" );
        day_thrd.setText( "" + args.getThreshold() );
        night_enable.setChecked( args.isNightModeEnabled() );
        night_start_hour.setText( df.format( args.getNightStart()/60 ) );
        night_start_minute.setText( df.format( args.getNightStart()%60 )  );
        night_end_hour.setText( df.format( args.getNightEnd()/60 ) );
        night_end_minute.setText( df.format( args.getNightEnd()%60 ) );
        night_thrd.setText( "" + args.getNightThreshold() );
        sw_notify.setChecked( notify );
        sw_linkage.setChecked( linkage );
        night_start_hour.setEnabled( night_enable.isChecked() );
        night_start_minute.setEnabled( night_enable.isChecked() );
        night_end_hour.setEnabled( night_enable.isChecked() );
        night_end_minute.setEnabled( night_enable.isChecked() );
        night_thrd.setEnabled( night_enable.isChecked() );
        day_thrd.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after )
            {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count )
            {
                day_thrd.setError( null );
            }

            @Override
            public void afterTextChanged( Editable s )
            {

            }
        } );
        night_thrd.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after )
            {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count )
            {
                night_thrd.setError( null );
            }

            @Override
            public void afterTextChanged( Editable s )
            {

            }
        } );
        night_start_hour.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after )
            {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count )
            {
                night_start_hour.setError( null );
            }

            @Override
            public void afterTextChanged( Editable s )
            {

            }
        } );
        night_start_minute.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after )
            {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count )
            {
                night_start_minute.setError( null );
            }

            @Override
            public void afterTextChanged( Editable s )
            {

            }
        } );
        night_end_hour.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after )
            {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count )
            {
                night_end_hour.setError( null );
            }

            @Override
            public void afterTextChanged( Editable s )
            {

            }
        } );
        night_end_minute.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after )
            {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count )
            {
                night_end_minute.setError( null );
            }

            @Override
            public void afterTextChanged( Editable s )
            {

            }
        } );
        night_enable.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked )
            {
                night_start_hour.setEnabled( isChecked );
                night_start_minute.setEnabled( isChecked );
                night_end_hour.setEnabled( isChecked );
                night_end_minute.setEnabled( isChecked );
                night_thrd.setEnabled( isChecked );
            }
        } );
        btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                dialog.dismiss();
            }
        } );
        btn_ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                String day_thrd_text = day_thrd.getText().toString();
                if ( !checkTextValidDigital(day_thrd_text, 15, 35) )
                {
                    day_thrd.setError( "15 - 35" );
                    return;
                }
                String night_thrd_text = night_thrd.getText().toString();
                if ( !checkTextValidDigital(night_thrd_text, 15, 35) )
                {
                    night_thrd.setError( "15 - 35" );
                }
                String night_start_hour_text = night_start_hour.getText().toString();
                String night_start_minute_text = night_start_minute.getText().toString();
                String night_end_hour_text = night_end_hour.getText().toString();
                String night_end_minute_text = night_end_minute.getText().toString();
                if ( !checkTextValidDigital(night_start_hour_text, 0, 23) )
                {
                    night_start_hour.setError( "0 - 23" );
                    return;
                }
                if ( !checkTextValidDigital(night_start_minute_text, 0, 59) )
                {
                    night_start_minute.setError( "0 - 59" );
                    return;
                }
                if ( !checkTextValidDigital(night_end_hour_text, 0, 23) )
                {
                    night_end_hour.setError( "0 - 23" );
                    return;
                }
                if ( !checkTextValidDigital(night_end_minute_text, 0, 59) )
                {
                    night_end_minute.setError( "0 - 59" );
                    return;
                }
                args.setThreshold( Integer.parseInt( day_thrd_text ) );
                args.setNightModeEnabled( night_enable.isChecked() );
                args.setNightStart( Integer.parseInt( night_start_hour_text ) * 60 + Integer.parseInt( night_start_minute_text ) );
                args.setNightEnd( Integer.parseInt( night_end_hour_text ) * 60 + Integer.parseInt( night_end_minute_text ) );
                args.setNightThreshold( Integer.parseInt( night_thrd_text ) );
                if ( idx == 0 )
                {
                    mSocket.setSocketSensor1Linkage( sw_notify.isChecked(), sw_linkage.isChecked(), args );
                }
                else if ( idx == 1 )
                {
                    mSocket.setSocketSensor2Linkage( sw_notify.isChecked(), sw_linkage.isChecked(), args );
                }
                dialog.dismiss();
            }
        } );
        dialog.setView( view );
        dialog.show();
    }

    private class NosensorViewHolder extends RecyclerView.ViewHolder
    {
        public NosensorViewHolder( View itemView )
        {
            super( itemView );
        }
    }

    private class TemperatureViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_temp;
        private TextView tv_set;
        private TextView tv_day_thrd;
        private TextView tv_night_thrd;

        public TemperatureViewHolder( View itemView )
        {
            super( itemView );
            tv_temp = itemView.findViewById( R.id.item_thermostat_temp );
            tv_set = itemView.findViewById( R.id.item_thermostat_set );
            tv_day_thrd = itemView.findViewById( R.id.item_thermostat_day );
            tv_night_thrd = itemView.findViewById( R.id.item_thermostat_night );
        }
    }
}
