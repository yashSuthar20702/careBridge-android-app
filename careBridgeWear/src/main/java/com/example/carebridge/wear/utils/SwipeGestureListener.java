package com.example.carebridge.wear.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class SwipeGestureListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private float initialX, initialY;

    public SwipeGestureListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Let the gesture detector handle the touch event first
        boolean handled = gestureDetector.onTouchEvent(event);

        // Additional handling to prevent system back gesture
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                // If we're handling a horizontal swipe, prevent parent from intercepting
                float deltaX = Math.abs(event.getX() - initialX);
                float deltaY = Math.abs(event.getY() - initialY);

                // If horizontal movement is greater than vertical, it's likely a swipe
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

    public abstract void onSwipeLeft();
    public abstract void onSwipeRight();

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 80; // Increased threshold for Wear OS
        private static final int SWIPE_VELOCITY_THRESHOLD = 80;
        private static final int SWIPE_MAX_OFF_PATH = 200; // Allow some vertical movement

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;

            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                // Check if it's primarily a horizontal swipe
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Check if the swipe is not too vertical
                    if (Math.abs(diffY) < SWIPE_MAX_OFF_PATH) {
                        // Check if it meets the threshold and velocity requirements
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            return true;
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }
}