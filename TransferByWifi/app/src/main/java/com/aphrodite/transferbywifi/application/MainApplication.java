package com.aphrodite.transferbywifi.application;

import com.aphrodite.transferbywifi.application.base.BaseApplication;

/**
 * Created by Aphrodite on 2018/11/6.
 */
public class MainApplication extends BaseApplication {
    private static MainApplication mainApplication;

    @Override
    protected void initSystem() {
        this.mainApplication = this;
    }

    public static MainApplication getApplication() {
        return mainApplication;
    }
}
