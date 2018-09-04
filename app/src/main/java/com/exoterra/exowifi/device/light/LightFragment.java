package com.exoterra.exowifi.device.light;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.exoterra.comman.BaseFragment;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.bean.ExoLightStrip;
import com.exoterra.exowifi.device.light.auto.AutoFragment;
import com.exoterra.exowifi.device.light.manual.ManualFragment;
import com.exoterra.exowifi.device.light.pro.ProFragment;
import com.exoterra.exowifi.manager.DeviceManager;

import cn.xlink.sdk.v5.module.main.XLinkSDK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LightFragment extends BaseFragment implements ILightView
{
    private CheckableImageButton light_cib_manual;
    private CheckableImageButton light_cib_auto;
    private CheckedTextView light_ctv_pro;

    private ExoLightStrip mLight;
    private LightPresenter mPresenter;

    public static LightFragment newInstance( String mac )
    {
        LightFragment frag = new LightFragment();
        Bundle b = new Bundle();
        b.putString( Constant.KEY_MAC_ADDRESS, mac );
        frag.setArguments( b );
        return frag;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_light, container, false );

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
        light_cib_manual = view.findViewById( R.id.light_cib_manual );
        light_cib_auto = view.findViewById( R.id.light_cib_auto );
        light_ctv_pro = view.findViewById( R.id.light_ctv_pro );
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
            mLight = new ExoLightStrip( device.getXDevice() );
            mLight.setDataPointList( device.getDataPointList() );

            showDataChanged();

            mPresenter = new LightPresenter( this, mLight );
            mPresenter.addDeviceStateChangedListener();
            mPresenter.addDataChangedListener();
            mPresenter.getDeviceDataPoints();
        }

    }

    @Override
    protected void initEvent()
    {
        light_cib_manual.setOnClickListener( new View.OnClickListener() {
            @SuppressLint ( "RestrictedApi" )
            @Override
            public void onClick( View view )
            {
                if ( !light_cib_manual.isChecked() )
                {
                    mPresenter.setMode( ExoLightStrip.MODE_MANUAL );
                }
            }
        } );

        light_cib_auto.setOnClickListener( new View.OnClickListener() {
            @SuppressLint ( "RestrictedApi" )
            @Override
            public void onClick( View view )
            {
                if ( !light_cib_auto.isChecked() )
                {
                    mPresenter.setMode( ExoLightStrip.MODE_AUTO );
                }
            }
        } );

        light_ctv_pro.setOnClickListener( new View.OnClickListener() {
            @SuppressLint ( "RestrictedApi" )
            @Override
            public void onClick( View v )
            {
                if ( !light_ctv_pro.isChecked() )
                {
                    mPresenter.setMode( ExoLightStrip.MODE_PRO );
                }
            }
        } );
    }

    @Override
    public void showDeviceStateChanged()
    {

    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public void showDataChanged()
    {
        if ( mLight.getMode() == ExoLightStrip.MODE_MANUAL && !light_cib_manual.isChecked() )
        {
            light_cib_manual.setChecked( true );
            light_cib_auto.setChecked( false );
            light_ctv_pro.setChecked( false );
            getActivity().getSupportFragmentManager()
                         .beginTransaction()
                         .replace( R.id.light_fl_show, ManualFragment.newInstance( mLight.getXDevice().getMacAddress() ) )
                         .commit();
        }
        else if ( mLight.getMode() == ExoLightStrip.MODE_AUTO && !light_cib_auto.isChecked() )
        {
            light_cib_manual.setChecked( false );
            light_cib_auto.setChecked( true );
            light_ctv_pro.setChecked( false );
            getActivity().getSupportFragmentManager()
                         .beginTransaction()
                         .replace( R.id.light_fl_show, AutoFragment.newInstance( mLight.getXDevice().getMacAddress() ) )
                         .commit();
        }
        else if ( mLight.getMode() == ExoLightStrip.MODE_PRO && !light_ctv_pro.isChecked() )
        {
            light_cib_manual.setChecked( false );
            light_cib_auto.setChecked( false );
            light_ctv_pro.setChecked( true );
            getActivity().getSupportFragmentManager()
                         .beginTransaction()
                         .replace( R.id.light_fl_show, ProFragment.newInstance( mLight.getXDevice().getMacAddress() ) )
                         .commit();
        }
    }

    @Override
    public void showGetDeviceDataPointError( String error )
    {
        toast( error );
    }

    @Override
    public void showStartGetDeviceDataPoint()
    {

    }

    @Override
    public void showGetDeviceDataPointSuccess()
    {

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
        if ( getActivity() != null )
        {
            getActivity().runOnUiThread( runnable );
        }
    }
}
