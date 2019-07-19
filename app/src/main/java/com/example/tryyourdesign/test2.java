package com.example.tryyourdesign;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class test2 extends GraphicsActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }

    class SampleView extends View {



        private static final int LAYER_FLAGS = Canvas.ALL_SAVE_FLAG;
        private Paint mPaint;

        public SampleView(Context context) {
            super(context);
            setFocusable(true);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        @Override protected void onDraw(Canvas canvas) {
            //canvas.drawColor(Color.WHITE);

            canvas.translate(10, 10);

            canvas.saveLayerAlpha(0, 0, 200, 200, 0x88, LAYER_FLAGS);
/*
            mPaint.setColor(Color.RED);
            canvas.drawCircle(75, 75, 75, mPaint);
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle(125, 125, 75, mPaint);
*/
            canvas.restore();
        }
    }
}




