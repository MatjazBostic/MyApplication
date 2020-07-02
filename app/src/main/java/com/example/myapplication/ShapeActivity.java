package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

public class ShapeActivity extends AbstractActivity {

    private int shape;
    private Vibrator vibrator;
    private Handler handler;
    private float scale;
    private final int STROKE_WIDTH = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CustomView(this));
        Intent intent = getIntent();
        int size = intent.getIntExtra("size", 0);
        shape = intent.getIntExtra("shape", 0);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();
        scale = ((float) size) / 100f;
    }

    public class CustomView extends View {

        private Paint paint;

        public CustomView(Context context) {
            super(context);

            // create the Paint and set its color
            paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();
            switch (shape) {
                case R.id.circleIV:
                    canvas.drawCircle(w / 2, h / 2,
                            (int) (((float) (w / 2 - 20)) * scale), paint);
                    break;
                case R.id.squareIV:
                    canvas.drawRect(w / 2.f - w / 2.f * scale,
                            h / 2.f + w / 2.f * scale,
                            w / 2f + w / 2f * scale,
                            h / 2f - w / 2f * scale, paint);
                    break;
                case R.id.triangleIV:
                    drawTriangle(canvas, paint, w / 2, h / 2, (int) (w * scale));
                    break;
            }
        }

        private int numFingers;
        private boolean vibrating;
        private static final int THRESHOLD = 50;
        private static final int VIBRATION_FREQUENCY = 400;

        @Override
        public boolean onTouchEvent(final MotionEvent event) {

            onTouchEventCallback(event); // Couldn't make it work with a call to super

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    numFingers = 1;
                case MotionEvent.ACTION_MOVE:
                    if (numFingers == 1) {
                        boolean isPressed = false;
                        float y = event.getY();
                        float x = event.getX();
                        int w = getWidth();
                        int h = getHeight();

                        switch (shape) {

                            case R.id.circleIV: {
                                double radius = Math.sqrt(Math.pow(x - w / 2.0, 2) + Math.pow(y - h / 2.0, 2));
                                isPressed = radius > (int) (((float) (w / 2 - THRESHOLD)) * scale) &&
                                        radius < (int) (((float) (w / 2 + THRESHOLD)) * scale);
                                break;
                            }
                            case R.id.squareIV: {
                                float leftSidePos = w / 2f - w / 2f * scale;
                                float rightSidePos = w / 2f + w / 2f * scale;
                                float topSidePos = h / 2f - w / 2f * scale;
                                float bottomSidePos = h / 2f + w / 2f * scale;

                                isPressed = (x > leftSidePos - THRESHOLD && x < leftSidePos + THRESHOLD && y > topSidePos && y < bottomSidePos)
                                        || (x < rightSidePos + THRESHOLD && x > rightSidePos - THRESHOLD && y > topSidePos && y < bottomSidePos)
                                        || (y > topSidePos - THRESHOLD && y < topSidePos + THRESHOLD && x < rightSidePos && x > leftSidePos)
                                        || (y < bottomSidePos + THRESHOLD && y > bottomSidePos - THRESHOLD && x < rightSidePos && x > leftSidePos);

                                break;
                            }
                            case R.id.triangleIV: {
                                float bottomSidePos = h / 2f + w * scale / 2;
                                float topSidePos = h / 2f - w * scale / 2;
                                float leftEdgeY = -2 * x + h / 2f - w * (scale / 2f - 1f);
                                float rightEdgeY = 2 * x + h / 2f - w * (scale / 2f + 1f);

                                isPressed = (y < bottomSidePos + THRESHOLD && y > bottomSidePos - THRESHOLD && x < w / 2f + w / 2f * scale && x > w / 2f - w / 2f * scale)
                                        || (y < leftEdgeY + THRESHOLD && y > leftEdgeY - THRESHOLD && y > topSidePos && y < bottomSidePos)
                                        || (y > rightEdgeY - THRESHOLD && y < rightEdgeY + THRESHOLD && y > topSidePos && y < bottomSidePos);
                                break;
                            }
                        }

                        if (isPressed) {
                            if (!vibrating) {
                                vibrating = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // Continuous vibration didn't work, had to make it with breaks
                                    vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_FREQUENCY, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    //deprecated in API 26
                                    vibrator.vibrate(VIBRATION_FREQUENCY);
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vibrating = false;
                                        onTouchEvent(event);
                                    }
                                }, VIBRATION_FREQUENCY);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    numFingers = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    numFingers += 1;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    numFingers -= 1;
                    break;
            }
            return true;
        }
    }

    private void drawTriangle(Canvas canvas, Paint paint, int x, int y, int width) {
        int halfWidth = width / 2;

        Path path = new Path();
        path.moveTo(x, y - halfWidth); // Top
        path.lineTo(x - halfWidth, y + halfWidth); // Bottom left
        path.lineTo(x + halfWidth, y + halfWidth); // Bottom right
        path.lineTo(x, y - halfWidth); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }
}
