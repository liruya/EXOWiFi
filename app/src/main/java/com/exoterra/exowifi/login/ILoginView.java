package com.exoterra.exowifi.login;

import com.exoterra.comman.IBaseView;

/**
 * Created by liruya on 2018/4/14.
 */

public interface ILoginView extends IBaseView
{
    void setAccount( String account );
    void setPassword( String password );
    String getAccount();
    String getPassword();
    void showProgress();
    void dismissProgress();
    boolean isProgressShowing();
    void toMainActivity();
    void toRegisterActivity();
}
