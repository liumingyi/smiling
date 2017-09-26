package org.liumingyi.loadingview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 控件 - 简易笑脸加载
 * Created by liumingyi on 2017/9/25.
 */

public class LoadingView extends View {

  private static final long DURATION_TIME_0 = 2500;//单位：毫秒
  private static final long DURATION_TIME_1 = 1000;
  private static final long DURATION_TIME_2 = 1500;
  private static final long START_DELAY = 300;

  private static final double ANGLE_EYE = 35;// 代表眼睛的Point与center的连线 和 中线的夹角,35度
  private static final float RATIO_ANGLE_PROGRESS = 3.6f;// angle／progress = 360 / 100 = 3.6f

  private int mode = 0;//区分动画的不同阶段,以对应不同的绘制

  private Paint paint;

  private Point center = new Point();
  private Point leftEye = new Point();
  private Point rightEye = new Point();
  private RectF arcRectF = new RectF();

  private boolean isStop;

  private float progress;
  private float swipeAngle = 1;

  private AnimatorSet animatorSet;
  private Animator.AnimatorListener animatorSetListener = new Animator.AnimatorListener() {
    @Override public void onAnimationStart(Animator animator) {
    }

    @Override public void onAnimationEnd(Animator animator) {
      resetAnimatorParams();
      if (isStop) {
        return;
      }
      animatorSet.setStartDelay(START_DELAY);
      animatorSet.start();
    }

    @Override public void onAnimationCancel(Animator animator) {
    }

    @Override public void onAnimationRepeat(Animator animator) {
    }
  };

  public float getProgress() {
    return progress;
  }

  public void setProgress(float progress) {
    this.progress = progress;
    invalidate();
  }

  public float getSwipeAngle() {
    return swipeAngle;
  }

  public void setSwipeAngle(float swipeAngle) {
    this.swipeAngle = swipeAngle;
    invalidate();
  }

  public LoadingView(Context context) {
    super(context);
    init(context);
  }

  public LoadingView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(ContextCompat.getColor(context, R.color.green));
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeCap(Paint.Cap.ROUND);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    setMeasuredDimension(width, height);
    initViewSize(width, height);
  }

  private void initViewSize(int width, int height) {
    center.set(width / 2, height / 2);

    int strokeWidth = width / 8;
    paint.setStrokeWidth(strokeWidth);

    int halfStroke = strokeWidth / 2;
    float radius = width / 2 - halfStroke;

    double cos = Math.cos(Math.PI / 180 * ANGLE_EYE);
    int y = (int) (radius * (1 - cos) + halfStroke);

    double sin = Math.sin(Math.PI / 180 * ANGLE_EYE);
    leftEye.set((int) (radius * (1 - sin) + halfStroke), y);
    rightEye.set((int) (radius * (1 + sin) + halfStroke), y);
    arcRectF.set(halfStroke, halfStroke, width - halfStroke, height - halfStroke);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (mode == 0) {
      float per = getSwipeAnglePercent(progress);
      if (progress < 50) {
        canvas.drawPoint(leftEye.x, leftEye.y, paint);
        canvas.drawPoint(rightEye.x, rightEye.y, paint);
      }
      canvas.drawArc(arcRectF, progress * RATIO_ANGLE_PROGRESS, per * 180, false, paint);
    } else {
      if (270 - progress * RATIO_ANGLE_PROGRESS < ANGLE_EYE) {
        canvas.drawPoint(leftEye.x, leftEye.y, paint);
      }
      if (progress * RATIO_ANGLE_PROGRESS - 270 > ANGLE_EYE) {
        canvas.drawPoint(rightEye.x, rightEye.y, paint);
      }
      canvas.drawArc(arcRectF, progress * RATIO_ANGLE_PROGRESS, swipeAngle * 1, false, paint);
    }
  }

  private float getSwipeAnglePercent(float progress) {
    if (progress < 75) {
      return 1f;
    } else {
      return (125 - progress) / 50f;
    }
  }

  private void initAnimator() {
    /* 第一段动画：Mode = 0，以长条样式滚动一周后，缩短为0*/
    Keyframe keyframe0 = Keyframe.ofFloat(0, 0);
    Keyframe keyframe1 = Keyframe.ofFloat(0.5f, 125);
    PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("progress", keyframe0, keyframe1);
    Animator animator0 = ObjectAnimator.ofPropertyValuesHolder(this, holder);
    animator0.setInterpolator(new LinearInterpolator());
    animator0.setDuration(DURATION_TIME_0);
    /* animator0 结束时，mode切换到 1*/
    animator0.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {

      }

      @Override public void onAnimationEnd(Animator animator) {
        mode = 1;
        progress = 0;
      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {

      }
    });

    /* 第二段动画：Mode = 1，以点的方式转动半周*/
    Keyframe keyframe2 = Keyframe.ofFloat(0, 50);
    Keyframe keyframe3 = Keyframe.ofFloat(1, 100);
    PropertyValuesHolder holder1 =
        PropertyValuesHolder.ofKeyframe("progress", keyframe2, keyframe3);
    Animator animator1 = ObjectAnimator.ofPropertyValuesHolder(this, holder1);
    animator1.setInterpolator(new LinearInterpolator());
    animator1.setDuration(DURATION_TIME_1);

    /* 第三段动画：Mode = 1，从点在变为长条样式*/
    Animator animator2 = ObjectAnimator.ofFloat(this, "swipeAngle", 180);
    animator2.setInterpolator(new LinearInterpolator());
    animator2.setDuration(DURATION_TIME_2);

    /* 三段动画依次播放,结束时重置动画参数，并再次启动动画*/
    animatorSet = new AnimatorSet();
    animatorSet.playSequentially(animator0, animator1, animator2);

    animatorSet.addListener(animatorSetListener);
  }

  private void resetAnimatorParams() {
    mode = 0;
    progress = 0;
    swipeAngle = 1;
  }

  public void loading() {
    isStop = false;
    if (animatorSet == null) {
      initAnimator();
    }

    if (animatorSet.isRunning() || animatorSet.isStarted()) {
      return;
    }

    animatorSet.start();
  }

  public void stop() {
    isStop = true;
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    destroyAnimatorSet();
  }

  private void destroyAnimatorSet() {
    animatorSet.cancel();
    animatorSet.removeAllListeners();
  }
}
