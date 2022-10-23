package com.unesell.lipari;

import android.app.Application;
import android.os.Build;
import com.google.android.material.color.DynamicColors;

public class GlobalSettingsApplication extends Application {
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
    }
}
