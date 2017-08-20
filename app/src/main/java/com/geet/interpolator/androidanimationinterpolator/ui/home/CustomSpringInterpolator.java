package com.geet.interpolator.androidanimationinterpolator.ui.home;

import android.view.animation.Interpolator;

/**
 * Created by geetgobindsingh on 16/08/17.
 */

public class CustomSpringInterpolator implements Interpolator {

    private float factor = 0.3f; // default

    public CustomSpringInterpolator() {}

    public CustomSpringInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, (-10 * input)) * Math.sin(((2* Math.PI) * (input - (factor/4)))/factor) + 1);
    }
}
