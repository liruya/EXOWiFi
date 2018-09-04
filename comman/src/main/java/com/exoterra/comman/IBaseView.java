package com.exoterra.comman;

import android.content.Context;

/**
 * Created by liruya on 2018/4/16.
 */

public interface IBaseView
{
    Context getMvpContext();

    void toast( String msg );

    void runOnUIThread( Runnable runnable );
}
