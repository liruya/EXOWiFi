package com.exoterra.exowifi.main.devicelist;

import android.support.annotation.IdRes;

/**
 * Created by liruya on 2018/4/18.
 */

public interface SwipeItemClickListener
{
    void onClickContent( int position );

    void onClickAction( int position, @IdRes int resid );
}
