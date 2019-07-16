package com.opensource.svgaplayer.glideplugin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.LinearInterpolator;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;

/**
 * Created by 张宇 on 2019/4/24.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 * <p>
 * 为什么突然改成Java，因为要利用Kotlin的一个Bug，强行调用一个internal的方法：SVGADrawable#setCurrentFrame
 */
public class SVGADrawableWrapper extends Drawable implements Animatable {

    private ValueAnimator animator;

    private final SVGADrawable drawable;

    public SVGADrawableWrapper(SVGADrawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void start() {
        Object callback = getCallback();
        if (callback instanceof SVGAImageView) {
            SVGAImageView imageView = (SVGAImageView) callback;
            drawable.setCallback(imageView);
            final SVGACallback cb = imageView.getCallback();

            double durationScale = 1.0;
            int startFrame = drawable.getCurrentFrame();
            int endFrame = drawable.getVideoItem().getFrames() - 1;

            if (animator != null) {
                animator.end();
            }
            animator = ValueAnimator.ofInt(startFrame, endFrame);
            if (imageView.getLoops() > 0) {
                animator.setRepeatCount(imageView.getLoops());
            } else {
                animator.setRepeatCount(ValueAnimator.INFINITE);
            }
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(
                    (long) (((endFrame - startFrame + 1) * (1000.0 / drawable.getVideoItem().getFPS()))
                            / durationScale)
            );
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int cFrame = (int) animation.getAnimatedValue();
                    drawable.setCurrentFrame$library_release(cFrame);
                    if (cb != null) {
                        cb.onStep(cFrame, cFrame / (drawable.getVideoItem().getFrames() - 1.0));
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationPause(Animator animation) {
                    if (cb != null) {
                        cb.onPause();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    if (cb != null) {
                        cb.onRepeat();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (cb != null) {
                        cb.onFinished();
                    }
                }
            });
            animator.start();
        }
    }

    @Override
    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        drawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        drawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        drawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return drawable.getOpacity();
    }
}
