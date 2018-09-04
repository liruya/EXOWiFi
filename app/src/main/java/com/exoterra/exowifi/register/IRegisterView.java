package com.exoterra.exowifi.register;

import com.exoterra.comman.IBaseView;

/**
 * Created by liruya on 2018/4/14.
 */

public interface IRegisterView extends IBaseView
{
    String getAccount();
    String getNickname();
    String getPassword();
    void showProgress();
    void dismissProgress();
    boolean isProgressShowing();
    void closeWithResult();
}
