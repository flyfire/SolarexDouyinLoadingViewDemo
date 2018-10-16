package com.solarexsoft.solarexdouyinloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 15:55/2018/10/16
 *    Desc:
 * </pre>
 */
public class SolarexDouyinLoadingView extends View {
    public static final String TAG = SolarexDouyinLoadingView.class.getSimpleName();

    private final float RADIUS = dp2px(6f);
    private final float GAP = dp2px(1f);
    private static final float RTL_SCALE = 0.7f;
    private static final float LTR_SCALE = 1.3f;
    private static final int LEFT_COLOR = Color.parseColor("#FFFF4040");
    private static final int RIGHT_COLOR = Color.parseColor("#FF00EEEE");
    private static final int MIX_COLOR = Color.BLACK;
    private static final int DURATION = 350;
    private static final int PAUSE_DURATION = 80;
    private static final float SCALE_START_FRACTION = 0.2f;
    private static final float SCALE_END_FRACTION = 0.8f;

    private float radius1; // 左小球半径
    private float radius2; // 右小球半径
    private float gap; // 小球之间的距离
    private float rtlScale; // 小球从右边移动到左边时大小倍数变化
    private float ltrScale; // 小球从左边移动到右边时大小倍数变化
    private int color1; // 左小球颜色
    private int color2; // 右小球颜色
    private int mixColor; // 两小球重叠处的颜色
    private int duration; // 小球一次移动时长
    private int pauseDuration; //小球一次移动后停顿时长
    private float scaleStartFraction; //小球一次移动期间，进度在[0,scaleStartFraction]期间根据rtlScale、ltrScale逐渐缩放
    private float scaleEndFraction; //小球一次移动期间，进度在[scaleEndFraction,1]期间逐渐恢复初始大小,取值为[0.5,1]


    private Paint paint1, paint2, mixPaint;
    private Path ltrPath, rtlPath, mixPath;
    private float distance; // 小球一次移动的距离，即两球圆点之间的距离

    private ValueAnimator mAnimator;
    private float mFraction; // 小球一次移动动画的进度百分比
    boolean isAnimCanceled = false;
    boolean isLtr = true; // true = 【初始左球】当前正【从左往右】移动,false = 【初始左球】当前正【从右往左】移动


    public SolarexDouyinLoadingView(Context context) {
        this(context, null);
    }

    public SolarexDouyinLoadingView(Context context, @Nullable AttributeSet
            attrs) {
        this(context, attrs, 0);
    }

    public SolarexDouyinLoadingView(Context context, @Nullable AttributeSet
            attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SolarexDouyinLoadingView);
        if (typedArray != null) {
            radius1 = typedArray.getDimension(R.styleable.SolarexDouyinLoadingView_radius1, RADIUS);
            radius2 = typedArray.getDimension(R.styleable.SolarexDouyinLoadingView_radius2, RADIUS);
            
            typedArray.recycle();
        }

    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
                .getDisplayMetrics());
    }
}
