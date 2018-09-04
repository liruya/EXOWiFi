package com.exoterra.exowifi.device.light.auto;

import com.exoterra.comman.IBaseView;

/**
 * Created by liruya on 2018/4/26.
 */

public interface IAutoView extends IBaseView
{
    void showDataChanged();

    void showEditSunriseDialog();

    void showEditDaylightDialog();

    void showEditSunsetDialog();

    void showEditNightDialog();

    void showEditTurnoffDialog();

    void showPreviewUpdate( int tm );

    void showPreviewStopped();
}
