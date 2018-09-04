package com.exoterra.exowifi.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.exoterra.comman.BaseActivity;
import com.exoterra.exowifi.R;
import com.exoterra.exowifi.adddevice.AddDeviceActivity;
import com.exoterra.exowifi.main.devicelist.DevicelistFragment;
import com.inledco.esptouch.EsptouchActivity;

import cn.xlink.sdk.v5.module.main.XLinkSDK;

public class MainActivity extends BaseActivity
{
    private MenuItem menu_main_esptouch;
    private MenuItem menu_main_add;
    private Toolbar main_toolbar;
    private BottomNavigationView main_bnv;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void getIntentData( @NonNull Intent intent )
    {

    }

    @Override
    protected void initView()
    {
        main_toolbar = findViewById( R.id.main_toolbar );
        main_bnv = findViewById( R.id.main_bnv );
        setSupportActionBar( main_toolbar );
    }

    @Override
    protected void initData()
    {
        XLinkSDK.start();
    }

    @Override
    protected void initEvent()
    {
        main_bnv.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( @NonNull MenuItem item )
            {
                switch ( item.getItemId() )
                {
                    case R.id.main_bnv_device:
                        getSupportFragmentManager().beginTransaction()
                                                   .replace( R.id.main_fl_show, new DevicelistFragment() )
                                                   .commit();
                        break;

                    case R.id.main_bnv_user:

                        break;
                }
                return true;
            }
        } );
        main_bnv.setSelectedItemId( R.id.main_bnv_device );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_main_top, menu );
        menu_main_esptouch = menu.findItem( R.id.menu_main_esptouch );
        menu_main_add = menu.findItem( R.id.menu_main_add );

        menu_main_esptouch.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick( MenuItem menuItem )
            {
                Intent intent = new Intent( MainActivity.this, EsptouchActivity.class );
                startActivity( intent );
                return false;
            }
        } );
        menu_main_add.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick( MenuItem menuItem )
            {
                Intent intent = new Intent( MainActivity.this, AddDeviceActivity.class );
                startActivity( intent );
                return false;
            }
        } );
        return super.onCreateOptionsMenu( menu );
    }
}
