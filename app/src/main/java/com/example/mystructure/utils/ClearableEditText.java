package com.example.mystructure.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ClearableEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private Location location = Location.RIGHT;
    private Drawable mDrawable;
    private Listener listener;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;

    public ClearableEditText(Context context) {
        super(context);
        initialize();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setClearIconVisible(boolean visible) {
        Drawable[] cd = getCompoundDrawables();
        Drawable displayed = getDisplayedDrawable();
        boolean wasVisible = (displayed != null);
        if (visible != wasVisible) {
            Drawable x = visible ? mDrawable : null;
            super.setCompoundDrawables((location == Location.LEFT) ? x : cd[0], cd[1], (location == Location.RIGHT) ? x : cd[2],
                    cd[3]);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void initialitationIcon() {
        mDrawable = null;
        if (location != null) {
            mDrawable = getCompoundDrawables()[location.idx];
        }
        if (mDrawable == null) {
            mDrawable = ContextCompat.getDrawable(getContext(), android.R.drawable.presence_offline);
        }
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        int min = getPaddingTop() + mDrawable.getIntrinsicHeight() + getPaddingBottom();
        if (getSuggestedMinimumHeight() < min) {
            setMinimumHeight(min);
        }
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        initialitationIcon();
    }

//  public void setIconLocation(Location location) {
//    this.location = location;
//    initialitationIcon();
//  }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setClearIconVisible(isNotEmpty(s));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(isNotEmpty(getText()));
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getDisplayedDrawable() != null) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int left = (location == Location.LEFT) ? 0 : getWidth() - getPaddingRight() - mDrawable.getIntrinsicWidth();
            int right = (location == Location.LEFT) ? getPaddingLeft() + mDrawable.getIntrinsicWidth() : getWidth();
            boolean tappedX = x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setText("");
                    if (listener != null) {
                        listener.didClearText();
                    }
                }
                return true;
            }
        }
        if (mOnTouchListener != null) {
            return mOnTouchListener.onTouch(v, event);
        }
        return false;
    }

    private void initialize() {
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
//    addTextChangedListener(new TextWatcherAdapter(this, this));
        initialitationIcon();
        setClearIconVisible(false);
    }

    private Drawable getDisplayedDrawable() {
        return (location != null) ? getCompoundDrawables()[location.idx] : null;
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        this.mOnTouchListener = onTouchListener;
    }

    public enum Location {
        LEFT(0), RIGHT(2);
        final int idx;

        private Location(int idx) {
            this.idx = idx;
        }
    }

    public interface Listener {
        void didClearText();
    }
}
