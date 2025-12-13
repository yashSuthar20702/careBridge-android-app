package com.example.carebridge.wear.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * SwipeGestureListener

 * Reusable gesture listener for detecting LEFT and RIGHT swipe gestures
 * on Wear OS screens.

 * Designed for small, round displays and future navigation extensions.
 */
@SuppressWarnings("unused") // Used by UI screens via composition
public abstract class SwipeGestureListener implements View.OnTouchListener {

    private static final String TAG = "SwipeGestureListener";

    // Gesture detector for handling swipe motions
    private final GestureDetector gestureDetector;

    // Initial touch positions
    private float initialX, initialY;

    /**
     * Constructor

     * Initializes gesture detection for Wear OS.
     */
    public SwipeGestureListener(@NonNull Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /**
     * Handles touch events from the attached view.
     */
    @Override
    public boolean onTouch(
            @NonNull View v,
            @NonNull MotionEvent event
    ) {

        // Let GestureDetector process gestures first
        boolean handled = gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(event.getX() - initialX);
                float deltaY = Math.abs(event.getY() - initialY);

                // Prioritize horizontal swipe
                if (deltaX > deltaY) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return handled;
    }

    /** Triggered when user swipes LEFT */
    public abstract void onSwipeLeft();

    /** Triggered when user swipes RIGHT */
    public abstract void onSwipeRight();

    /**
     * Internal listener that detects swipe gestures.
     */
    private final class GestureListener
            extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 80;
        private static final int SWIPE_VELOCITY_THRESHOLD = 80;
        private static final int SWIPE_MAX_OFF_PATH = 200;

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(
                @NonNull MotionEvent e1,
                @NonNull MotionEvent e2,
                float velocityX,
                float velocityY
        ) {

            try {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                // Horizontal swipe check
                if (Math.abs(diffX) > Math.abs(diffY)
                        && Math.abs(diffY) < SWIPE_MAX_OFF_PATH
                        && Math.abs(diffX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }

            } catch (Exception e) {
                Log.e(TAG, "Swipe gesture detection failed", e);
            }

            return false;
        }
    }
}