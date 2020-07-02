package com.example.myapplication;

import android.content.Intent;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    private float startX, stopX, startY, stopY;
    // We will only detect a swipe if the difference is at least 200 pixels
    private static final int TRESHOLD = 200;
    private int numFingers;
    // Used to prevent 3 finger left swipe
    boolean twoFingerTouch;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onTouchEventCallback(event);
        return false;
    }

    protected void onTouchEventCallback(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                numFingers = 1;
                break;

            case MotionEvent.ACTION_UP:
                numFingers = 0;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                numFingers += 1;
                if (numFingers == 2) {
                    startX = event.getX(0);
                    twoFingerTouch = true;
                }

                if (numFingers == 3) {
                    startY = event.getY(0);
                    if (twoFingerTouch) {
                        twoFingerTouch = false;
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:

                if (numFingers == 3 && startY < 200 && startY + TRESHOLD < stopY) {
                    // Exit the app
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                }

                if (numFingers == 2 && twoFingerTouch && startX > stopX + TRESHOLD) {
                    // Swipe left.
                    finish();
                    twoFingerTouch = false;
                }
                numFingers -= 1;

                break;

            case MotionEvent.ACTION_MOVE:

                if (numFingers == 2) {
                    stopX = event.getX(0);
                }

                if (numFingers == 3) {
                    stopY = event.getY(0);
                }

                break;
        }
    }


    @Override
    public void onBackPressed() {
        // do nothing
    }
}
