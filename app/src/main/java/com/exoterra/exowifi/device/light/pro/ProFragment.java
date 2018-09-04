package com.exoterra.exowifi.device.light.pro;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exoterra.comman.BaseFragment;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.device.LightUtil;
import com.exoterra.exowifi.manager.DeviceManager;
import com.exoterra.exowifi.manager.XlinkListenerManager;
import com.exoterra.exowifi.view.MultiPointSeekbar;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.xlink.sdk.v5.listener.XLinkDataListener;
import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.model.XLinkDataPoint;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProFragment extends BaseFragment
{
    private final int POINT_COUNT_MIN = 4;
    private final int POINT_COUNT_MAX = 10;

    private LineChart pro_line_chart;
    private MultiPointSeekbar pro_mps;
    private ImageButton pro_ib_edit;
    private LinearLayout pro_ll_edit;
    private ImageButton pro_tmr_dec;
    private TextView pro_tv_tmr;
    private ImageButton pro_tmr_inc;
    private RecyclerView pro_rv_show;
    private Button pro_btn_cancel;
    private Button pro_btn_prev;
    private Button pro_btn_next;
    private Button pro_btn_save;

    private ExoLightStrip mLight;
    private LineData mLineData;
    private List<ILineDataSet > mDataSets;

    private int mPointCount;
    private int[] mPointTimer;
    private byte[][] mPointBright;
    private ProAdapter mAdapter;

    private XLinkDataListener mDataListener;
    private boolean mPreviewFlag;
    private Timer mPreviewTimer;
    private TimerTask mPreviewTask;

    public static ProFragment newInstance( String mac )
    {
        ProFragment frag = new ProFragment();
        Bundle bundle = new Bundle();
        bundle.putString( Constant.KEY_MAC_ADDRESS, mac );
        frag.setArguments( bundle );

        return frag;
    }

    public ProFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_pro, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        XlinkListenerManager.getInstance().removeXlinkDataListener( mDataListener );
    }

    @Override
    protected void initView( View view )
    {
        pro_line_chart = view.findViewById( R.id.pro_line_chart );
        pro_mps = view.findViewById( R.id.pro_mps );
        pro_ib_edit = view.findViewById( R.id.pro_ib_edit );
        pro_ll_edit = view.findViewById( R.id.pro_ll_edit );
        pro_tmr_dec = view.findViewById( R.id.pro_tmr_dec );
        pro_tv_tmr = view.findViewById( R.id.pro_tv_tmr );
        pro_tmr_inc = view.findViewById( R.id.pro_tmr_inc );
        pro_rv_show = view.findViewById( R.id.pro_rv_show );
        pro_btn_cancel = view.findViewById( R.id.pro_btn_cancel );
        pro_btn_prev = view.findViewById( R.id.pro_btn_prev );
        pro_btn_next = view.findViewById( R.id.pro_btn_next );
        pro_btn_save = view.findViewById( R.id.pro_btn_save );
        pro_rv_show.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.HORIZONTAL, false ) );

        XAxis xAxis = pro_line_chart.getXAxis();
        YAxis axisLeft = pro_line_chart.getAxisLeft();
        YAxis axisRight = pro_line_chart.getAxisRight();
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
        pro_line_chart.setTouchEnabled( false );
        pro_line_chart.setDragEnabled( false );
        pro_line_chart.setScaleEnabled( false );
        pro_line_chart.setPinchZoom( false );
        pro_line_chart.setDoubleTapToZoomEnabled( false );
        pro_line_chart.setBorderColor( Color.CYAN );
        pro_line_chart.setBorderWidth( 1 );
        pro_line_chart.setDrawBorders( false );
        pro_line_chart.setDrawGridBackground( true );
        pro_line_chart.setGridBackgroundColor( Color.TRANSPARENT );
        pro_line_chart.setDescription( null );
        pro_line_chart.setMaxVisibleValueCount( 0 );
        pro_line_chart.getLegend().setTextColor( Color.WHITE );
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

            mPointCount = mLight.getPointCount();
            mPointTimer = mLight.getPointTimers();
            mPointBright = mLight.getPointBrights();

            pro_mps.setPointCount( mPointCount );
            pro_mps.setMax( 1439 );
            pro_mps.setProgress( mPointTimer );
            pro_mps.clearSelectedPoint();
            pro_mps.setMaxLengthHint( "88:88" );
            pro_mps.setGetTextImpl( new MultiPointSeekbar.GetTextImpl() {
                @Override
                public String getText( int progress )
                {
                    if ( progress >= pro_mps.getMin() && progress <= pro_mps.getMax() )
                    {
                        DecimalFormat df = new DecimalFormat( "00" );
                        return df.format( progress/60 ) + ":" + df.format( progress%60 );
                    }
                    return "" + progress;
                }
            } );

            mAdapter = new ProAdapter( mPointBright, mLight.getChannelNames() );
            pro_rv_show.setAdapter( mAdapter );

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
                        if ( mLight.getPreviewFlag() && !mPreviewFlag )
                        {
                            mPreviewFlag = true;
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
                                                    mPreviewTask = null;
                                                    if ( mPreviewTimer != null )
                                                    {
                                                        mPreviewTimer.cancel();
                                                        mPreviewTimer = null;
                                                    }
                                                    mPreviewFlag = false;
                                                    pro_line_chart.getXAxis()
                                                                   .removeAllLimitLines();
                                                    pro_line_chart.invalidate();
                                                    return;
                                                }
                                                pro_line_chart.getXAxis()
                                                               .removeAllLimitLines();
                                                LimitLine limitLine = new LimitLine( previewCount[0] );
                                                limitLine.setLineWidth( 1 );
                                                limitLine.setLineColor( 0xFFFF4081 );
                                                pro_line_chart.getXAxis()
                                                               .addLimitLine( limitLine );
                                                pro_line_chart.invalidate();
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
                            if ( getActivity() != null )
                            {
                                getActivity().runOnUiThread( new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        pro_line_chart.getXAxis()
                                                      .removeAllLimitLines();
                                        pro_line_chart.invalidate();
                                    }
                                } );
                            }
                        }
                    }
                }
            };

            refreshData();

            XlinkListenerManager.getInstance().addXlinkDataListener( mDataListener );
        }

    }

    @Override
    protected void initEvent()
    {
        pro_mps.setListener( new MultiPointSeekbar.Listener() {
            @Override
            public void onPointCountChanged( int pointCount )
            {

            }

            @Override
            public void onPointSelected( int index )
            {
                if ( mLight != null && index >= 0 && index < mPointCount )
                {
                    pro_ll_edit.setVisibility( View.VISIBLE );
                    pro_ib_edit.setVisibility( View.GONE );
                    int tmr = mPointTimer[index];
                    DecimalFormat df = new DecimalFormat( "00" );
                    pro_tv_tmr.setText( df.format( tmr/60 ) + ":" + df.format( tmr%60 ) );
                    mAdapter.setSelectedPoint( index );
                }
            }

            @Override
            public void onMultiPointTouched( List< Integer > points )
            {
                if ( mLight != null )
                {
                    showPopDialog( points );
                }
            }

            @Override
            public void onStartPointTouch( int index )
            {
                if ( mLight != null && index >= 0 && index < mPointCount )
                {
                    mPointTimer[index] = pro_mps.getProgressByIndex( index );
                    refreshData();
                    DecimalFormat df = new DecimalFormat( "00" );
                    pro_tv_tmr.setText( df.format( mPointTimer[index]/60 ) + ":" + df.format( mPointTimer[index]%60 ) );
                }
            }

            @Override
            public void onStopPointTouch( int index )
            {
                if ( mLight != null && index >= 0 && index < mPointCount )
                {
                    mPointTimer[index] = pro_mps.getProgressByIndex( index );
                    refreshData();
                    DecimalFormat df = new DecimalFormat( "00" );
                    pro_tv_tmr.setText( df.format( mPointTimer[index]/60 ) + ":" + df.format( mPointTimer[index]%60 ) );
                }
            }

            @Override
            public void onPointProgressChanged( int index, int progress, boolean fromUser )
            {
                if ( mLight != null && index >= 0 && index < mPointCount )
                {
                    mPointTimer[index] = progress;
                    refreshData();
                    DecimalFormat df = new DecimalFormat( "00" );
                    pro_tv_tmr.setText( df.format( progress/60 ) + ":" + df.format( progress%60 ) );
                }
            }
        } );
        pro_ib_edit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                if ( mLight != null )
                {
                    showSetPointCountDialog( mPointCount );
//                    mLight.setLightPreview( true );
                }

            }
        } );
        pro_tmr_dec.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                decTimer( pro_mps.getSelectedPoint() );
            }
        } );
        pro_tmr_inc.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                incTimer( pro_mps.getSelectedPoint() );
            }
        } );
        pro_tmr_dec.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick( final View v )
            {
                new Timer().schedule( new TimerTask() {
                    @Override
                    public void run()
                    {
                        if ( v.isPressed() )
                        {
                            decTimer( pro_mps.getSelectedPoint() );
                        }
                        else
                        {
                            this.cancel();
                        }
                    }
                }, 0, 20 );
                return true;
            }
        } );
        pro_tmr_inc.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick( final View v )
            {
                new Timer().schedule( new TimerTask() {
                    @Override
                    public void run()
                    {
                        if ( v.isPressed() )
                        {
                            incTimer( pro_mps.getSelectedPoint() );
                        }
                        else
                        {
                            this.cancel();
                        }
                    }
                }, 0, 20 );
                return true;
            }
        } );
        pro_btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                pro_mps.clearSelectedPoint();
                pro_ll_edit.setVisibility( View.GONE );
                pro_ib_edit.setVisibility( View.VISIBLE );
                if ( mLight != null )
                {
                    mPointCount = mLight.getPointCount();
                    mPointTimer = mLight.getPointTimers();
                    mPointBright = mLight.getPointBrights();
                    refreshData();
                    mAdapter.setBright( mPointBright );
                    pro_mps.setProgress( mPointTimer );
                }
            }
        } );
        pro_btn_prev.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {


                int point = pro_mps.getSelectedPoint();
                if ( mLight != null && point >= 0 && point < mPointCount )
                {
                    int[] index = new int[mPointCount];
                    for ( int i = 0; i < mPointCount; i++ )
                    {
                        index[i] = i;
                    }
                    for ( int i = mPointCount - 1; i > 0; i-- )
                    {
                        for ( int j = 0; j < i; j++ )
                        {
                            if ( mPointTimer[index[j]] > mPointTimer[index[j + 1]] )
                            {
                                int temp = index[j];
                                index[j] = index[j+1];
                                index[j+1] = temp;
                            }
                        }
                    }
                    for ( int i = 0; i < mPointCount; i++ )
                    {
                        if ( index[i] == point )
                        {
                            point = index[(mPointCount+i-1)%mPointCount];
                            pro_mps.setSelectedPoint( point );
                            int tmr = mPointTimer[point];
                            DecimalFormat df = new DecimalFormat( "00" );
                            pro_tv_tmr.setText( df.format( tmr/60 ) + ":" + df.format( tmr%60 ) );
                            mAdapter.setSelectedPoint( pro_mps.getSelectedPoint() );
//                            showSelectedTimer();
                            return;
                        }
                    }
                }
            }
        } );
        pro_btn_next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                int point = pro_mps.getSelectedPoint();
                if ( mLight != null && point >= 0 && point < mPointCount )
                {
                    int[] index = new int[mPointCount];
                    for ( int i = 0; i < mPointCount; i++ )
                    {
                        index[i] = i;
                    }
                    for ( int i = mPointCount - 1; i > 0; i-- )
                    {
                        for ( int j = 0; j < i; j++ )
                        {
                            if ( mPointTimer[index[j]] > mPointTimer[index[j + 1]] )
                            {
                                int temp = index[j];
                                index[j] = index[j+1];
                                index[j+1] = temp;
                            }
                        }
                    }
                    for ( int i = 0; i < mPointCount; i++ )
                    {
                        if ( index[i] == point )
                        {
                            point = index[(i+1)%mPointCount];
                            pro_mps.setSelectedPoint( point );
                            int tmr = mPointTimer[point];
                            DecimalFormat df = new DecimalFormat( "00" );
                            pro_tv_tmr.setText( df.format( tmr/60 ) + ":" + df.format( tmr%60 ) );
                            mAdapter.setSelectedPoint( pro_mps.getSelectedPoint() );
//                            showSelectedTimer();
                            return;
                        }
                    }
                }
            }
        } );
        pro_btn_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                pro_mps.clearSelectedPoint();
                if ( mLight != null )
                {
                    mLight.setLightPoint( mPointCount, mPointTimer, mPointBright );
                    pro_ll_edit.setVisibility( View.GONE );
                    pro_ib_edit.setVisibility( View.VISIBLE );
                    pro_mps.clearSelectedPoint();
                }
            }
        } );
    }

    private void refreshData()
    {
        if ( mLight != null && mPointCount >= POINT_COUNT_MIN && mPointCount <= POINT_COUNT_MAX )
        {
            if ( mDataSets == null )
            {
                mDataSets = new ArrayList<>();
            }
            mDataSets.clear();

            int[] index = new int[mPointCount];
            for ( int i = 0; i < mPointCount; i++ )
            {
                index[i] = i;
            }
            for ( int i = mPointCount - 1; i > 0; i-- )
            {
                for ( int j = 0; j < i; j++ )
                {
                    if ( mPointTimer[index[j]] > mPointTimer[index[j + 1]] )
                    {
                        int temp = index[j];
                        index[j] = index[j+1];
                        index[j+1] = temp;
                    }
                }
            }
            for ( int i = 0; i < mLight.getChannelCount(); i++ )
            {
                List<Entry > entries = new ArrayList<>();
                int ts = mPointTimer[index[0]];
                int te = mPointTimer[index[mPointCount-1]];
                int bs = mPointBright[index[0]][i];
                int be = mPointBright[index[mPointCount-1]][i];
                int duration = 1440 - te + ts;
                int dbrt = bs - be;
                float b0 = be + dbrt * ( 1440 - te) / (float) duration;
                entries.add( new Entry( 0, b0 ) );
                int idx;
                for ( int j = 0; j < mPointCount; j++ )
                {
                    idx = index[j];
                    entries.add( new Entry( mPointTimer[idx], mPointBright[idx][i] ) );
                }
                entries.add( new Entry( 1440, b0 ) );

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
            }
            mLineData = new LineData( mDataSets );
            pro_line_chart.setData( mLineData );
            pro_line_chart.invalidate();
//            showSelectedTimer();

            if ( pro_mps.getSelectedPoint() >= 0 && pro_mps.getSelectedPoint() < mPointCount )
            {
                pro_ib_edit.setVisibility( View.GONE );
                pro_ll_edit.setVisibility( View.VISIBLE );
            }
            else
            {
                pro_ib_edit.setVisibility( View.VISIBLE );
                pro_ll_edit.setVisibility( View.GONE );
            }
        }
    }

    private void incTimer( final int point )
    {
        if ( mLight != null )
        {
            if ( point >= 0 && point < mPointCount )
            {
                if ( mPointTimer[point] < 1439 )
                {
                    mPointTimer[point]++;
                }
                else
                {
                    mPointTimer[point] = 0;
                }
                final DecimalFormat df = new DecimalFormat( "00" );
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run()
                    {
                        pro_tv_tmr.setText( df.format( mPointTimer[point]/60 ) + ":" + df.format( mPointTimer[point]%60 ) );
                        refreshData();
                        pro_mps.setProgress( point, mPointTimer[point] );
                    }
                } );
            }
        }
    }

    private void decTimer( final int point )
    {
        if ( mLight != null )
        {
            if ( point >= 0 && point < mPointCount )
            {
                if ( mPointTimer[point] > 0 )
                {
                    mPointTimer[point]--;
                }
                else
                {
                    mPointTimer[point] = 1439;
                }
                final DecimalFormat df = new DecimalFormat( "00" );
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run()
                    {
                        pro_tv_tmr.setText( df.format( mPointTimer[point]/60 ) + ":" + df.format( mPointTimer[point]%60 ) );
                        refreshData();
                        pro_mps.setProgress( point, mPointTimer[point] );
                    }
                } );
            }
        }
    }

    private void initPara( int count )
    {
        if ( mLight == null || count < POINT_COUNT_MIN || count > POINT_COUNT_MAX )
        {
            return;
        }
        if ( count != mPointCount )
        {
            mPointCount = count;
            mPointTimer = new int[count];
            mPointBright = new byte[count][mLight.getChannelCount()];
            if ( count == 4 )
            {
                mPointTimer[0] = 7 * 60;
                mPointTimer[1] = 8 * 60;
                mPointTimer[2] = 17 * 60;
                mPointTimer[3] = 18 * 60;
                for ( int i = 0; i < mLight.getChannelCount(); i++ )
                {
                    mPointBright[0][i] = 0;
                    mPointBright[1][i] = 100;
                    mPointBright[2][i] = 100;
                    mPointBright[3][i] = 0;
                }
            }
            else if ( count == 5 )
            {
                mPointTimer[0] = 7 * 60;
                mPointTimer[1] = 8 * 60;
                mPointTimer[2] = 17 * 60;
                mPointTimer[3] = 18 * 60;
                mPointTimer[4] = 21 * 60;
                for ( int i = 0; i < mLight.getChannelCount(); i++ )
                {
                    String name = mLight.getChannelName( i );
                    if ( !TextUtils.isEmpty( name ) )
                    {
                        if ( name.endsWith( "\0" ) )
                        {
                            name = name.substring( 0, name.length() - 1 );
                        }
                        name = name.toLowerCase();
                    }
                    mPointBright[0][i] = 0;
                    mPointBright[1][i] = 100;
                    mPointBright[2][i] = 100;
                    if ( "blue".equals( name ) )
                    {
                        mPointBright[3][i] = 5;
                    }
                    else
                    {
                        mPointBright[3][i] = 0;
                    }
                    mPointBright[4][i] = 0;
                }
            }
            else
            {
                mPointTimer[0] = 7 * 60;
                mPointTimer[1] = 8 * 60;
                for ( int i = 2; i < count - 4; i++ )
                {
                    mPointTimer[i] = ( 6 + i * 2 ) * 60;
                }
                mPointTimer[count-4] = 17 * 60;
                mPointTimer[count-3] = 18 * 60;
                mPointTimer[count-2] = 21 * 60;
                mPointTimer[count-1] = 22 * 60;
                for ( int i = 0; i < mLight.getChannelCount(); i++ )
                {
                    String name = mLight.getChannelName( i );
                    if ( !TextUtils.isEmpty( name ) )
                    {
                        if ( name.endsWith( "\0" ) )
                        {
                            name = name.substring( 0, name.length() - 1 );
                        }
                        name = name.toLowerCase();
                    }
                    mPointBright[0][i] = 0;
                    for ( int j = 1; j < count - 3; j++ )
                    {
                        mPointBright[j][i] = 100;
                    }
                    if ( "blue".equals( name ) )
                    {
                        mPointBright[count-3][i] = 5;
                        mPointBright[count-2][i] = 5;
                    }
                    else
                    {
                        mPointBright[count-3][i] = 0;
                        mPointBright[count-2][i] = 0;
                    }
                    mPointBright[count-1][i] = 0;
                }
            }
            pro_mps.setProgress( mPointTimer );
            mAdapter.setBright( mPointBright );
        }
        mAdapter.setBright( mPointBright );
        pro_mps.setSelectedPoint( 0 );
        refreshData();
    }

