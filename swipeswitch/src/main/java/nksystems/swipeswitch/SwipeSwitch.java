package nksystems.swipeswitch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SwipeSwitch extends View implements View.OnTouchListener {

    private final String TAG = "SwipeSwitch";
    Bundle attributes;
    RectF pill;
    RectF pillDrawableRect;
    Paint pillPaint;
    TextPaint pillSubTextPaint, pillMainTextPaint, contentSubTextPaint, contentMainTextPaint;
    Bitmap pillDrawable;
    Float right = 0.0f;
    Float minRight = 0.0f;
    Float imageOffset = 0.0f;
    Context mContext;
    boolean isSwipeEnabled;
    int drawableDimen = 0;
    String subText, mainText, contentSubText, contentMaintext;
    Float textOffset = 0.0f;
    Float contentTextOffset = 0.0f;
    SwipeSwitchStateListener listener;
    Float swipeThreshold = 0.0f;
    Float drawableOffset = 0.0f;

    // Attribute keys
    private final String BACKGROUND_COLOR_KEY = "backgroundColor";
    private final String PILL_COLOR_KEY = "pillColor";
    private final String PILL_DRAWABLE_KEY = "pillDrawable";
    private final String PILL_DRAWABLE_COLOR_KEY = "pillDrawableTint";
    private final String PILL_OFFSET_KEY = "pillOffset";
    private final String PILL_RADIUS = "pillRadius";

    private final String PILL_SUBTEXT_COLOR_KEY = "pillSubTextColor";
    private final String PILL_SUBTEXT_SIZE_KEY = "pillSubTextSize";
    private final String PILL_SUBTEXT_STYLE_KEY = "pillSubTextStyle";

    private final String PILL_MAINTEXT_COLOR_KEY = "pillMainTextColor";
    private final String PILL_MAINTEXT_SIZE_KEY = "pillMainTextSize";
    private final String PILL_MAINTEXT_STYLE_KEY = "pillMainTextStyle";

    private final String CONTENT_SUBTEXT_COLOR_KEY = "contentSubTextColor";
    private final String CONTENT_SUBTEXT_SIZE_KEY = "contentSubTextSize";
    private final String CONTENT_SUBTEXT_STYLE_KEY = "contentSubTextStyle";

    private final String CONTENT_MAINTEXT_COLOR_KEY = "contentMainTextColor";
    private final String CONTENT_MAINTEXT_SIZE_KEY = "contentMainTextSize";
    private final String CONTENT_MAINTEXT_STYLE_KEY = "contentMainTextStyle";

    public SwipeSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributeSet = null;
        if ( attrs != null ) {
            attributeSet = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwipeSwitch, 0 ,0);
        }
        attributes = new Bundle();
        pill = new RectF();
        pillDrawableRect = new RectF();
        mContext = context;
        setOnTouchListener(this);
        if ( attributeSet != null ) {
            try {
                attributes.putString(BACKGROUND_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_backgroundColor));
                attributes.putString(PILL_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_pillColor));
                attributes.putInt(PILL_DRAWABLE_KEY, attributeSet.getResourceId(R.styleable.SwipeSwitch_pillDrawable, 0));
                attributes.putString(PILL_DRAWABLE_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_pillDrawableTint));
                attributes.putFloat(PILL_OFFSET_KEY, attributeSet.getFloat(R.styleable.SwipeSwitch_pillOffset, 0.5f));
                attributes.putFloat(PILL_RADIUS, attributeSet.getFloat(R.styleable.SwipeSwitch_pillRadius, 0.5f));
                swipeThreshold = attributeSet.getFloat(R.styleable.SwipeSwitch_swipeThreshold, 0.9f);
                drawableOffset = attributeSet.getFloat(R.styleable.SwipeSwitch_drawableOffset, 40f);

                subText = attributeSet.getString(R.styleable.SwipeSwitch_pillSubtext);
                attributes.putString(PILL_SUBTEXT_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_pillSubTextColor));
                attributes.putFloat(PILL_SUBTEXT_SIZE_KEY, attributeSet.getDimensionPixelSize(R.styleable.SwipeSwitch_pillSubTextSize, 20));
                attributes.putInt(PILL_SUBTEXT_STYLE_KEY, attributeSet.getInt(R.styleable.SwipeSwitch_pillSubTextStyle, 0));

                mainText = attributeSet.getString(R.styleable.SwipeSwitch_pillMainText);
                attributes.putString(PILL_MAINTEXT_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_pillMainTextColor));
                attributes.putFloat(PILL_MAINTEXT_SIZE_KEY, attributeSet.getDimensionPixelSize(R.styleable.SwipeSwitch_pillMainTextSize, 20));
                attributes.putInt(PILL_MAINTEXT_STYLE_KEY, attributeSet.getInt(R.styleable.SwipeSwitch_pillMainTextStyle, 0));

                contentSubText = attributeSet.getString(R.styleable.SwipeSwitch_contentSubtext);
                attributes.putString(CONTENT_SUBTEXT_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_contentSubTextColor));
                attributes.putFloat(CONTENT_SUBTEXT_SIZE_KEY, attributeSet.getDimensionPixelSize(R.styleable.SwipeSwitch_contentSubTextSize, 20));
                attributes.putInt(CONTENT_SUBTEXT_STYLE_KEY, attributeSet.getInt(R.styleable.SwipeSwitch_contentSubTextStyle, 0));

                contentMaintext = attributeSet.getString(R.styleable.SwipeSwitch_contentMainText);
                attributes.putString(CONTENT_MAINTEXT_COLOR_KEY, attributeSet.getString(R.styleable.SwipeSwitch_contentMainTextColor));
                attributes.putFloat(CONTENT_MAINTEXT_SIZE_KEY, attributeSet.getDimensionPixelSize(R.styleable.SwipeSwitch_contentMainTextSize, 20));
                attributes.putInt(CONTENT_MAINTEXT_STYLE_KEY, attributeSet.getInt(R.styleable.SwipeSwitch_contentMainTextStyle, 0));

                drawableDimen = attributeSet.getInt(R.styleable.SwipeSwitch_pillImageDimen, 50);
            }finally {
                attributeSet.recycle();
            }

            init();
        }
    }

    private void init() {
        pillPaint = new Paint();
        pillPaint.setColor(Color.parseColor(attributes.getString(PILL_COLOR_KEY)));
        pillPaint.setStrokeWidth(1);
        pillPaint.setStyle(Paint.Style.FILL);

        if ( subText != null ) {
            pillSubTextPaint = new TextPaint();
            String subTextColor = attributes.getString(PILL_SUBTEXT_COLOR_KEY) == null ? "#FFFFFF" : attributes.getString(PILL_SUBTEXT_COLOR_KEY);
            pillSubTextPaint.setColor(Color.parseColor(subTextColor));
            pillSubTextPaint.setTextSize(attributes.getFloat(PILL_SUBTEXT_SIZE_KEY));
            if ( attributes.getInt(PILL_SUBTEXT_STYLE_KEY) == 1 ) {
                pillSubTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            }

        }

        if ( mainText != null ) {
            pillMainTextPaint = new TextPaint();
            String subTextColor = attributes.getString(PILL_MAINTEXT_COLOR_KEY) == null ? "#FFFFFF" : attributes.getString(PILL_MAINTEXT_COLOR_KEY);
            pillMainTextPaint.setColor(Color.parseColor(subTextColor));
            pillMainTextPaint.setTextSize(attributes.getFloat(PILL_MAINTEXT_SIZE_KEY));
            if ( attributes.getInt(PILL_MAINTEXT_STYLE_KEY) == 1 ) {
                pillMainTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            }

        }

        if ( contentSubText != null ) {
            contentSubTextPaint = new TextPaint();
            String subTextColor = attributes.getString(CONTENT_SUBTEXT_COLOR_KEY) == null ? "#FFFFFF" : attributes.getString(CONTENT_SUBTEXT_COLOR_KEY);
            contentSubTextPaint.setColor(Color.parseColor(subTextColor));
            contentSubTextPaint.setTextSize(attributes.getFloat(CONTENT_SUBTEXT_SIZE_KEY));
            if ( attributes.getInt(CONTENT_SUBTEXT_STYLE_KEY) == 1 ) {
                contentSubTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            }

        }

        if ( contentMaintext != null ) {
            contentMainTextPaint = new TextPaint();
            String subTextColor = attributes.getString(CONTENT_MAINTEXT_COLOR_KEY) == null ? "#FFFFFF" : attributes.getString(CONTENT_MAINTEXT_COLOR_KEY);
            contentMainTextPaint.setColor(Color.parseColor(subTextColor));
            contentMainTextPaint.setTextSize(attributes.getFloat(CONTENT_MAINTEXT_SIZE_KEY));
            if ( attributes.getInt(CONTENT_MAINTEXT_STYLE_KEY) == 1 ) {
                contentMainTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            }

        }

        if ( subText != null && mainText != null )
            textOffset = 10f;

        if ( contentSubText != null && contentMaintext != null )
            contentTextOffset = 10f;

        pillDrawable = getBitmapFromDrawable(getResources().getDrawable(attributes.getInt(PILL_DRAWABLE_KEY)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( right == 0.0f ) {
            right = getX() + getWidth() * attributes.getFloat(PILL_OFFSET_KEY);
            minRight = right;
        }
        imageOffset = right - drawableOffset;
        // canvas fill
        canvas.drawColor(Color.parseColor(attributes.getString(BACKGROUND_COLOR_KEY)));

        // content block
        if ( contentSubText != null ) {
            canvas.drawText(contentSubText, getX() + getWidth() * 0.75f - contentSubTextPaint.measureText(contentSubText) / 2, getY() + getHeight() / 2 - attributes.getFloat(CONTENT_SUBTEXT_SIZE_KEY) / 2 - contentTextOffset,contentSubTextPaint);
        }

        if ( contentMaintext != null ) {
            canvas.drawText(contentMaintext, getX() + getWidth() * 0.75f - contentMainTextPaint.measureText(contentMaintext) / 2, getY() + getHeight() / 2 + attributes.getFloat(CONTENT_MAINTEXT_SIZE_KEY) / 2 + contentTextOffset,contentMainTextPaint);
        }
        // end content block

        // pill block
        pill.set((getX() + getWidth() * attributes.getFloat(PILL_OFFSET_KEY)) * -1,0,right,getY() + getHeight());
        pillDrawableRect.set(imageOffset - drawableDimen, getY() + getHeight() / 2 - drawableDimen / 2,imageOffset, getY() + getHeight() / 2 + drawableDimen / 2);
        canvas.drawRoundRect(pill,getHeight() * attributes.getFloat(PILL_RADIUS),getHeight() * attributes.getFloat(PILL_RADIUS),pillPaint);
        if ( subText != null ) {
            canvas.drawText(subText, 30, getY() + getHeight() / 2 - attributes.getFloat(PILL_SUBTEXT_SIZE_KEY) / 2 - textOffset,pillSubTextPaint);
        }

        if ( mainText != null ) {
            canvas.drawText(mainText, 30, getY() + getHeight() / 2 + attributes.getFloat(PILL_MAINTEXT_SIZE_KEY) / 2 + textOffset,pillMainTextPaint);
        }
        canvas.drawBitmap(pillDrawable, null, pillDrawableRect, null);
        //end pill block
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap;
        if ( attributes.getString(PILL_DRAWABLE_COLOR_KEY) != null ) {
            DrawableCompat.setTint(drawable, Color.parseColor(attributes.getString(PILL_DRAWABLE_COLOR_KEY)));
        }
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0 , 0 , canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // enable swiping if valid position
                if ( imageOffset - (drawableDimen + 50) < motionEvent.getX() && motionEvent.getX() < right )
                    isSwipeEnabled = true;
                else
                    isSwipeEnabled = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // if swipe is enabled handle changing right here
                if ( isSwipeEnabled ) {
                    if ( Float.compare(motionEvent.getX(), minRight) > 0 ) {
                        right = motionEvent.getX();
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // if swipe is enabled
                // - if swipe crossed threshold switch state is
                if ( motionEvent.getX() >= getX() + getWidth() * swipeThreshold ) {
                    right = getX() + getWidth();
                    invalidate();
                    if ( listener != null ) {
                        listener.onStateOn();
                    } else {
                        Log.e(TAG, "No listener attached, use setListener");
                    }
                } else {
                    right = minRight;
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void setListener(SwipeSwitchStateListener listener){
        this.listener = listener;
    }
}
