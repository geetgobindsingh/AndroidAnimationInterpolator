package com.geet.interpolator.androidanimationinterpolator.ui.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geet.interpolator.androidanimationinterpolator.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nishant.math.MathView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //region Member variables
    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int CYCLE_INTERPOLATOR = 0;
    private static final int ACCELERATE_INTERPOLATOR = 1;
    private static final int DECELERATE_INTERPOLATOR = 2;
    private static final int ACCELERATE_DECELERATE_INTERPOLATOR = 3;
    private static final int ANTICIPATE_INTERPOLATOR = 4;
    private static final int OVERSHOOT_INTERPOLATOR = 5;
    private static final int ANTICIPATE_OVERSHOOT_INTERPOLATOR = 6;
    private static final int LINEAR_INTERPOLATOR = 7;
    private static final int BOUNCE_INTERPOLATOR = 8;
    private static final int CUSTOM_SPRING_INTERPOLATOR = 9;

    private static final int ANIMATION_DURATION_MILLISECONDS = 2000;
    //endregion

    //region View variables
    @BindView(R.id.interpolator_spinner)
    AppCompatSpinner mInterpolatorSpinner;

    @BindView(R.id.interpolator_line_chart)
    LineChart mInterpolatorLineChart;

    @BindView(R.id.interpolator_selected_formulae)
    MathView mInterpolatorSelectedFormulae;

    @BindView(R.id.interpolator_alpha)
    RelativeLayout mInterpolatorAlpha;

    @BindView(R.id.interpolator_scale)
    RelativeLayout mInterpolatorScale;

    @BindView(R.id.interpolator_translate_path)
    RelativeLayout mInterpolatorTranslatePath;

    @BindView(R.id.interpolator_translate)
    RelativeLayout mInterpolatorTranslate;

    @BindView(R.id.interpolator_rotate)
    RelativeLayout mInterpolatorRotate;

    @BindView(R.id.interpolator_selected_name)
    TextView mInterpolatorSelectedName;
    //endregion

    //region Member variables
    private List<String> interpolatorList = new ArrayList<>();
    private List<Entry> entries = new ArrayList<Entry>();
    private int mSelectedInterpolatorState = CYCLE_INTERPOLATOR; // default selected cycle interpolator index
    private Interpolator mSelectedInterpolator = new CycleInterpolator(1); // taking default cycle Interpolator cycle count as 1
    private static boolean toggleFlag = true; //by default true : toggles animation
    private AnimatorSet mAnimatorSet;
    private ValueAnimator mAlphaAnimator, mScaleDownXAnimator, mScaleDownYAnimator, mScaleUpXAnimator, mScaleUpYAnimator, mTranslateAnimator, mRotateAnimator;
    //endregion

    //region Activity Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Binding View with respective ViewObjects
        ButterKnife.bind(this);

        // Setting listener to Spinner item selection
        mInterpolatorSpinner.setOnItemSelectedListener(HomeActivity.this);

        // Populating list of Default Android Interpolator
        interpolatorList.add(CYCLE_INTERPOLATOR, "Cycle Interpolator");
        interpolatorList.add(ACCELERATE_INTERPOLATOR, "Accelerate Interpolator");
        interpolatorList.add(DECELERATE_INTERPOLATOR, "Decelerate Interpolator");
        interpolatorList.add(ACCELERATE_DECELERATE_INTERPOLATOR, "Accelerate Decelerate Interpolator");
        interpolatorList.add(ANTICIPATE_INTERPOLATOR, "Anticipate Interpolator");
        interpolatorList.add(OVERSHOOT_INTERPOLATOR, "Overshoot Interpolator");
        interpolatorList.add(ANTICIPATE_OVERSHOOT_INTERPOLATOR, "Anticipate Overshoot Interpolator");
        interpolatorList.add(LINEAR_INTERPOLATOR, "Linear Interpolator");
        interpolatorList.add(BOUNCE_INTERPOLATOR, "Bounce Interpolator");
        interpolatorList.add(CUSTOM_SPRING_INTERPOLATOR, "Custom Spring Interpolator");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, interpolatorList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attaching data adapter to spinner
        mInterpolatorSpinner.setAdapter(dataAdapter);

        // Initializing AnimatorSet object for group animation
        mAnimatorSet = new AnimatorSet();

        mAnimatorSet.setDuration(ANIMATION_DURATION_MILLISECONDS);

        // Disabling all touvh events on Graph
        mInterpolatorLineChart.setTouchEnabled(false);

        // Rest to default Graph
        resetGraphData();
    }
    //endregion

    //region Private Helper methods
    private void resetGraphData() {

        // Set selected Interpolator name
        mInterpolatorSelectedName.setText(interpolatorList.get(mSelectedInterpolatorState));

        // clear and add new points to chart wrt. selected interpolator
        entries.clear();
        entries.addAll(getSelectedInterpolatorGraphPoints());

        // Populating data in LinChart
        LineDataSet lineDataSet = new LineDataSet(entries, "X = originalTime | Y = displacedTime");
        LineData lineData = new LineData(lineDataSet);

        mInterpolatorLineChart.setData(lineData);

        // Refresh Chart
        mInterpolatorLineChart.invalidate();
    }

    /**
     * This method updates member interpolator object and
     * computes points for respective interpolator.
     *
     * @return List of Points to be plot on chart
     */
    private List<Entry> getSelectedInterpolatorGraphPoints() {
        List<Entry> entryList = new ArrayList<>();
        switch (mSelectedInterpolatorState) {
            case LINEAR_INTERPOLATOR: {
                mSelectedInterpolator = new LinearInterpolator();
                mInterpolatorSelectedFormulae.setText("`displacedTime = time`, \nWhere time is originalTime");
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = original_time;
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case ACCELERATE_INTERPOLATOR: {
                mSelectedInterpolator = new AccelerateInterpolator();
                mInterpolatorSelectedFormulae.setText("`displacedTime = time^2`, \nWhere time is originalTime");
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) Math.pow((original_time), 2);
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case DECELERATE_INTERPOLATOR: {
                mSelectedInterpolator = new DecelerateInterpolator();
                mInterpolatorSelectedFormulae.setText("`displacedTime = 1 - (1 - time)^2`, \nWhere time is originalTime");
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) (1 - Math.pow(1 - original_time, 2));
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case ACCELERATE_DECELERATE_INTERPOLATOR: {
                mSelectedInterpolator = new AccelerateDecelerateInterpolator();
                mInterpolatorSelectedFormulae.setText("`displacedTime = cos((time + 1)π)/2 + 0.5`, \nWhere time is originalTime");
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) (Math.cos((Math.PI) * (original_time + 1)) / 2) + 0.5f;
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case ANTICIPATE_INTERPOLATOR: {
                mSelectedInterpolator = new AnticipateInterpolator();
                mInterpolatorSelectedFormulae.setText("`displacedTime = (T + 1)*(time^3) - T*time^2`, \nWhere T is the amount of tension, the default is 2 and time is originalTime");
                int T = 2; // default Where T is the amount of tension, the default is 2
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) (((T + 1) * Math.pow(original_time, 3)) - (T * Math.pow(original_time, 2)));
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case OVERSHOOT_INTERPOLATOR: {
                mSelectedInterpolator = new OvershootInterpolator();
                mInterpolatorSelectedFormulae.setText("`displacedTime = (T + 1)*(time - 1)^3 + (T*(time - 1)^2) + 1`, \nWhere T is the amount of tension, the default is 2 and time is originalTime");
                int T = 2; // default Where T is the amount of tension, the default is 2
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) (((T + 1) * Math.pow(original_time - 1, 3)) + (T * Math.pow(original_time - 1, 2)) + 1);
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case ANTICIPATE_OVERSHOOT_INTERPOLATOR: {
                mSelectedInterpolator = new AnticipateOvershootInterpolator();
                mInterpolatorSelectedFormulae.setText("displacedTime = if (time < 0.5) than {`(0.5)*((T + 1)*(2t)^3) - (T*(2t)^2)`}\n " +
                        "else if (time >= 0.5) than {`(0.5)*((T + 1)*(2t- 2)^3) - (T*(2t-2)^2)`, \nWhere T is the amount of tension, the default is 2 and time/t is originalTime} ");
                int T = 2; // default Where T is the amount of tension, the default is 2"
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = 0.0f;
                    if (original_time >= 0.5f) {
                        displaced_time = (float) ((0.5f) * (((T + 1) * Math.pow((2 * original_time) - 2, 3)) + ((T * Math.pow((2 * original_time) - 2, 2))))) + 1;
                    } else if (original_time < 0.5f) {
                        displaced_time = (float) ((0.5f) * (((T + 1) * Math.pow((2 * original_time), 3)) - (T * Math.pow(2 * original_time, 2))));
                    }
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case BOUNCE_INTERPOLATOR: {
                mSelectedInterpolator = new BounceInterpolator();
                mInterpolatorSelectedFormulae.setText("displacedTime = if (time < 0.31489) than {`8*(1.1.226time)^2)`}\n " +
                        "else if (0.31489 <= time < 0.65990) than {`8*(1.1.226time - 0.54719)^2) + 0.7`} " +
                        "else if (0.65990 <= time < 0.85908) than {`8*(1.1.226time - 0.8526)^2) + 0.9`} " +
                        "else if (0.85908 <= time) than {`8*(1.1.226time - 1.0435)^2) + 1.0435`, \nwhere time is originalTime} ");

                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = 0.0f;
                    if (original_time < 0.31489f) {
                        displaced_time = (float) (8 * Math.pow((1.1226 * original_time), 2));
                    } else if (original_time >= 0.31489f && original_time < 0.65990f) {
                        displaced_time = (float) (8 * Math.pow((1.1226 * original_time) - 0.54719, 2) + 0.7f);
                    } else if (original_time >= 0.65990f && original_time < 0.85908f) {
                        displaced_time = (float) (8 * Math.pow((1.1226 * original_time) - 0.8526, 2) + 0.9f);
                    } else if (original_time >= 0.85908) {
                        displaced_time = (float) (8 * Math.pow((1.1226 * original_time - 1.0435), 2) + 1.0435f);
                    }
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case CYCLE_INTERPOLATOR: {
                mSelectedInterpolator = new CycleInterpolator(1);
                mInterpolatorSelectedFormulae.setText("`displacedTime = sin(2π*C*time)`, \nWhere C is the number of cycles, the default is 1 and time is originalTime");
                int C = 1; //default Where C is the number of cycles, the default is 1
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) Math.sin(2 * Math.PI * C * original_time);
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
            case CUSTOM_SPRING_INTERPOLATOR: {
                float F = 0.3f; //Where F is the damping factor, the default is 0.3
                mSelectedInterpolator = new CustomSpringInterpolator(F);
                mInterpolatorSelectedFormulae.setText("`displacedTime = 2^(-10*time) * sin((2π*(time - (F/4)))) + 1` " +
                        ", \nWhere F is the damping factor, the default is 0.3 and time is originalTime");
                for (float original_time = 0; original_time < 1.1f; original_time += 0.1f) {
                    float displaced_time = (float) (Math.pow(2, (-10 * original_time)) * Math.sin(((2* Math.PI) * (original_time - (F/4)))/F) + 1);
                    entryList.add(new Entry(original_time, displaced_time));
                }
                break;
            }
        }

        animate();

        return entryList;
    }

    private void animate() {
        mAnimatorSet.setInterpolator(mSelectedInterpolator);

        if (toggleFlag) {

            mAlphaAnimator = ObjectAnimator.ofFloat(mInterpolatorAlpha, "alpha", 1f, 0f);

            mScaleDownXAnimator = ObjectAnimator.ofFloat(mInterpolatorScale, "scaleX", 1.0f, 0.0f);
            mScaleDownYAnimator = ObjectAnimator.ofFloat(mInterpolatorScale, "scaleY", 1.0f, 0.0f);

            mTranslateAnimator = ObjectAnimator.ofFloat(mInterpolatorTranslate, "translationX", 0, (mInterpolatorTranslatePath.getWidth() - mInterpolatorTranslate.getWidth()));

            mRotateAnimator = ObjectAnimator.ofFloat(mInterpolatorRotate, "rotation", 0f, 360f);

            mAnimatorSet.play(mAlphaAnimator).with(mScaleDownXAnimator).with(mScaleDownYAnimator).with(mTranslateAnimator).with(mRotateAnimator);

            toggleFlag = false;

        } else {
            mAlphaAnimator = ObjectAnimator.ofFloat(mInterpolatorAlpha, "alpha", 0f, 1f);

            mScaleUpXAnimator = ObjectAnimator.ofFloat(mInterpolatorScale, "scaleX", 0.0f, 1.0f);
            mScaleUpYAnimator = ObjectAnimator.ofFloat(mInterpolatorScale, "scaleY", 0.0f, 1.0f);

            mTranslateAnimator = ObjectAnimator.ofFloat(mInterpolatorTranslate, "translationX", (mInterpolatorTranslatePath.getWidth() - mInterpolatorTranslate.getWidth()), 0);

            mRotateAnimator = ObjectAnimator.ofFloat(mInterpolatorRotate, "rotation", 360f, 0f);

            mAnimatorSet.play(mAlphaAnimator).with(mScaleUpXAnimator).with(mScaleUpYAnimator).with(mTranslateAnimator).with(mRotateAnimator);

            toggleFlag = true;
        }

        mAnimatorSet.start();
    }
    //endregion

    //region Overriding AdapterView.OnItemSelectedListener methods
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        // Update selected interpolator index in list
        mSelectedInterpolatorState = position;

        //reset Graph to respective selected interpolator
        resetGraphData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //no-op
    }
    //endregion

    //region ButterKnife Listeners Binding
    @OnClick(R.id.animate)
    void onCLick(View view) {
        animate();
    }
    //endregion
}


