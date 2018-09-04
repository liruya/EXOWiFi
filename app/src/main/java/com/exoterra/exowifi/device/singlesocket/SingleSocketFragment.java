package com.exoterra.exowifi.device.singlesocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;

import com.exoterra.comman.BaseFragment;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.bean.SingleSocket;
import com.exoterra.exowifi.bean.SocketTimer;
import com.exoterra.exowifi.manager.DeviceManager;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.sdk.v5.module.main.XLinkSDK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSocketFragment extends BaseFragment implements ISingleSocketView
{
    private RecyclerView single_socket_rv_sensor;
    private RecyclerView single_socket_rv_timer;
    private CheckableImageButton single_socket_power;
    private ImageButton single_socket_add_timer;

    private SingleSocket mSocket;
    private List<SocketTimer> mTimers;
    private SingleSocketPresenter mPresenter;
    private SensorAdapter mSensorAdapter;
    private TimerAdapter mTimerAdapter;

    public static SingleSocketFragment newInstance( String mac )
    {
        SingleSocketFragment frag = new SingleSocketFragment();
        Bundle b = new Bundle();
        b.putString( Constant.KEY_MAC_ADDRESS, mac );
        frag.setArguments( b );
        return frag;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_single_socket, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mPresenter.removeDeviceStateChangedListener();
        mPresenter.removeDataChangedListener();
    }

    @Override
    protected void initView( View view )
    {
        single_socket_rv_sensor = view.findViewById( R.id.single_socket_rv_sensor );
        single_socket_rv_timer = view.findViewById( R.id.single_socket_rv_timer );
        single_socket_power = view.findViewById( R.id.single_socket_power );
        single_socket_add_timer = view.findViewById( R.id.single_socket_add_timer );

        single_socket_rv_sensor.setLayoutManager( new LinearLayoutManager( getContext(),
                                                                           LinearLayoutManager.VERTICAL,
                                                                           false ) );
        single_socket_rv_sensor.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ) );
        single_socket_rv_timer.setLayoutManager( new LinearLayoutManager( getContext(),
                                                                           LinearLayoutManager.VERTICAL,
                                                                           false ) );
        single_socket_rv_timer.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ) );
    }

    @Override
    protected void initData()
    {
        XLinkSDK.start();
        Bundle b = getArguments();
        if ( b != null )
        {
            String mac = b.getString( Constant.KEY_MAC_ADDRESS );
            Device device = DeviceManager.getInstance().getDevice( mac );
            mSocket = new SingleSocket( device.getXDevice() );
            mSocket.setDataPointList( device.getDataPointList() );

            mPresenter = new SingleSocketPresenter( this, mSocket );
            mPresenter.addDeviceStateChangedListener();
            mPresenter.addDataChangedListener();
            mPresenter.getDeviceDataPoints();

            mSensorAdapter = new SensorAdapter( getContext(), mSocket );
            single_socket_rv_sensor.setAdapter( mSensorAdapter );

            mTimers = new ArrayList<>();
            mTimerAdapter = new TimerAdapter( getContext(), mTimers );
            mTimerAdapter.setListener( new RecyclerItemListener() {
                @Override
                public void onItemClick( int position )
                {
                    showTimerDialog( mTimers.get( position ) );
                }

                @Override
                public void onItemLongClick( int position )
                {
                    showRemoveTimerDialog( position );
                }
            } );
            single_socket_rv_timer.setAdapter( mTimerAdapter );

            if( (mSocket.getSensor1Available() && mSocket.getSensor1LinkageEnable()) ||
                (mSocket.getSensor1Available() && mSocket.getSensor2LinkageEnable()) )
            {
                single_socket_power.setEnabled( false );
            }
            else
            {
                single_socket_power.setEnabled( true );
            }
        }
    }

    @Override
    protected void initEvent()
    {
        single_socket_power.setOnClickListener( new View.OnClickListener() {
            @SuppressLint ( "RestrictedApi" )
            @Override
            public void onClick( View v )
            {
                if ( mSocket != null )
                {
                    mSocket.setSocketPower( !single_socket_power.isChecked() );
                }
            }
        } );
        single_socket_add_timer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                if ( mTimers.size() < SingleSocket.TIMER_COUNT_MAX )
                {
                    showTimerDialog( null );
                }
                else
                {

                }
            }
        } );
    }

    @Override
    public Context getMvpContext()
    {
        return getContext();
    }

    @Override
    public void toast( String msg )
    {

    }

    @Override
    public void runOnUIThread( Runnable runnable )
    {
        if ( getActivity() != null )
        {
            getActivity().runOnUiThread( runnable );
        }
    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public void showDataChanged()
    {
        single_socket_power.setChecked( mSocket.getPower() );
        mSensorAdapter.refreshData();
        mSensorAdapter.notifyDataSetChanged();
        if ( mTimers == null )
        {
            mTimers = new ArrayList<>();
        }
        mTimers.clear();
        mTimers.addAll( mSocket.getTimer() );
        mTimerAdapter.notifyDataSetChanged();
        if( (mSocket.getSensor1Available() && mSocket.getSensor1LinkageEnable()) ||
            (mSocket.getSensor1Available() && mSocket.getSensor2LinkageEnable()) )
        {
            single_socket_power.setEnabled( false );
        }
        else
        {
            single_socket_power.setEnabled( true );
        }
    }

    @Override
    public void showTimerDialog( final SocketTimer timer )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setTitle( R.string.title_set_timer );
        View view = LayoutInflater.from( getContext() ).inflate( R.layout.dialog_timer, null );
        final CheckBox[] cb_week = new CheckBox[7];
        final TimePicker tp_tmr = view.findViewById( R.id.dialog_tp_timer );
        final RadioGroup rg_action = view.findViewById( R.id.dialog_rg_action );
        cb_week[0] = view.findViewById( R.id.dialog_ctv_sun );
        cb_week[1] = view.findViewById( R.id.dialog_ctv_mon );
        cb_week[2] = view.findViewById( R.id.dialog_ctv_tue );
        cb_week[3] = view.findViewById( R.id.dialog_ctv_wed );
        cb_week[4] = view.findViewById( R.id.dialog_ctv_thu );
        cb_week[5] = view.findViewById( R.id.dialog_ctv_fri );
        cb_week[6] = view.findViewById( R.id.dialog_ctv_sat );
        final Switch sw_enable = view.findViewById( R.id.dialog_sw_enable );
        tp_tmr.setIs24HourView( true );
        if ( timer != null )
        {
            tp_tmr.setCurrentHour( timer.getTimer()/60 );
            tp_tmr.setCurrentMinute( timer.getTimer()%60 );
            rg_action.check( timer.getAction() ? R.id.dialog_rb_turnon : R.id.dialog_rb_turnoff );
            for ( int i = 0; i < 7; i++ )
            {
                cb_week[i].setChecked( timer.getWeek()[i] );
            }
            sw_enable.setChecked( timer.isEnable() );
        }
        builder.setNegativeButton( R.string.cancel, null );
        builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                if ( timer != null )
                {
                    timer.setTimer( tp_tmr.getCurrentHour()*60 + tp_tmr.getCurrentMinute() );
                    timer.setAction( rg_action.getCheckedRadioButtonId() == R.id.dialog_rb_turnoff ? false : true );
                    for ( int i = 0; i < 7; i++ )
                    {
                        timer.setWeek( i, cb_week[i].isChecked() );
                    }
                    timer.setEnable( sw_enable.isChecked() );
                }
                else
                {
                    SocketTimer tmr = new SocketTimer();
                    tmr.setTimer( tp_tmr.getCurrentHour()*60 + tp_tmr.getCurrentMinute() );
                    tmr.setAction( rg_action.getCheckedRadioButtonId() == R.id.dialog_rb_turnoff ? false : true );
                    for ( int i = 0; i < 7; i++ )
                    {
                        tmr.setWeek( i, cb_week[i].isChecked() );
                    }
                    tmr.setEnable( sw_enable.isChecked() );
                    mTimers.add( tmr );
                }
                mSocket.setSocketTimer( mTimers );
            }
        } );
        builder.setView( view );
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside( false );
        dialog.show();
    }

    private void showRemoveTimerDialog( final int position )
    {
        if ( position >= 0 && position < SingleSocket.TIMER_COUNT_MAX )
        {
            AlertDialog dialog = new AlertDialog.Builder( getContext() ).create();
            dialog.setTitle( R.string.delete_timer );
            dialog.setButton( AlertDialog.BUTTON_NEGATIVE, getContext().getString( R.string.cancel ), new DialogInterface.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialog, int which )
                {

                }
            } );
            dialog.setButton( AlertDialog.BUTTON_POSITIVE, getContext().getString( R.string.ok ), new DialogInterface.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialog, int which )
                {
                    mTimers.remove( position );
                    mSocket.setSocketTimer( mTimers );
                }
            } );
            dialog.setCanceledOnTouchOutside( false );
            dialog.show();
        }
    }
}
