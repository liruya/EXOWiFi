package com.exoterra.exowifi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.exoterra.comman.SizeUtil;

/**
 * Created by liruya on 2018/4/28.
 */

public class SeekbarIndicator extends View
{
    public static final int TYPE_NONE = 0;
    public static final int TYPE_DROPWATER = 1;
    public static final int TYPE_RECTANGLE = 2;
    public static final int TYPE_ROUND_RECTANGLE = 3;

    @IntDef({ TYPE_NONE, TYPE_DROPWATER, TYPE_RECTANGLE, TYPE_ROUND_RECTANGLE })
    public @interface Type
    {

    }

    private Context mContext;
    private float mIndicatorWidth;
    private float mIndicatorHeight;
    private int mIndicatorColor;
    private int mTextColor;
    private int mTextSize;
    private String mText;
    private String mMaxLengthText;

    private int mMinWidth;

    private Paint mPaint;

    @Type
    private int mType;

    public SeekbarIndicator( Context context )
    {
        this( context, (AttributeSet) null );
    }

    public SeekbarIndicator( Context context, @Nullable AttributeSet attrs )
    {
        this( context, attrs, 0 );
    }

    public SeekbarIndicator( Context context, @Nullable AttributeSet attrs, int defStyleAttr )
    {
        super( context, attrs, defStyleAttr );
        mContext = context;
        init( "100%" );
    }

    public SeekbarIndicator( Context context, String maxLengthText )
    {
        super( context, null, 0 );
        mContext = context;
        init( maxLengthText );
    }

    private void init( String maxLengthText )
    {
        mMaxLengthText = maxLengthText;
        mMinWidth = (int) SizeUtil.dp2px( mContext, 36 );
        mPaint = new Paint();
        mPaint.setAntiAlias( true );
        mPaint.setStrokeWidth( 1 );
        mPaint.setTextAlign( Paint.Align.CENTER );
    }

    private void drawDropwaterIndicator( Canvas canvas )
    {
        Path path = new Path();
        RectF rectF = new RectF( 0, 0, mIndicatorWidth, mIndicatorHeight );
        path.arcTo( rectF, 135, 270 );
        path.lineTo( mIndicatorWidth/2, mIndicatorHeight );
        path.close();
        canvas.drawPath( path, mPaint );
    }

    private void drawRectangleIndicator( Canvas canvas )
    {
        float dpSize = SizeUtil.dp2px( mContext, 1 );
        float rectHeight = mIndicatorHeight - 2 * dpSize;
        canvas.drawRect( 0, 0, mIndicatorWidth, rectHeight, mPaint );
        Path path = new Path();
        path.moveTo( mIndicatorWidth/2 - dpSize, rectHeight );
        path.lineTo( mIndicatorWidth/2, mIndicatorHeight );
        path.lineTo( mIndicatorWidth/2 + dpSize, rectHeight );
        path.close();
        canvas.drawPath( path, mPaint );
    }

    private void drawRoundRectangleIndicator( Canvas canvas )
    {
        float dpSize = SizeUtil.dp2px( mContext, 1 );
        float rectHeight = mIndicatorHeight - 2 * dpSize;
        float min = mIndicatorWidth < mIndicatorHeight ? mIndicatorWidth : mIndicatorHeight;
        float r = min/4;
        if ( r < dpSize )
        {
            r = 0;
        }
        else if ( r > 8*dpSize )
        {
            r = 8*dpSize;
        }
        Path path = new Path();
        RectF rectF = new RectF( 0, 0, 2*r, 2*r );
        path.arcTo( rectF, 180, 90 );
        path.moveTo( r, 0 );
        path.lineTo( mIndicatorWidth - r, 0 );
        rectF.set( mIndicatorWidth - 2*r, 0, mIndicatorWidth, 2*r );
        path.arcTo( rectF, 270, 90 );
        path.moveTo( mIndicatorWidth, r );
        path.lineTo( mIndicatorWidth, mIndicatorHeight - r );
        rectF.set( mIndicatorWidth - 2*r, mIndicatorHeight - 2*r, mIndicatorWidth, mIndicatorHeight );
        path.arcTo( rectF, 0, 90 );
        path.moveTo( mIndicatorWidth - r, mIndicatorHeight );
        path.lineTo( r, mIndicatorHeight );
        rectF.set( 0, mIndicatorHeight - 2*r, 2*r, mIndicatorHeight );
        path.arcTo( rectF, 90, 90 );
        path.moveTo( mIndicatorWidth/2 - dpSize, rectHeight );
        path.lineTo( mIndicatorWidth/2, mIndicatorHeight );
        path.lineTo( mIndicatorWidth/2 + dpSize, rectHeight );
        path.close();
        canvas.drawPath( path, mPaint );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        setMeasuredDimension( (int) mIndicatorWidth, (int) mIndicatorHeight );
    }

    @Override
    protected void onDraw( Canvas canvas )
    {
        super.onDraw( canvas );

        float baseline = 0;
        mPaint.setTextSize( mTextSize );
        Rect rect = new Rect();
        mPaint.getTextBounds( mMaxLengthText, 0, mMaxLengthText.length(), rect );
        mIndicatorWidth = rect.width() + SizeUtil.dp2px( mContext, 4 );
        if ( mIndicatorWidth < mMinWidth )
        {
            mIndicatorWidth = mMinWidth;
        }

        mPaint.setColor( mIndicatorColor );
        switch ( mType )
        {
            case TYPE_NONE:
                break;
            case TYPE_DROPWATER:
                mIndicatorHeight =  mIndicatorWidth * 1.2f;
                baseline = mIndicatorHeight/2.0f + rect.height()/4;
                drawDropwaterIndicator( canvas );
                break;
            case TYPE_RECTANGLE:
                mIndicatorHeight = rect.height() + SizeUtil.dp2px( mContext, 6 );
                baseline = ( mIndicatorHeight - SizeUtil.dp2px( mContext, 2 ) )/2 + rect.height()/4;
                drawRectangleIndicator( canvas );
                break;
            case TYPE_ROUND_RECTANGLE:
                mIndicatorHeight = rect.height() + SizeUtil.dp2px( mContext, 6 );
                baseline = ( mIndicatorHeight - SizeUtil.dp2px( mContext, 2 ) )/2 + rect.height()/4;
                drawRoundRectangleIndicator( canvas );
                break;
        }

        String text = mText;
        if ( mText.length() > mMaxLengthText.length() )
        {
            text = mText.substring( 0, mMaxLengthText.length() );
        }
        mPaint.setColor( mTextColor );
        canvas.drawText( text, mIndicatorWidth/2.0f, baseline, mPaint );
    }

    public synchronized int getIndicatorColor()
    {
        return mIndicatorColor;
    }

    public synchronized void setIndicatorColor( int indicatorColor )
    {
        mIndicatorColor = indicatorColor;
        invalidate();
    }

    public synchronized int getTextColor()
    {
        return mTextColor;
    }

    public synchronized void setTextColor( int textColor )
    {
        mTextColor = textColor;
        invalidate();
    }

    public synchronized int getTextSize()
    {
        return mTextSize;
    }

    public synchronized void setTextSize( int textSize )
    {
        mTextSize = textSize;
        invalidate();
    }

    public synchronized String getText()
    {
        return mText;
    }

    public synchronized void setText( String text )
    {
        mText = text;
        invalidate();
    }

    @Type
    public synchronized int getType()
    {
        return mType;
    }

    public synchronized void setType( @Type int type )
    {
        mType = type;
        invalidate();
    }
}
