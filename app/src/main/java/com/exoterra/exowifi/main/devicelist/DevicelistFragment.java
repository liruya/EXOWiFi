package com.exoterra.exowifi.main.devicelist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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
import com.exoterra.exowifi.device.DeviceActivity;
import com.exoterra.exowifi.manager.DeviceManager;
import com.inledco.itemtouchhelperextension.ItemTouchHelperCallback;
import com.inledco.itemtouchhelperextension.ItemTouchHelperExtension;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.sdk.v5.model.XDevice;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevicelistFragment extends BaseFragment implements IDevicelistView
{
    private SwipeRefreshLayout device_swipe_refresh;
    private RecyclerView device_rv_show;

    private DevicelistPresenter mPresenter;
    private DevicelistAdapter mAdapter;
    private List<Device> mDevices;

    private ItemTouchHelperCallback mItemTouchHelperCallback;
    private ItemTouchHelperExtension mItemTouchHelperExtension;



    public DevicelistFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_device, container, false );

        initView( view );
        initData();
        initEvent();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mPresenter.removeDeviceStateChangedListener();
    }

    @Override
    protected void initView( View view )
    {
        device_swipe_refresh = view.findViewById( R.id.device_swipe_refresh );
        device_rv_show = view.findViewById( R.id.device_rv_show );

        device_rv_show.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );
        device_rv_show.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ) );
    }

    @Override
    protected void initData()
    {
        mPresenter = new DevicelistPresenter( this );
        mDevices = new ArrayList<>();
        mDevices.addAll( DeviceManager.getInstance().getAllDevices() );
        mAdapter = new DevicelistAdapter( getContext(), mDevices );
        device_rv_show.setAdapter( mAdapter );
        mItemTouchHelperCallback = new ItemTouchHelperCallback();
        mItemTouchHelperExtension = new ItemTouchHelperExtension( mItemTouchHelperCallback );
        mItemTouchHelperExtension.attachToRecyclerView( device_rv_show );
        mPresenter.addDeviceStateChangedListener();
        mPresenter.refreshSubcribedDevices();
        device_swipe_refresh.setRefreshing( true );
    }

    @Override
    protected void initEvent()
    {
        device_swipe_refresh.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                mPresenter.refreshSubcribedDevices();
            }
        } );

        mAdapter.setListener( new SwipeItemClickListener() {
            @Override
            public void onClickContent( int position )
            {
                Device device = mAdapter.getItem( position );
                if ( device == null )
                {
                    return;
                }
                if ( device.getXDevice().getCloudConnectionState() == XDevice.State.CONNECTED
                     || device.getXDevice().getLocalConnectionState() == XDevice.State.CONNECTED )
                {
                    Intent intent = new Intent( getContext(), DeviceActivity.class );
                    intent.putExtra( Constant.KEY_MAC_ADDRESS,
                                     device.getXDevice()
                                           .getMacAddress() );
                    startActivity( intent );
                }
            }

            @Override
            public void onClickAction( int position, int resid )
            {
                Device device = mAdapter.getItem( position );
                if ( device == null )
                {
                    return;
                }
                switch ( resid )
                {
                    case R.id.item_device_remove:
                        mPresenter.unsubcribeDevice( device.getXDevice() );
                        break;
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
        Toast.makeText( getContext(), msg, Toast.LENGTH_SHORT )
             .show();
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

//    @Override
//    public void startRefresh()
//    {
//        device_swipe_refresh.setRefreshing( true );
//    }

    @Override
    public void showDeviceStateChanged()
    {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRefreshSuccess()
    {
        mDevices.clear();
        mDevices.addAll( DeviceManager.getInstance().getAllDevices() );
        mAdapter.notifyDataSetChanged();
        device_swipe_refresh.setRefreshing( false );
    }

    @Override
    public void showRefreshSubcribedDevicesError( String error )
    {
        device_swipe_refresh.setRefreshing( false );
    }

    @Override
    public void startUnsubcribe()
    {

    }

    @Override
    public void showUnsubcribeSuccess()
    {
        mDevices.clear();
        mDevices.addAll( DeviceManager.getInstance().getAllDevices() );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showUnsubcribeDeviceError( String error )
    {

    }
}
