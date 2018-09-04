package com.exoterra.exowifi.device.light.manual;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.exoterra.comman.BaseFragment;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.device.LightUtil;
import com.exoterra.exowifi.manager.DeviceManager;
import com.exoterra.exowifi.view.MultiCircleProgress;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManualFragment extends BaseFragment implements View.OnClickListener,
                                                            View.OnLongClickListener,
                                                            IManualView
{
    private RecyclerView manual_rv_show;
    private MultiCircleProgress manual_mcp_custom1;
    private MultiCircleProgress manual_mcp_custom2;
    private MultiCircleProgress manual_mcp_custom3;
    private MultiCircleProgress manual_mcp_custom4;
    private CheckableImageButton manual_cib_power;

    private ExoLightStrip mLight;
    private ManualPresenter mPresenter;
    private ManualSliderAdapter mAdapter;

    public static ManualFragment newInstance( String mac )
    {
        ManualFragment frag = new ManualFragment();
        Bundle bundle = new Bundle();
        bundle.putString( Constant.KEY_MAC_ADDRESS, mac );
        frag.setArguments( bundle );
        return frag;
    }

    public ManualFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_manual, container, false );

        initView( view );
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mPresenter.removeDataListener();
    }

    @Override
    protected void initView( View view )
    {
        manual_rv_show = view.findViewById( R.id.manual_rv_show );
        manual_mcp_custom1 = view.findViewById( R.id.manual_mcp_custom1 );
        manual_mcp_custom2 = view.findViewById( R.id.manual_mcp_custom2 );
        manual_mcp_custom3 = view.findViewById( R.id.manual_mcp_custom3 );
        manual_mcp_custom4 = view.findViewById( R.id.manual_mcp_custom4 );
        manual_cib_power = view.findViewById( R.id.manual_cib_power );

        manual_rv_show.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );
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
            mPresenter = new ManualPresenter( this, mLight );
            mAdapter = new ManualSliderAdapter( getContext(), mLight );
            mAdapter.setListener( new ISeekbarListener() {
                @Override
                public void setBright( int position, int value )
                {
                    mPresenter.setBright( position, value );
                }
            } );
            mPresenter.addDataListener();
            manual_rv_show.setAdapter( mAdapter );
            refreshData();
        }
    }

    @Override
    protected void initEvent()
    {
        manual_mcp_custom1.setOnClickListener( this );
        manual_mcp_custom2.setOnClickListener( this );
        manual_mcp_custom3.setOnClickListener( this );
        manual_mcp_custom4.setOnClickListener( this );
        manual_cib_power.setOnClickListener( this );

        manual_mcp_custom1.setOnLongClickListener( this );
        manual_mcp_custom2.setOnLongClickListener( this );
        manual_mcp_custom3.setOnLongClickListener( this );
        manual_mcp_custom4.setOnLongClickListener( this );
    }

    @SuppressLint ( "RestrictedApi" )
    private void refreshData()
    {
        mAdapter.notifyDataSetChanged();
        int channelCount = mLight.getChannelCount();
        MultiCircleProgress[] views = new MultiCircleProgress[]{ manual_mcp_custom1,
                                                                 manual_mcp_custom2,
                                                                 manual_mcp_custom3,
                                                                 manual_mcp_custom4 };
        for ( int i = 0; i < 4; i++ )
        {
            views[i].setCircleCount( channelCount );
            byte[] array = mLight.getCustom( i );
            if ( array != null && array.length == channelCount )
            {
                for ( int j = 0; j < channelCount; j++ )
                {
                    views[i].setProgress( j, array[j] );
                }
            }
            for ( int k = 0; k < channelCount; k++ )
            {
                String color = mLight.getChannelName( k );
                views[i].setCircleColor( k, LightUtil.getColorValue( color ) );
            }
            views[i].invalidate();
        }
        manual_cib_power.setChecked( mLight.isPower() );
    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public void onClick( View view )
    {
        int[] progress;
        switch ( view.getId() )
        {
            case R.id.manual_mcp_custom1:
                progress = manual_mcp_custom1.getProgress();
                for ( int i = 0; i < progress.length; i++ )
                {
                    progress[i] *= 10;
                }
                mPresenter.setAllBrights( progress );
                break;
            case R.id.manual_mcp_custom2:
                progress = manual_mcp_custom2.getProgress();
                for ( int i = 0; i < progress.length; i++ )
                {
                    progress[i] *= 10;
                }
                mPresenter.setAllBrights( progress );
                break;
            case R.id.manual_mcp_custom3:
                progress = manual_mcp_custom3.getProgress();
                for ( int i = 0; i < progress.length; i++ )
                {
                    progress[i] *= 10;
                }
                mPresenter.setAllBrights( progress );
                break;
            case R.id.manual_mcp_custom4:
                progress = manual_mcp_custom4.getProgress();
                for ( int i = 0; i < progress.length; i++ )
                {
                    progress[i] *= 10;
                }
                mPresenter.setAllBrights( progress );
                break;
            case R.id.manual_cib_power:
                boolean b = !manual_cib_power.isChecked();
                mPresenter.setPower( b );
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
        Toast.makeText( getContext(), msg, Toast.LENGTH_SHORT )
             .show();
    }

    @Override
    public void runOnUIThread( Runnable runnable )
    {
        getActivity().runOnUiThread( runnable );
    }

    @Override
    public void showDataChanged()
    {
        refreshData();
    }

    @Override
    public void showSetDeviceDataPointError( String error )
    {

    }

    @Override
    public void showStartSetDeviceDataPoint()
    {

    }

    @Override
    public void showSetDeviceDataPointSuccess()
    {

    }

    @Override
    public boolean onLongClick( View view )
    {
        switch ( view.getId() )
        {
            case R.id.manual_mcp_custom1:
                mPresenter.setCustom( 0, mAdapter.getBrights() );
                break;
            case R.id.manual_mcp_custom2:
                mPresenter.setCustom( 1, mAdapter.getBrights() );
                break;
            case R.id.manual_mcp_custom3:
                mPresenter.setCustom( 2, mAdapter.getBrights() );
                break;
            case R.id.manual_mcp_custom4:
                mPresenter.setCustom( 3, mAdapter.getBrights() );
                break;
        }
        return true;
    }
}
