package com.exoterra.exowifi.adddevice;

import com.exoterra.comman.IBaseView;

import cn.xlink.sdk.v5.model.XDevice;

/**
 * Created by liruya on 2018/4/17.
 */

public interface IAddDeviceView extends IBaseView
{
    void showStartScan();

    void showCompleteScan();

    void updateScannedDevice();

    void clearScannedDevice();

    void showRegisterDeviceSuccess( XDevice device );

    void showRegisterDeviceFailed( XDevice device, String error );

    void showStartSubcribeDevice( XDevice device );

    void showSubcribeSuccess( XDevice device );

    void showSubcribeFailed( XDevice device, String error );

    void showSubcribeComplete();
}
