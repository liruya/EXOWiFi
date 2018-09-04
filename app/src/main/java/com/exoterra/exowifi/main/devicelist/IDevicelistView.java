package com.exoterra.exowifi.main.devicelist;

import com.exoterra.comman.IBaseView;

/**
 * Created by liruya on 2018/4/18.
 */

public interface IDevicelistView extends IBaseView
{
    void showDeviceStateChanged();

//    void startRefresh();

    void showRefreshSuccess();

    void showRefreshSubcribedDevicesError( String error );

    void startUnsubcribe();

    void showUnsubcribeSuccess();

    void showUnsubcribeDeviceError( String error );

}
