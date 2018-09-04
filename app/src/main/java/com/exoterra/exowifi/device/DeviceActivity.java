package com.exoterra.exowifi.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.exoterra.comman.BaseActivity;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.device.light.LightFragment;
import com.exoterra.exowifi.device.singlesocket.SingleSocketFragment;
import com.exoterra.exowifi.manager.DeviceManager;
import com.exoterra.exowifi.manager.DeviceUtil;

import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class DeviceActivity extends BaseActivity implements IDeviceView
{
    private Toolbar device_toolbar;
    private CheckableImageButton menu_control_cloud;
    private CheckableImageButton menu_control_local;

    private DevicePresenter mPresenter;
    private Device mDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_device );

        getIntentData( getIntent() );
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.removeDeviceStateChangedListener();
    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_control_top, menu );
        MenuItem menuItem = menu.findItem( R.id.menu_control_state );
        View menuView = menuItem.getActionView();
        menu_control_cloud = menuView.findViewById( R.id.menu_item_control_cloud );
        menu_control_local = menuView.findViewById( R.id.menu_item_control_local);
        menu_control_cloud.setChecked( mDevice.getXDevice().getCloudConnectionState() == XDevice.State.CONNECTED );
        menu_control_local.setChecked( mDevice.getXDevice().getLocalConnectionState() == XDevice.State.CONNECTED );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {
        if ( intent != null )
        {
            String mac = intent.getStringExtra( Constant.KEY_MAC_ADDRESS );
            mDevice = DeviceManager.getInstance().getDevice( mac );
            String pid = mDevice.getXDevice().getProductId();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if ( Constant.PRODUCT_EXOSTRIP_ID.equals( pid ) )
            {
                transaction.replace( R.id.device_fl_show, LightFragment.newInstance( mac ) )
                           .commit();
            }
            else if ( Constant.PRODUCT_EXOSOCKET_ID.equals( pid ) )
            {
                transaction.replace( R.id.device_fl_show, SingleSocketFragment.newInstance( mac ) )
                           .commit();
            }
        }
    }

    @Override
    protected void initView()
    {
        device_toolbar = findViewById( R.id.device_toolbar );

        if ( mDevice != null )
        {
            String name = mDevice.getXDevice().getDeviceName();
            String pid = mDevice.getXDevice().getProductId();
            device_toolbar.setTitle( TextUtils.isEmpty( name ) ? DeviceUtil.getDefaultName( pid ) : name );
        }
        setSupportActionBar( device_toolbar );
    }

    @Override
    protected void initData()
    {
        XLinkSDK.start();
        mPresenter = new DevicePresenter( this, mDevice );
        mPresenter.addDeviceStateChangedListener();
    }

    @Override
    protected void initEvent()
    {
        device_toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                finish();
            }
        } );
    }

    @Override
    public Context getMvpContext()
    {
        return this;
    }

    @Override
    public void toast( String msg )
    {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT )
             .show();
    }

    @Override
    public void runOnUIThread( Runnable runnable )
    {
        runOnUiThread( runnable );
    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public void showDeviceStateChanged()
    {
        if ( menu_control_cloud != null )
        {
            menu_control_cloud.setChecked( mDevice.getXDevice().getCloudConnectionState() == XDevice.State.CONNECTED );
        }
        if ( menu_control_local != null )
        {
            menu_control_local.setChecked( mDevice.getXDevice().getLocalConnectionState() == XDevice.State.CONNECTED );
        }
    }
}
