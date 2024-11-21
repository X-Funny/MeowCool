package top.xfunny.meowcool.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Dp2px {





    public static int dpToPx(int dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f);
    }
}
