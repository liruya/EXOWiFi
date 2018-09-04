package com.exoterra.exowifi.adddevice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exoterra.exowifi.R;
import com.exoterra.exowifi.manager.DeviceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.xlink.sdk.v5.model.XDevice;

/**
 * Created by liruya on 2018/4/17.
 */

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ScanViewHolder>
{
    private Context mContext;
    private Set<String> mSubcribedMacs;
    private List<XDevice> mXDevices;
    private List<XDevice> mSelectedDevices;
    private String mSubcribeAddress;

    public ScanAdapter( Context context, Set< String > subcribedMacs, List< XDevice > XDevices )
    {
        mContext = context;
        mSubcribedMacs = subcribedMacs;
        mXDevices = XDevices;
        mSelectedDevices = new ArrayList<>();
        mSubcribeAddress = null;
    }

    private boolean isSubcribed( XDevice xDevice )
    {
        if ( mSubcribedMacs == null || mSubcribedMacs.size() == 0 )
        {
            return false;
        }
        return mSubcribedMacs.contains( xDevice.getMacAddress() );
    }

    public List<XDevice> getSelectedDevices()
    {
        return mSelectedDevices;
    }

    public void setSubcribeAddress( String mac )
    {
        mSubcribeAddress = mac;
        notifyDataSetChanged();
    }

    @Override
    public ScanViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
        ScanViewHolder holder = new ScanViewHolder( LayoutInflater.from( mContext )
                                                                  .inflate( R.layout.item_scan, parent, false ) );
        return holder;
    }

    @Override
    public void onBindViewHolder( ScanViewHolder holder, int position )
    {
        final XDevice xDevice = mXDevices.get( holder.getAdapterPosition() );
        String pid = xDevice.getProductId();
        holder.iv_icon.setImageResource( DeviceUtil.getProductIcon( pid ) );
        String name = xDevice.getDeviceName();
        if ( TextUtils.isEmpty( name ) )
        {
            holder.tv_name.setText( DeviceUtil.getDefaultName( pid ) );
        }
        else
        {
            holder.tv_name.setText( name );
        }
        holder.tv_mac.setText( xDevice.getMacAddress() );
        holder.tv_type.setText( xDevice.getProductId() );
        holder.cb_select.setEnabled( !isSubcribed( xDevice ) );
        holder.cb_select.setChecked( isSubcribed( xDevice ) || mSelectedDevices.contains( xDevice ) );
        holder.progress.setVisibility( xDevice.getMacAddress().equals( mSubcribeAddress ) ? View.VISIBLE : View.INVISIBLE );
        holder.cb_select.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton compoundButton, boolean b )
            {
                if ( !isSubcribed( xDevice ) )
                {
                    if ( b )
                    {
                        mSelectedDevices.add( xDevice );
                    }
                    else
                    {
                        if ( mSelectedDevices.contains( xDevice ) )
                        {
                            mSelectedDevices.remove( xDevice );
                        }
                    }
                }
            }
        } );
    }

    @Override
    public int getItemCount()
    {
        return mXDevices == null ? 0 : mXDevices.size();
    }

    public class ScanViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_mac;
        private TextView tv_type;
        private CheckBox cb_select;
        private ProgressBar progress;

        public ScanViewHolder( View itemView )
        {
            super( itemView );
            iv_icon = itemView.findViewById( R.id.item_scan_icon );
            tv_name = itemView.findViewById( R.id.item_scan_name );
            tv_mac = itemView.findViewById( R.id.item_scan_mac );
            tv_type = itemView.findViewById( R.id.item_scan_type );
            cb_select = itemView.findViewById( R.id.item_scan_select );
            progress = itemView.findViewById( R.id.item_scan_pb );
        }
    }
}
