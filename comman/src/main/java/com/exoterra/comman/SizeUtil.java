package com.exoterra.comman;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by liruya on 2018/4/28.
 */

public class SizeUtil
{
    public static float dp2px( Context context, float dp )
    {
        if ( context != null )
        {
            return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics() );
        }
        return 0;
    }

    public static float sp2px( Context context, float sp )
    {
        if ( context != null )
        {
            return sp * context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
        }
        return 0;
    }

    public static float px2sp( Context context, float px )
    {
        if ( context != null )
        {
            return px / (float) context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
        }
        return 0;
    }
}
