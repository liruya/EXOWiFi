package com.exoterra.exowifi.main.devicelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.exoterra.exowifi.R;
import com.exoterra.exowifi.bean.Device;
import com.exoterra.exowifi.manager.DeviceUtil;
import com.inledco.itemtouchhelperextension.SwipeItemViewHolder;

import java.util.List;

import cn.xlink.sdk.v5.model.XDevice;

/**
 * Created by liruya on 2018/4/18.
 */

public class DevicelistAdapter extends RecyclerView.Adapter<DevicelistAdapter.DeviceViewHolder>
{
    private Context mContext;
    private List<Device> mDevices;
    private SwipeItemClickListener mListener;

    public DevicelistAdapter( Context context, List< Device > devices )
    {
        mContext = context;
        mDevices = devices;
    }

    public void setListener( SwipeItemClickListener listener )
    {
        mListener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
        DeviceViewHolder holder = new DeviceViewHolder( LayoutInflater.from( mContext )
                                                                      .inflate( R.layout.item_device_action, parent, false ) );
        return holder;
    }

    @SuppressLint ( "RestrictedApi" )
    @Override
    public void onBindViewHolder( final DeviceViewHolder holder, int position )
    {
        Device device = mDevices.get( holder.getAdapterPosition() );
        String pid = device.getXDevice().getProductId();
        holder.iv_icon.setImageResource( DeviceUtil.getProductIcon( pid ) );
        String name = device.getXDevice().getDeviceName();
        if ( TextUtils.isEmpty( name ) )
        {
            holder.tv_name.setText( DeviceUtil.getDefaultName( pid ) );
        }
        else
        {
            holder.tv_name.setText( name );
        }
        holder.tv_mac.setText( device.getXDevice().getMacAddress() );
        holder.tv_type.setText( DeviceUtil.getProductType( pid ) );
        holder.cib_cloud.setChecked( device.getXDevice().getCloudConnectionState() == XDevice.State.CONNECTED );
        holder.cib_local.setChecked( device.getXDevice().getLocalConnectionState() == XDevice.State.CONNECTED );
        holder.item_content.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                if ( mListener != null )
                {
                    mListener.onClickContent( holder.getAdapterPosition() );
                }
            }
        } );

        holder.iv_remove.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                if ( mListener != null )
                {
                    mListener.onClickAction( holder.getAdapterPosition(), view.getId() );
                }
            }
        } );
    }

    @Override
    public int getItemCount()
    {
        return mDevices == null ? 0 : mDevices.size();
    }

    public Device getItem( int position )
    {
        if ( position >= 0 && position < getItemCount() )
        {
            return mDevices.get( position );
        }
        return null;
    }

    public class DeviceViewHolder extends SwipeItemViewHolder
    {
        private View item_content;
        private View item_action;
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_mac;
        private TextView tv_type;
        private CheckableImageButton cib_cloud;
        private CheckableImageButton cib_local;
        private ImageButton iv_remove;

        public DeviceViewHolder( View itemView )
        {
            super( itemView );
            item_content = itemView.findViewById( R.id.item_device_content );
            item_action = itemView.findViewById( R.id.item_device_action );
            iv_icon = itemView.findViewById( R.id.item_device_icon );
            tv_name = itemView.findViewById( R.id.item_device_name );
            tv_mac = itemView.findViewById( R.id.item_device_mac );
            tv_type = itemView.findViewById( R.id.item_device_type );
            cib_cloud = itemView.findViewById( R.id.item_device_cloud );
            cib_local = itemView.findViewById( R.id.item_device_local );
            iv_remove = itemView.findViewById( R.id.item_device_remove );
        }

        @Override
        public float getActionWidth()
        {
            return item_action.getWidth();
        }

        @Override
        public View getContentView()
        {
            return item_content;
        }
    }
}
