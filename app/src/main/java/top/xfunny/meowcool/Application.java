package top.xfunny.meowcool;

import com.google.android.material.color.DynamicColors;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 全局启用动态配色
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
