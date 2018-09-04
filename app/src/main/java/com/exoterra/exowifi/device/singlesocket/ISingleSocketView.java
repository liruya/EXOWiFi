package com.exoterra.exowifi.device.singlesocket;

import com.exoterra.comman.IBaseView;
import com.exoterra.exowifi.bean.SocketTimer;

public interface ISingleSocketView extends IBaseView
{
    void showDataChanged();

    void showTimerDialog( SocketTimer timer );
}
