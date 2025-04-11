package top.xfunny.meowcool.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;

public class BorderColorUtil {
    public static void setTextViewBorderColor(TextView textView, @ColorInt int color) {
        GradientDrawable drawable = getMutableDrawable(textView);
        if (drawable != null) {
            drawable.setStroke(dpToPx(textView.getContext(), 1), color);
            textView.setBackground(drawable);
        }
    }

    private static GradientDrawable getMutableDrawable(TextView textView) {
        Drawable background = textView.getBackground();
        if (background instanceof GradientDrawable) {
            return (GradientDrawable) background.mutate();
        }
        return null;
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}
