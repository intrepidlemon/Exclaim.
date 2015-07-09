package com.iantoxi.prg02;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by linxi on 7/8/15.
 */
public class SerifTextView extends TextView {
    public SerifTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/goudy.ttf"));
    }
}
