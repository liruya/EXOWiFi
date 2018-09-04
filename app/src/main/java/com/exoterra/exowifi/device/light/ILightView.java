package com.exoterra.exowifi.device.light;

import com.exoterra.comman.IBaseView;

public interface ILightView extends IBaseView
{
    void showDeviceStateChanged();

    void showDataChanged();

    void showGetDeviceDataPointError( String error );

    void showStartGetDeviceDataPoint();

    void showGetDeviceDataPointSuccess();
}
