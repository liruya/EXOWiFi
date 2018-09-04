package com.exoterra.comman;

import android.content.Context;
import android.support.annotation.StringRes;

import java.lang.ref.WeakReference;

/**
 * Created by liruya on 2018/4/13.
 */

public class BasePresenter<T extends IBaseView>
{
    private WeakReference<T> mView;

    public BasePresenter ( T t )
    {
        mView = new WeakReference<>( t );
    }

    protected T getView()
    {
        return mView.get();
    }

    protected boolean isViewExist()
    {
        return mView.get() != null;
    }

    protected Context getContext()
    {
        if ( isViewExist() )
        {
            return mView.get().getMvpContext();
        }
        return null;
    }

    protected String getString( @StringRes int resid )
    {
        if ( getContext() != null )
        {
            return getContext().getString( resid );
        }
        return null;
    }
}
