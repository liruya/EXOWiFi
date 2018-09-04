package com.exoterra.exowifi.device.light.auto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.exoterra.comman.BaseFragment;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.device.LightUtil;
import com.exoterra.exowifi.manager.DeviceManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoFragment extends BaseFragment implements View.OnClickListener, IAutoView
{
    private LineChart auto_line_chart;
    private TextView auto_tv_sunrise;
    private TextView auto_tv_sunset;
    private TextView auto_tv_turnoff;
    private ListView auto_lv_day;
    private ListView auto_lv_night;
    private ImageView auto_iv_sunrise;
    private Button auto_btn_prev;

    private ExoLightStrip mLight;
    private LineData mLineData;
    private List<ILineDataSet> mDataSets;

    private AutoPresenter mPresenter;

    private int mChannelCount;
    private int mSunriseStart;
    private int mSunriseEnd;
    private byte[] mDayBrights;
    private int mSunsetStart;
    private int mSunsetEnd;
    private byte[] mNightBrights;
    private boolean mTurnoffEnabled;
    private int mTurnoffTime;
    private boolean mPreviewFlag;
    private Timer mPreviewTimer;
    private TimerTask mPreviewTask;

    public static AutoFragment newInstance( String mac )
    {
        AutoFragment frag = new AutoFragment();
        Bundle bundle = new Bundle();
        bundle.putString( Constant.KEY_MAC_ADDRESS, mac );
        frag.setArguments( bundle );

        return frag;
    }

    public AutoFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_auto, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mPresenter.setPreview( false );
        mPresenter.removeDataListener();
    }

    @Override
    protected void initView( View view )
    {
        auto_line_chart = view.findViewById( R.id.auto_line_chart );
        auto_tv_sunrise = view.findViewById( R.id.auto_tv_sunrise );
        auto_tv_sunset = view.findViewById( R.id.auto_tv_sunset );
        auto_tv_turnoff = view.findViewById( R.id.auto_tv_turnoff );
        auto_lv_day = view.findViewById( R.id.auto_lv_day );
        auto_lv_night = view.findViewById( R.id.auto_lv_night );
        auto_iv_sunrise = view.findViewById( R.id.auto_iv_sunrise );
        auto_btn_prev = view.findViewById( R.id.auto_btn_prev );

        XAxis xAxis = auto_line_chart.getXAxis();
        YAxis axisLeft = auto_line_chart.getAxisLeft();
        YAxis axisRight = auto_line_chart.getAxisRight();
        xAxis.setGranularity( 1 );
        xAxis.setGranularityEnabled( true );
        xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
        xAxis.setDrawGridLines( false );
        xAxis.setDrawAxisLine( false );
        xAxis.setTextColor( Color.WHITE );
        xAxis.setEnabled( true );
        xAxis.setAxisMaximum( 24 * 60 );
        xAxis.setAxisMinimum( 0 );
        xAxis.setLabelCount( 13, true );
        xAxis.setValueFormatter( new IAxisValueFormatter() {
            @Override
            public String getFormattedValue( float value, AxisBase axis )
            {
                return "" + (((int) value)%1440)/60;
            }
        } );
        axisLeft.setAxisMaximum( 100 );
        axisLeft.setAxisMinimum( 0 );
        axisLeft.setLabelCount( 5, true );
        axisLeft.setValueFormatter( new IAxisValueFormatter() {
            @Override
            public String getFormattedValue( float value, AxisBase axis )
            {
                DecimalFormat df = new DecimalFormat("##0");
                return df.format( value );
            }
        } );
        axisLeft.setPosition( YAxis.YAxisLabelPosition.OUTSIDE_CHART );
        axisLeft.setTextColor( Color.WHITE );
        axisLeft.setDrawGridLines( true );
        axisLeft.setGridColor( 0xFF9E9E9E );
        axisLeft.setGridLineWidth( 0.75f );
        axisLeft.setDrawAxisLine( false );
        axisLeft.setAxisLineColor( Color.WHITE );
        axisLeft.setGranularity( 1 );
        axisLeft.setGranularityEnabled( true );
        axisLeft.setSpaceTop( 0 );
        axisLeft.setSpaceBottom( 0 );
        axisLeft.setEnabled( true );
        axisRight.setEnabled( false );
        auto_line_chart.setTouchEnabled( false );
        auto_line_chart.setDragEnabled( false );
        auto_line_chart.setScaleEnabled( false );
        auto_line_chart.setPinchZoom( false );
        auto_line_chart.setDoubleTapToZoomEnabled( false );
        auto_line_chart.setBorderColor( Color.CYAN );
        auto_line_chart.setBorderWidth( 1 );
        auto_line_chart.setDrawBorders( false );
        auto_line_chart.setDrawGridBackground( true );
        auto_line_chart.setGridBackgroundColor( Color.TRANSPARENT );
        auto_line_chart.setDescription( null );
        auto_line_chart.setMaxVisibleValueCount( 0 );
        auto_line_chart.getLegend().setTextColor( Color.WHITE );
    }

    @Override
    protected void initData()
    {
        Bundle bundle = getArguments();
        if ( bundle != null )
        {
            String mac = bundle.getString( Constant.KEY_MAC_ADDRESS );
            Device device = DeviceManager.getInstance().getDevice( mac );
            mLight = new ExoLightStrip( device.getXDevice() );
            mLight.setDataPointList( device.getDataPointList() );
            mPresenter = new AutoPresenter( this, mLight );
            mPresenter.addDataListener();

            mChannelCount = mLight.getChannelCount();
            mSunriseStart = mLight.getSunriseStart();
            mSunriseEnd = mLight.getSunriseEnd();
            mDayBrights = Arrays.copyOf( mLight.getDayBright(), mLight.getDayBright().length );
            mSunsetStart = mLight.getSunsetStart();
            mSunsetEnd = mLight.getSunsetEnd();
            mNightBrights = Arrays.copyOf( mLight.getNightBright(), mLight.getNightBright().length );
            mTurnoffEnabled = mLight.isTurnoffEnabled();
            mTurnoffTime = mLight.getTurnoffTime();

            refreshData();
        }
    }

    @Override
    protected void initEvent()
    {
        auto_btn_prev.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                if ( mLight != null )
                {
                    mPresenter.setPreview( !mPreviewFlag );
                }
            }
        } );
        auto_tv_sunrise.setOnClickListener( this );
        auto_tv_sunset.setOnClickListener( this );
        auto_tv_turnoff.setOnClickListener( this );
        auto_lv_day.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l )
            {
                showEditDayNightDialog( false );
            }
        } );
        auto_lv_night.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l )
            {
                showEditDayNightDialog( true );
            }
        } );
    }

    private int[] sortAutoTime()
    {
        int[] time;
        if ( mLight.isTurnoffEnabled() )
        {
            time = new int[]{ mSunriseStart, mSunriseEnd, mSunsetStart, mSunsetEnd, mTurnoffTime, mTurnoffTime };
        }
        else
        {
            time = new int[]{ mSunriseStart, mSunriseEnd, mSunsetStart, mSunsetEnd };
        }
        int[] index = new int[time.length];
        for ( int i = 0; i < index.length; i++ )
        {
            index[i] = i;
        }
        for ( int i = time.length - 1; i > 0; i-- )
        {
            for ( int j = 0; j < i; j++ )
            {
                if ( time[index[j]] > time[index[j+1]] )
                {
                    int tmp = index[j];
                    index[j] = index[j+1];
                    index[j+1] = tmp;
                }
            }
        }
        return index;
    }

    private boolean checkAutoPara( int[] index )
    {
        for ( int i = 0; i < index.length; i++ )
        {
            if ( (index[i]+1)%index.length != index[(i+1)%index.length]%index.length )
            {
                return false;
            }
        }
        return true;
    }

    private void refreshData()
    {
        if ( mLight != null )
        {

            List< Map< String, Object > > day_list = new ArrayList<>();
            List< Map< String, Object > > night_list = new ArrayList<>();

            if ( mDataSets == null )
            {
                mDataSets = new ArrayList<>();
            }
            mDataSets.clear();
            int[] time;
            int[][] brights;
            if ( mLight.isTurnoffEnabled() )
            {
                time = new int[]{ mSunriseStart, mSunriseEnd, mSunsetStart, mSunsetEnd, mTurnoffTime, mTurnoffTime };
                brights = new int[6][mLight.getChannelCount()];
                for ( int i = 0; i < mLight.getChannelCount(); i++ )
                {
                    brights[0][i] = 0;
                    brights[1][i] = mDayBrights[i];
                    brights[2][i] = mDayBrights[i];
                    brights[3][i] = mNightBrights[i];
                    brights[4][i] = mNightBrights[i];
                    brights[5][i] = 0;
                }
            }
            else
            {
                time = new int[]{ mSunriseStart, mSunriseEnd, mSunsetStart, mSunsetEnd };
                brights = new int[4][mLight.getChannelCount()];
                for ( int i = 0; i < mLight.getChannelCount(); i++ )
                {
                    brights[0][i] = mNightBrights[i];
                    brights[1][i] = mDayBrights[i];
                    brights[2][i] = mDayBrights[i];
                    brights[3][i] = mNightBrights[i];
                }
            }
            int[] index = sortAutoTime();
            boolean b = checkAutoPara( index );
            for ( int i = 0; i < mChannelCount; i++ )
            {
                List<Entry> entries = new ArrayList<>();
                if ( b )
                {
                    int ts = time[index[0]];
                    int te = time[index[index.length-1]];
                    int bs = brights[index[0]][i];
                    int be = brights[index[index.length-1]][i];
                    int duration = 1440 - te + ts;
                    int dbrt = bs - be;
                    float b0 = be + dbrt * ( 1440 - te) / (float) duration;
                    entries.add( new Entry( 0, b0 ) );
                    int idx;
                    for ( int j = 0; j < index.length; j++ )
                    {
                        idx = index[j];
                        entries.add( new Entry( time[idx], brights[idx][i] ) );
                    }
                    entries.add( new Entry( 1440, b0 ) );
                }
//                if ( mTurnoffEnabled )
//                {
//                    entries.add( new Entry( 0, 0 ) );
//                    entries.add( new Entry( mSunriseStart, 0 ) );
//                    entries.add( new Entry( mSunriseEnd, mDayBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mSunsetStart, mDayBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mSunsetEnd, mNightBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mTurnoffTime, mNightBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mTurnoffTime, 0 ) );
//                    entries.add( new Entry( 1440, 0 ) );
//                }
//                else
//                {
//                    entries.add( new Entry( 0, mNightBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mSunriseStart, mNightBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mSunriseEnd, mDayBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mSunsetStart, mDayBrights[i] & 0xFF ) );
//                    entries.add( new Entry( mSunsetEnd, mNightBrights[i] & 0xFF ) );
//                    entries.add( new Entry( 1440, mNightBrights[i] & 0xFF ) );
//                }
                String color = mLight.getChannelName( i );
                if ( color.endsWith( "\0" ) )
                {
                    color = color.substring( 0, color.length() - 1 );
                }
                LineDataSet lineDataSet = new LineDataSet( entries, color );
                lineDataSet.setColor( LightUtil.getColorValue( color ) );
                lineDataSet.setCircleRadius( 3.0f );
                lineDataSet.setCircleColor( LightUtil.getColorValue( color ) );
                lineDataSet.setDrawCircleHole( false );
                lineDataSet.setLineWidth( 2.0f );
                mDataSets.add( lineDataSet );

                Map< String, Object > day_map = new HashMap<>();
                Map< String, Object > night_map = new HashMap<>();
                day_map.put( "icon", LightUtil.getIconRes( color ) );
                day_map.put( "value", "" + ( mDayBrights[i] & 0xFF ) + "%" );
                night_map.put( "icon", LightUtil.getIconRes( color ) );
                night_map.put( "value", "" + ( mNightBrights[i] & 0xFF ) + "%" );
                day_list.add( day_map );
                night_list.add( night_map );
            }
            mLineData = new LineData( mDataSets );
            auto_line_chart.setData( mLineData );
            auto_line_chart.invalidate();

            DecimalFormat df = new DecimalFormat( "00" );
            auto_tv_sunrise.setText( df.format( mSunriseStart/60 ) + ":" + df.format( mSunriseStart%60 ) + "\n|\n" +
                                     df.format( mSunriseEnd/60 ) + ":" + df.format( mSunriseEnd%60 ) );
            auto_tv_sunset.setText( df.format( mSunsetStart/60 ) + ":" + df.format( mSunsetStart%60 ) + "\n|\n" +
                                     df.format( mSunsetEnd/60 ) + ":" + df.format( mSunsetEnd%60 ) );

            String[] keys = new String[]{ "icon", "value" };
            int[] res = new int[]{ R.id.item_color_bright_icon, R.id.item_color_bright_text };
            SimpleAdapter dayAdapter = new SimpleAdapter( getContext(),
                                                          day_list,
                                                          R.layout.item_color_bright,
                                                          keys,
                                                          res );
            SimpleAdapter nightAdapter = new SimpleAdapter( getContext(),
                                                          night_list,
                                                          R.layout.item_color_bright,
                                                          keys,
                                                          res );
            auto_lv_day.setAdapter( dayAdapter );
            auto_lv_night.setAdapter( nightAdapter );

            if ( mTurnoffEnabled )
            {
                auto_tv_turnoff.setText( df.format( mTurnoffTime/60 ) + ":" + df.format( mTurnoffTime%60 ) );
            }
            else
            {
                auto_tv_turnoff.setText( R.string.disabled );
            }
        }
    }

    @Override
    public void showDataChanged()
    {
        if ( mLight.getPreviewFlag() && !mPreviewFlag )
        {
            mPreviewFlag = true;
            auto_btn_prev.setText( R.string.stop );
            auto_btn_prev.setCompoundDrawablesWithIntrinsicBounds( 0, R.mipmap.ic_stop, 0, 0 );
            final int[] previewCount = new int[]{ 0};
            mPreviewTimer = new Timer();
            mPreviewTask = new TimerTask() {
                @Override
                public void run()
                {
                    if ( getActivity() != null )
                    {
                        getActivity().runOnUiThread( new TimerTask() {
                            @Override
                            public void run()
                            {
                                previewCount[0]++;
                                if ( previewCount[0] > 1439 )
                                {
                                    cancel();
                                    mPreviewFlag = false;
                                    auto_line_chart.getXAxis()
                                                   .removeAllLimitLines();
                                    auto_line_chart.invalidate();
                                    auto_btn_prev.setText( R.string.preview );
                                    auto_btn_prev.setCompoundDrawablesWithIntrinsicBounds( 0, R.mipmap.ic_preview, 0, 0 );
                                    return;
                                }
                                auto_line_chart.getXAxis()
                                               .removeAllLimitLines();
                                LimitLine limitLine = new LimitLine( previewCount[0] );
                                limitLine.setLineWidth( 1 );
                                limitLine.setLineColor( 0xFFFF4081 );
                                auto_line_chart.getXAxis()
                                               .addLimitLine( limitLine );
                                auto_line_chart.invalidate();
                            }
                        } );
                    }
                }
            };
            mPreviewTimer.schedule( mPreviewTask, 0, 50 );
        }
        else if ( !mLight.getPreviewFlag() && mPreviewFlag )
        {
            if ( mPreviewTask != null )
            {
                mPreviewTask.cancel();
                mPreviewTask = null;
            }
            if ( mPreviewTimer != null )
            {
                mPreviewTimer.cancel();
                mPreviewTimer = null;
            }
            mPreviewFlag = false;
            auto_btn_prev.setText( R.string.preview );
            auto_btn_prev.setCompoundDrawablesWithIntrinsicBounds( 0, R.mipmap.ic_preview, 0, 0 );
            auto_line_chart.getXAxis()
                           .removeAllLimitLines();
            auto_line_chart.invalidate();
        }
    }

    @Override
    public void showEditSunriseDialog()
    {

    }

    @Override
    public void showEditDaylightDialog()
    {

    }

    @Override
    public void showEditSunsetDialog()
    {

    }

    @Override
    public void showEditNightDialog()
    {

    }

    @Override
    public void showEditTurnoffDialog()
    {

    }

    @Override
    public void showPreviewUpdate( int tm )
    {
        auto_line_chart.getXAxis()
                      .removeAllLimitLines();
        LimitLine limitLine = new LimitLine( tm );
        limitLine.setLineWidth( 1 );
        limitLine.setLineColor( 0xFFFF4081 );
        auto_line_chart.getXAxis()
                      .addLimitLine( limitLine );
        auto_line_chart.invalidate();
    }

    @Override
    public void showPreviewStopped()
    {
        auto_line_chart.getXAxis()
                       .removeAllLimitLines();
        auto_line_chart.invalidate();
    }

    @SuppressLint ( "RestrictedApi" )
    private void showEditSunrsDialog( final boolean sunset )
    {
        mPresenter.removeDataListener();
        View view = LayoutInflater.from( getContext() ).inflate( R.layout.dialog_edit_sunrise_sunset, null, false );
        view.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, auto_iv_sunrise.getHeight() ) );
        final BottomSheetDialog dialog = new BottomSheetDialog( getContext() );
        dialog.setContentView( view );
        dialog.setCanceledOnTouchOutside( false );
        CheckableImageButton cib_bg = view.findViewById( R.id.dialog_sunrs_bg );
        final TimePicker tp_start = view.findViewById( R.id.dialog_sunrs_tp_start );
        final TimePicker tp_end = view.findViewById( R.id.dialog_sunrs_tp_end );
        Button btn_cancel = view.findViewById( R.id.dialog_sunrs_cancel );
        Button btn_save = view.findViewById( R.id.dialog_sunrs_save );
        cib_bg.setChecked( sunset );
        int start = sunset ? mSunsetStart : mSunriseStart;
        int end = sunset ? mSunsetEnd : mSunriseEnd;
        tp_start.setIs24HourView( true );
        tp_end.setIs24HourView( true );
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            tp_start.setHour( start/60 );
            tp_start.setMinute( start%60 );
            tp_end.setHour( end/60 );
            tp_end.setMinute( end%60 );
        }
        else
        {
            tp_start.setCurrentHour( start/60 );
            tp_start.setCurrentMinute( start%60 );
            tp_end.setCurrentHour( end/60 );
            tp_end.setCurrentMinute( end%60 );
        }
        btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                dialog.dismiss();
                mSunriseStart = mLight.getSunriseStart();
                mSunriseEnd = mLight.getSunriseEnd();
                mSunsetStart = mLight.getSunsetStart();
                mSunsetEnd = mLight.getSunsetEnd();
                refreshData();
            }
        } );
        btn_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                dialog.dismiss();
                if ( sunset )
                {
                    mPresenter.setSunset( mSunsetStart, mSunsetEnd );
                }
                else
                {
                    mPresenter.setSunrise( mSunriseStart, mSunriseEnd );
                }
            }
        } );
        tp_start.setOnTimeChangedListener( new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged( TimePicker timePicker, int i, int i1 )
            {
                int tm = i * 60 + i1;
                if ( sunset )
                {
                    mSunsetStart = tm;
                }
                else
                {
                    mSunriseStart = tm;
                }
                refreshData();
            }
        } );
        tp_end.setOnTimeChangedListener( new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged( TimePicker timePicker, int i, int i1 )
            {
                int tm = i * 60 + i1;
                if ( sunset )
                {
                    mSunsetEnd = tm;
                }
                else
                {
                    mSunriseEnd = tm;
                }
                refreshData();
            }
        } );
        dialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss( DialogInterface dialogInterface )
            {
                mPresenter.addDataListener();
            }
        } );
        dialog.show();
    }

    @SuppressLint ( "RestrictedApi" )
    private void showEditDayNightDialog( final boolean night )
    {
        mPresenter.removeDataListener();
        View view = LayoutInflater.from( getContext() ).inflate( R.layout.dialog_edit_day_night, null, false );
        CheckableImageButton cib_bg = view.findViewById( R.id.dialog_daynight_bg );
        ListView listView = view.findViewById( R.id.dialog_daynight_lv );
        Button btn_cancel = view.findViewById( R.id.dialog_daynight_cancel );
        Button btn_save = view.findViewById( R.id.dialog_daynight_save );
        cib_bg.setChecked( night );
        view.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, auto_iv_sunrise.getHeight() ) );
        final BottomSheetDialog dialog = new BottomSheetDialog( getContext() );
        dialog.setContentView( view );
        dialog.setCanceledOnTouchOutside( false );
        DialogSliderAdeapter adapter = new DialogSliderAdeapter( night ? mNightBrights : mDayBrights );
        listView.setAdapter( adapter );
        btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                dialog.dismiss();
                mDayBrights = Arrays.copyOf( mLight.getDayBright(), mLight.getDayBright().length );
                mNightBrights = Arrays.copyOf( mLight.getNightBright(), mLight.getNightBright().length );
                refreshData();
            }
        } );
        btn_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                dialog.dismiss();
                if ( night )
                {
                    mPresenter.setNightBright( mNightBrights );
                }
                else
                {
                    mPresenter.setDayBright( mDayBrights );
                }
            }
        } );
        dialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss( DialogInterface dialogInterface )
            {
                mPresenter.addDataListener();
            }
        } );
        dialog.show();
    }

    private void showTurnoffDialog()
    {
        mPresenter.removeDataListener();
        View view = LayoutInflater.from( getContext() ).inflate( R.layout.dialog_edit_turnoff, null, false );
        final Switch sw_enable = view.findViewById( R.id.dialog_turnoff_enable );
        TimePicker tp_turnoff = view.findViewById( R.id.dialog_turnoff_time );
        Button btn_cancel = view.findViewById( R.id.dialog_turnoff_cancel );
        Button btn_save = view.findViewById( R.id.dialog_turnoff_save );
        sw_enable.setChecked( mTurnoffEnabled );
        tp_turnoff.setIs24HourView( true );
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            tp_turnoff.setHour( mTurnoffTime/60 );
            tp_turnoff.setMinute( mTurnoffTime%60 );
        }
        else
        {
            tp_turnoff.setCurrentHour( mTurnoffTime/60 );
            tp_turnoff.setCurrentMinute( mTurnoffTime%60 );
        }
        view.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, auto_iv_sunrise.getHeight() ) );
        final BottomSheetDialog dialog = new BottomSheetDialog( getContext() );
        dialog.setContentView( view );
        dialog.setCanceledOnTouchOutside( false );
        sw_enable.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton compoundButton, boolean b )
            {
                mTurnoffEnabled = b;
                refreshData();
            }
        } );
        tp_turnoff.setOnTimeChangedListener( new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged( TimePicker timePicker, int i, int i1 )
            {
                mTurnoffTime = i * 60 + i1;
                refreshData();
            }
        } );
        btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                dialog.dismiss();
                mTurnoffEnabled = mLight.isTurnoffEnabled();
                mTurnoffTime = mLight.getTurnoffTime();
                refreshData();
            }
        } );
        btn_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                dialog.dismiss();
                mPresenter.setTurnoff( sw_enable.isChecked(), mTurnoffTime );
            }
        } );
        dialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss( DialogInterface dialogInterface )
            {
                mPresenter.addDataListener();
            }
        } );
        dialog.show();
    }

    @Override
    public void onClick( View view )
    {
        switch ( view.getId() )
        {
            case R.id.auto_tv_sunrise:
                showEditSunrsDialog( false );
                break;
            case R.id.auto_tv_sunset:
                showEditSunrsDialog( true );
                break;
            case R.id.auto_tv_turnoff:
                showTurnoffDialog();
                break;
        }
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
        if ( getActivity() == null )
        {
            return;
        }
        getActivity().runOnUiThread( runnable );
    }

    class DialogSliderAdeapter extends BaseAdapter
    {
        private byte[] mBrights;

        public DialogSliderAdeapter( byte[] brights )
        {
            mBrights = brights;
        }

        @Override
        public int getCount()
        {
            return mBrights == null ? 0 : mBrights.length;
        }

        @Override
        public Object getItem( int i )
        {
            return mBrights[i];
        }

        @Override
        public long getItemId( int i )
        {
            return i;
        }

        @Override
        public View getView( final int postion, View view, ViewGroup viewGroup )
        {
            ViewHolder holder;
            if ( view == null )
            {
                view = LayoutInflater.from( getContext() ).inflate( R.layout.dialog_item_slider, viewGroup, false );
                holder = new ViewHolder();
                holder.iv_icon = view.findViewById( R.id.dialog_item_slider_color );
                holder.sb_progress = view.findViewById( R.id.dialog_item_slider_progress );
                holder.tv_percent = view.findViewById( R.id.dialog_item_slider_percent );
                view.setTag( holder );
            }
            else
            {
                holder = (ViewHolder) view.getTag();
            }
            String color = mLight.getChannelName( postion );
            holder.iv_icon.setImageResource( LightUtil.getIconRes( color ) );
            Drawable progressDraw = getResources().getDrawable( LightUtil.getProgressRes( color ) );
            Drawable thumbDraw = getResources().getDrawable( LightUtil.getThumbRes( color ) );
            holder.sb_progress.setProgressDrawable( progressDraw );
            holder.sb_progress.setThumb( thumbDraw );
            holder.sb_progress.setProgress( mBrights[postion] );
            holder.tv_percent.setText( "" + mBrights[postion] + "%" );
            final ViewHolder finalHolder = holder;
            holder.sb_progress.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged( SeekBar seekBar, int i, boolean b )
                {
                    if ( b )
                    {
                        mBrights[postion] = (byte) i;
                        finalHolder.tv_percent.setText( "" + i + "%" );
                        refreshData();
                    }
                }

                @Override
                public void onStartTrackingTouch( SeekBar seekBar )
                {

                }

                @Override
                public void onStopTrackingTouch( SeekBar seekBar )
                {

                }
            } );
            return view;
        }

        class ViewHolder
        {
            private ImageView iv_icon;
            private SeekBar sb_progress;
            private TextView tv_percent;
        }
    }
}
