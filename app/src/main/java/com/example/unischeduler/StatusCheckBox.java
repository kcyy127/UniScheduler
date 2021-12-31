package com.example.unischeduler;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

public class StatusCheckBox extends AppCompatCheckBox {

    private static final int STATUS_UNSTARTED = 0;
    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_COMPLETED = 2;

    private int state;

    public StatusCheckBox(@NonNull Context context) {
        super(context);
        initialize();
    }

    public StatusCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public StatusCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        state = STATUS_UNSTARTED;
        updateBtn();

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (state) {
                    default:
                    case STATUS_UNSTARTED:
                        setState(STATUS_IN_PROGRESS);
                        break;
                    case STATUS_IN_PROGRESS:
                        setState(STATUS_COMPLETED);
                        break;
                    case STATUS_COMPLETED:
                        setState(STATUS_UNSTARTED);
                        break;
                }
                updateBtn();
            }
        });
    }

    public void updateBtn() {
        int buttonDrawable;
        switch (state) {
            case STATUS_UNSTARTED:
                buttonDrawable = R.drawable.checkbox_status_unstarted;
                break;
            case STATUS_IN_PROGRESS:
                buttonDrawable = R.drawable.checkbox_status_progress;
                break;
            case STATUS_COMPLETED:
                buttonDrawable = R.drawable.checkbox_status_completed;
                break;
            default:
                buttonDrawable = R.drawable.checkbox_status_unstarted;
                break;
        }
        setButtonDrawable(buttonDrawable);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateBtn();
    }
}
