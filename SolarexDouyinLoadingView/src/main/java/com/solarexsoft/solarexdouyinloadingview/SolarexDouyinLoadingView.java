package com.solarexsoft.solarexdouyinloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

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

    float centerY;
    float ltrInitRadius, rtlInitRadius;
    Paint ltrPaint, rtlPaint;
    float ltrX, rtlX;
    float ltrBallRadius, rtlBallRadius;
    int mWidth, mHeight;
    float scaleFraction;

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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .SolarexDouyinLoadingView);
        if (typedArray != null) {
            radius1 = typedArray.getDimension(R.styleable.SolarexDouyinLoadingView_radius1, RADIUS);
            radius2 = typedArray.getDimension(R.styleable.SolarexDouyinLoadingView_radius2, RADIUS);
            gap = typedArray.getDimension(R.styleable.SolarexDouyinLoadingView_gap, GAP);
            rtlScale = typedArray.getFloat(R.styleable.SolarexDouyinLoadingView_rtlScale,
                    RTL_SCALE);
            ltrScale = typedArray.getFloat(R.styleable.SolarexDouyinLoadingView_ltrScale,
                    LTR_SCALE);
            color1 = typedArray.getColor(R.styleable.SolarexDouyinLoadingView_color1, LEFT_COLOR);
            color2 = typedArray.getColor(R.styleable.SolarexDouyinLoadingView_color2, RIGHT_COLOR);
            mixColor = typedArray.getColor(R.styleable.SolarexDouyinLoadingView_mixColor,
                    MIX_COLOR);
            duration = typedArray.getInt(R.styleable.SolarexDouyinLoadingView_duration, DURATION);
            pauseDuration = typedArray.getInt(R.styleable.SolarexDouyinLoadingView_pauseDuration,
                    PAUSE_DURATION);
            scaleStartFraction = typedArray.getFloat(R.styleable
                    .SolarexDouyinLoadingView_scaleStartFraction, SCALE_START_FRACTION);
            scaleEndFraction = typedArray.getFloat(R.styleable
                    .SolarexDouyinLoadingView_scaleEndFraction, SCALE_END_FRACTION);
            typedArray.recycle();
        }

        checkAttr();

        distance = gap + radius1 + radius2;

        initDraw();

        initAnim();
    }

    private void checkAttr() {
        radius1 = radius1 > 0 ? radius1 : RADIUS;
        radius2 = radius2 > 0 ? radius2 : RADIUS;
        gap = gap >= 0 ? gap : GAP;
        rtlScale = rtlScale >= 0 ? rtlScale : RTL_SCALE;
        ltrScale = ltrScale >= 0 ? ltrScale : LTR_SCALE;
        duration = duration > 0 ? duration : DURATION;
        pauseDuration = pauseDuration >= 0 ? pauseDuration : PAUSE_DURATION;
        if (scaleStartFraction < 0 || scaleStartFraction > 0.5f) {
            scaleStartFraction = SCALE_START_FRACTION;
        }
        if (scaleEndFraction < 0.5f || scaleEndFraction > 1) {
            scaleEndFraction = SCALE_END_FRACTION;
        }
    }

    private void initDraw() {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint1.setColor(color1);
        paint2.setColor(color2);
        mixPaint.setColor(mixColor);

        ltrPath = new Path();
        rtlPath = new Path();
        mixPath = new Path();
    }

    private void initAnim() {
        mFraction = 0.0f;

        stop();

        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimator.setDuration(duration);
        if (pauseDuration > 0) {
            mAnimator.setStartDelay(pauseDuration);
            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        } else {
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setInterpolator(new LinearInterpolator());
        }

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = animation.getAnimatedFraction();
                invalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isAnimCanceled) {
                    mAnimator.start();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isLtr = !isLtr;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isLtr = !isLtr;
            }
        });
    }

    /**
     * 停止动画
     */
    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    public void start() {
        if (mAnimator == null) {
            initAnim();
        }
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        post(new Runnable() {
            @Override
            public void run() {
                isAnimCanceled = false;
                isLtr = false;
                mAnimator.start();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
                .getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        float maxScale = Math.max(rtlScale, ltrScale);
        maxScale = Math.max(maxScale, 1);

        if (wMode != MeasureSpec.EXACTLY) {
            wSize = (int) (gap + (2*radius1 + 2*radius2) * maxScale + dp2px(1));
        }
        if (hMode != MeasureSpec.EXACTLY) {
            hSize = (int) (2 * Math.max(radius1, radius2) * maxScale + dp2px(1));
        }

        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        centerY = h/2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isLtr) {
            ltrInitRadius = radius1;
            rtlInitRadius = radius2;
            ltrPaint = paint1;
            rtlPaint = paint2;
        } else {
            ltrInitRadius = radius2;
            rtlInitRadius = radius1;
            ltrPaint = paint2;
            rtlPaint = paint1;
        }

        ltrX = mWidth/2.0f - distance/2.0f;
        ltrX = ltrX + (distance * mFraction);

        rtlX = mWidth/2.0f + distance/2.0f;
        rtlX = rtlX - (distance * mFraction);

        if (mFraction <= scaleStartFraction) {
            scaleFraction = 1.0f * mFraction / scaleStartFraction;
            ltrBallRadius = ltrInitRadius * (1 + (ltrScale - 1) * scaleFraction);
            rtlBallRadius = rtlInitRadius * (1 + (rtlScale - 1) * scaleFraction);
        } else if (mFraction >= scaleEndFraction) {
            scaleFraction = (mFraction - 1) / (scaleEndFraction - 1);
            ltrBallRadius = ltrInitRadius * (1 + (ltrScale - 1) * scaleFraction);
            rtlBallRadius = rtlBallRadius * (1 + (rtlScale - 1) * scaleFraction);
        } else {
            ltrBallRadius = ltrInitRadius * ltrScale;
            rtlBallRadius = rtlBallRadius * rtlScale;
        }

        ltrPath.reset();
        ltrPath.addCircle(ltrX, centerY, ltrBallRadius, Path.Direction.CW);
        rtlPath.reset();
        rtlPath.addCircle(rtlX, centerY, rtlBallRadius, Path.Direction.CW);
        mixPath.op(ltrPath, rtlPath, Path.Op.INTERSECT);

        canvas.drawPath(ltrPath, ltrPaint);
        canvas.drawPath(rtlPath, rtlPaint);
        canvas.drawPath(mixPath, mixPaint);
    }
}
