package com.exoterra.exowifi.device.light.manual;

import com.exoterra.comman.IBaseView;

/**
 * Created by liruya on 2018/4/24.
 */

public interface IManualView extends IBaseView
{
    void showDataChanged();

    void showSetDeviceDataPointError( String error );

    void showStartSetDeviceDataPoint();

    void showSetDeviceDataPointSuccess();
}
