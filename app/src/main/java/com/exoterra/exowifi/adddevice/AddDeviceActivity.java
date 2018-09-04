package com.exoterra.exowifi.adddevice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.exoterra.comman.BaseActivity;
import com.exoterra.exowifi.Constant;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.manager.DeviceManager;

import cn.xlink.sdk.v5.model.XDevice;
import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class AddDeviceActivity extends BaseActivity implements IAddDeviceView
{
    private Toolbar adddevice_toolbar;
    private RecyclerView adddevice_rv_show;
    private Button adddevice_btn_add;
    private ProgressBar menu_item_scan_pb;
    private ToggleButton menu_item_scan_text;

    private ScanAdapter mAdapter;
    private AddDevicePresenter mPresenter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_device );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.stopScan();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_adddevice_top, menu );
        MenuItem menuItem = menu.findItem( R.id.menu_adddevice_scan );
        View menuView = menuItem.getActionView();
        menu_item_scan_pb = menuView.findViewById( R.id.menu_item_scan_pb );
        menu_item_scan_text = menuView.findViewById( R.id.menu_item_scan_text );
        menu_item_scan_text.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton compoundButton, boolean b )
            {
                if ( b )
                {
                    mPresenter.scanDevice( Constant.PRODUCT_EXOSTRIP_ID, Constant.PRODUCT_EXOSOCKET_ID );
                }
                else
                {
                    mPresenter.stopScan();
                }
            }
        } );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {

    }

    @Override
    protected void initView()
    {
        adddevice_toolbar = findViewById( R.id.adddevice_toolbar );
        adddevice_rv_show = findViewById( R.id.adddevice_rv_show );
        adddevice_btn_add = findViewById( R.id.adddevice_btn_add );

        setSupportActionBar( adddevice_toolbar );
        adddevice_rv_show.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false ) );
        adddevice_rv_show.addItemDecoration( new DividerItemDecoration( this, DividerItemDecoration.VERTICAL ) );
    }

    @Override
    protected void initData()
    {
        XLinkSDK.start();
        mPresenter = new AddDevicePresenter( this );
        mAdapter = new ScanAdapter( this, DeviceManager.getInstance().getAllDeviceAddress(), mPresenter.getScannedDevices() );

        adddevice_rv_show.setAdapter( mAdapter );
    }

    @Override
    protected void initEvent()
    {
        adddevice_toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                finish();
            }
        } );

        adddevice_btn_add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                mPresenter.registerAndSubcribeDevices( mAdapter.getSelectedDevices() );
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

    @Override
    public void showStartScan()
    {
        menu_item_scan_pb.setVisibility( View.VISIBLE );
        menu_item_scan_text.setChecked( true );
    }

    @Override
    public void showCompleteScan()
    {
        menu_item_scan_pb.setVisibility( View.GONE );
        menu_item_scan_text.setChecked( false );
    }

    @Override
    public void updateScannedDevice()
    {
        mAdapter.notifyItemInserted( mPresenter.getScannedDevices().size() - 1 );
    }

    @Override
    public void clearScannedDevice()
    {
        mAdapter.setSubcribeAddress( null );
        mAdapter.getSelectedDevices().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRegisterDeviceSuccess( XDevice device )
    {
        toast( device.getMacAddress() + " 注册成功." );
    }

    @Override
    public void showRegisterDeviceFailed( XDevice device, String error )
    {
        toast( device.getMacAddress() + " 注册失败: " + error );
    }

    @Override
    public void showStartSubcribeDevice( XDevice device )
    {
        mAdapter.setSubcribeAddress( device.getMacAddress() );
    }

    @Override
    public void showSubcribeSuccess( XDevice device )
    {
        mAdapter.setSubcribeAddress( null );
    }

    @Override
    public void showSubcribeFailed( XDevice device, String error )
    {
        mAdapter.setSubcribeAddress( null );
        toast( error );
    }

    @Override
    public void showSubcribeComplete()
    {

    }
}