//    private void showSelectedTimer()
//    {
//        if ( mLight == null || pro_mps.getSelectedPoint() < 0 || pro_mps.getSelectedPoint() >= mPointCount )
//        {
//            pro_line_chart.getXAxis()
//                          .removeAllLimitLines();
//            return;
//        }
//        pro_line_chart.getXAxis()
//                      .removeAllLimitLines();
//        LimitLine limitLine = new LimitLine( pro_mps.getProgressByIndex( pro_mps.getSelectedPoint() ) );
//        limitLine.setLineWidth( 1 );
//        limitLine.setLineColor( 0xFFFF4081 );
//        pro_line_chart.getXAxis()
//                      .addLimitLine( limitLine );
//        pro_line_chart.invalidate();
//    }

    private void showSetPointCountDialog( final int def )
    {
        final int idx = ( def < POINT_COUNT_MIN || def > POINT_COUNT_MAX ) ? -1 : def - POINT_COUNT_MIN;
        final int[] selectedItem = new int[]{idx};
        String[] array = new String[POINT_COUNT_MAX-POINT_COUNT_MIN+1];
        for ( int i = 0; i < POINT_COUNT_MAX-POINT_COUNT_MIN+1; i++ )
        {
            array[i] = "" + (POINT_COUNT_MIN + i);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setTitle( R.string.set_points_count );
        builder.setSingleChoiceItems( array, idx, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                selectedItem[0] = which;
            }
        } );
        final AlertDialog dlg = builder.create();
        dlg.setButton( DialogInterface.BUTTON_NEGATIVE, getContext().getString( R.string.cancel ), (DialogInterface.OnClickListener) null );
        dlg.setButton( DialogInterface.BUTTON_POSITIVE, getContext().getString( R.string.ok ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                initPara( selectedItem[0] + POINT_COUNT_MIN );
            }
        } );
        dlg.setCanceledOnTouchOutside( false );
        dlg.show();
    }

    private void showPopDialog( List<Integer> list )
    {
        if ( list == null || list.size() == 0 )
        {
            return;
        }
        String[] array = new String[list.size()];
        for ( int i = 0; i < array.length; i++ )
        {
            array[i] = "" + list.get( i );
        }
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setSingleChoiceItems( array, -1, null );
        builder.show();
    }

    class ProAdapter extends RecyclerView.Adapter<ProAdapter.ProViewHolder >
    {
        private byte[][] mBright;
        private String[] mChannelNames;
        private int mSelectedPoint;
        private long tmr;

        public ProAdapter( byte[][] bright, String[] channelNames )
        {
            mBright = bright;
            mChannelNames = channelNames;
        }

        @Override
        public ProViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
        {
            ProViewHolder holder = new ProViewHolder( LayoutInflater.from( getContext() ).inflate( R.layout.item_up_down,
                                                                                               parent,
                                                                                               false ) );
            return holder;
        }

        @Override
        public void onBindViewHolder( final ProViewHolder holder, final int position )
        {
            String color = mChannelNames[position];
            holder.ib_up.setImageResource( LightUtil.getUpRes( color ) );
            holder.ib_down.setImageResource( LightUtil.getDownRes( color ) );
            if ( mSelectedPoint >= 0 && mSelectedPoint < mBright.length )
            {
                holder.tv_percent.setText( "" + mBright[mSelectedPoint][position] + "%" );
                holder.ib_up.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick( View v )
                    {
                        if ( mBright[mSelectedPoint][position] < 100 )
                        {
                            mBright[mSelectedPoint][position]++;
                            holder.tv_percent.setText( "" + mBright[mSelectedPoint][position] + "%" );
                            refreshData();
                        }
                    }
                } );
                holder.ib_down.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick( View v )
                    {
                        if ( mBright[mSelectedPoint][position] > 0 )
                        {
                            mBright[mSelectedPoint][position]--;
                            holder.tv_percent.setText( "" + mBright[mSelectedPoint][position] + "%" );
                            refreshData();
                        }
                    }
                } );
                holder.ib_up.setOnLongClickListener( new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick( final View v )
                    {
                        new Timer().schedule( new TimerTask() {
                            @Override
                            public void run()
                            {
                                if ( v.isPressed() == false )
                                {
                                    this.cancel();
                                    return;
                                }
                                if ( mBright[mSelectedPoint][position] < 100 )
                                {
                                    mBright[mSelectedPoint][position]++;
                                    getActivity().runOnUiThread( new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            holder.tv_percent.setText( "" + mBright[mSelectedPoint][position] + "%" );
                                            refreshData();
                                        }
                                    } );
                                }
                            }
                        }, 0, 50 );
                        return true;
                    }
                } );
                holder.ib_down.setOnLongClickListener( new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick( final View v )
                    {
                        new Timer().schedule( new TimerTask() {
                            @Override
                            public void run()
                            {
                                if ( v.isPressed() == false )
                                {
                                    this.cancel();
                                    return;
                                }
                                if ( mBright[mSelectedPoint][position] > 0 )
                                {
                                    mBright[mSelectedPoint][position]--;
                                    getActivity().runOnUiThread( new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            holder.tv_percent.setText( "" + mBright[mSelectedPoint][position] + "%" );
                                            refreshData();
                                        }
                                    } );
                                }
                            }
                        }, 0, 50 );
                        return true;
                    }
                } );
            }
        }

        @Override
        public int getItemCount()
        {
            return mChannelNames == null ? 0 : mChannelNames.length;
        }

        public int getSelectedPoint()
        {
            return mSelectedPoint;
        }

        public void setSelectedPoint( int selectedPoint )
        {
            if ( mBright == null )
            {
                return;
            }
            if ( selectedPoint >= 0 && selectedPoint < mBright.length && selectedPoint != mSelectedPoint )
            {
                mSelectedPoint = selectedPoint;
                notifyDataSetChanged();
            }
        }

        public void setBright( byte[][] bright )
        {
            mBright = bright;
            notifyDataSetChanged();
        }

        class ProViewHolder extends RecyclerView.ViewHolder
        {
            private ImageButton ib_down;
            private TextView tv_percent;
            private ImageButton ib_up;

            public ProViewHolder( View itemView )
            {
                super( itemView );
                ib_down = itemView.findViewById( R.id.item_ib_down );
                tv_percent = itemView.findViewById( R.id.item_tv_percent );
                ib_up = itemView.findViewById( R.id.item_ib_up );
            }
        }
    }
}
