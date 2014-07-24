package com.cuckoo;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by chethan on 20/07/14.
 */
public class Utils {
    public  static Typeface getLightTypeface(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/segoeuil.ttf");
    }

    public  static Typeface getNormalTypeface(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/segoeui.ttf");
    }
}
