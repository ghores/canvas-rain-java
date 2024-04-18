package com.example.canvasrain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;

public class CanvasView extends AppCompatImageView {

    private Paint paint;
    private Thread physicThread;
    private Thread renderThread;
    private ArrayList<Drop> drops = new ArrayList<>();


    public CanvasView(Context context) {
        super(context);
        initialize();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        paint = new Paint();
        paint.setColor(Color.argb(255, 127, 127, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        physicThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        for (Drop drop : drops) {
                            drop.size += 0.3f * drop.speed;
                        }
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        renderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        postInvalidate();
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        physicThread.start();
        renderThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < 10; i++) {
            @SuppressLint("DrawAllocation") Drop drop = new Drop();
            drop.px = (float) (Math.random() * getWidth());
            drop.py = (float) (Math.random() * getHeight());
            drop.size = (float) (Math.random() * 10);
            drop.speed = (float) (Math.random() * 2 + 1);
            drops.add(drop);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Drop drop : drops) {
            float alpha = 255 - drop.size * 2.55f;
            if (alpha < 0) {
                alpha = 0;
            }
            if (alpha > 255) {
                alpha = 255;
            }
            paint.setAlpha((int) alpha);
            canvas.drawCircle(drop.px, drop.py, drop.size, paint);
        }
    }
}
