package com.example.tryyourdesign;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import siclo.com.ezphotopicker.api.EZPhotoPickStorage;

import android.graphics.PorterDuffXfermode;
class myView2 extends View
{
    final int MODE_PENCIL = 0;
    final int MODE_LINE = 1;
    final int MODE_RECTANGLE = 2;
    final int MODE_CIRCLE = 3;

    final int MODE_FILL = 10;
    final int MODE_SELECT = 11;
    final int MODE_POLYGON = 12;
    final int MAX_UNDO = 10;

    final float SELECT_TOL = 50.0f;

    Bitmap currentBitmap;
    Bitmap bufferBitmap;
    Bitmap clipBitmap;
    Deque<Bitmap> undoQueue;
    Resources res;
    Canvas currentCanvas;
    int Width;
    int Height;
    boolean resized = false;
    int mode = MODE_RECTANGLE;
    boolean select_selected;
    int temp;
    int currentColor = Color.BLUE;

    float lastX, lastY = -1;
    float saveFX, saveFY ;
    int selectSX, selectSY, selectTX, selectTY;
    int origSX, origSY, origW, origH;
    Path pencilPath;
    EZPhotoPickStorage ezPhotoPickStorage;
    Paint paint;
    private static final int LAYER_FLAGS = Canvas.ALL_SAVE_FLAG;
    PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    public void init()
    {
        Log.d("Debug", "INITTTTTTT!!!");
        measure(Width, Height);
        Width = 1000;
        Height = 700;
        undoQueue = new ArrayDeque<>();

    }

    public myView2(Context context)
    {
        super(context);
        setFocusable(true);
        paint.setAntiAlias(true);
        init();
    }

    public myView2(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public myView2(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    public void drawCircle()
    {
        int x = 400, y = 400;
        Random rnd = new Random();
        int r = rnd.nextInt(300);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#5DCC5C"));
        //currentCanvas.drawCircle(x, y, r, paint);
        //canvas.drawARGB(100, 30, 100, 10);
        Log.d("Debug", "" + getDrawingTime());
        res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_eighth);
        currentCanvas.save();
        currentCanvas.drawBitmap(bitmap, 20, 50, paint);
        currentCanvas.restore();
        deBuffer();
        invalidate();


    }


    public void showPickedPhoto(Bitmap pickedPhoto) {

        paint = new Paint();
        currentCanvas.save();
        //currentCanvas.saveLayerAlpha(0, 0, currentCanvas.getWidth(), currentCanvas.getHeight(), 0x99, LAYER_FLAGS);
        currentCanvas.drawBitmap(pickedPhoto,0,0,paint);
        paint.setXfermode(xfermode);
        currentCanvas.restore();
        invalidate();
        deBuffer();

    }

    void deBuffer()
    {
        undoQueue.addFirst(Bitmap.createBitmap(currentBitmap));
        if(undoQueue.size() > MAX_UNDO)
            undoQueue.removeLast();
        Canvas cv = new Canvas(currentBitmap);
        cv.drawBitmap(bufferBitmap, 0, 0, null);
        clearCanvas(currentCanvas);
        Log.d("Debug", "deBuffer !!!");
    }

    public void undo()
    {
        if(undoQueue.isEmpty()) return;

        setMode(mode);

        Canvas cv = new Canvas(currentBitmap);
        clearCanvas(cv);
        cv.drawBitmap(undoQueue.getFirst(), 0, 0, null);
        undoQueue.removeFirst();
        invalidate();
    }

    public void setMode(int m)
    {
        if(mode == MODE_SELECT)
            selectEndMove();

        clearCanvas(currentCanvas);
        mode = m;
        invalidate();
    }

    public void setColor(int color)
    {
        currentColor = color;
    }

    void clearCanvas(Canvas cv)
    {
        cv.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    void selectEndMove()
    {
        if(!select_selected) return;
        select_selected = false;
        if(selectSX == origSX && selectSY == origSY)
        {
            clearCanvas(currentCanvas);
            return;
        }

        Paint paintW = new Paint();
        paintW.setStyle(Paint.Style.FILL);
        paintW.setColor(Color.WHITE);

        clearCanvas(currentCanvas);
        currentCanvas.drawRect(origSX, origSY, origSX + origW, origSY + origH, paintW);
        currentCanvas.drawBitmap(clipBitmap, selectSX, selectSY, null);

        deBuffer();
    }

    public void saveBitmap()
    {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        String filename = "Image_" + sdf.format(date) + ".png";

        MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                currentBitmap, filename, "Step5 Painter");

        Toast toast = Toast.makeText(getContext(), "Image saved : " + filename, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if(!resized)
        {
            currentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            currentCanvas = new Canvas(bufferBitmap);
            Canvas cv = new Canvas(currentBitmap);
            cv.drawColor(Color.WHITE);
            resized = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(currentBitmap, 0, 0, null);
        canvas.drawBitmap(bufferBitmap, 0, 0, null);
        //canvas.setBitmap(currentBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        int act = event.getAction();

        Paint paint = new Paint();
        paint.setColor(currentColor);
        paint.setStrokeWidth(5);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        if(mode == MODE_PENCIL)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                pencilPath = new Path();
                pencilPath.setLastPoint(x, y);
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                float mx = (x + lastX) / 2;
                float my = (y + lastY) / 2;
                pencilPath.quadTo(lastX, lastY, mx, my);
                clearCanvas(currentCanvas);
                currentCanvas.drawPath(pencilPath, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                pencilPath.lineTo(x+0.1f, y+0.1f);
                clearCanvas(currentCanvas);
                currentCanvas.drawPath(pencilPath, paint);
                pencilPath = null;
                deBuffer();
            }

            lastX = x;
            lastY = y;
        }
        else if(mode == MODE_LINE)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                lastX = x;
                lastY = y;
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawLine(lastX, lastY, x, y, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_POLYGON)
        {

            if(act == MotionEvent.ACTION_DOWN)
            {
                float xP = event.getX();
                float yP = event.getY();
                if(lastX<0)
                {
                    x=xP;
                    y=yP;
                    lastX = x;
                    lastY = y;
                    saveFX = x;
                    saveFY = y;
                }

            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawLine(lastX, lastY, x, y, paint);

            }
            else if(act == MotionEvent.ACTION_UP)
            {
                lastX = x;
                lastY = y;
                currentCanvas.drawLine(lastX,lastY,saveFX,saveFY,paint);
                deBuffer();
            }





        }
        else if(mode == MODE_RECTANGLE)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                lastX = x;
                lastY = y;
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawRect(lastX, lastY, x, y, paint);

            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_CIRCLE)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                lastX = x;
                lastY = y;
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
                clearCanvas(currentCanvas);
                currentCanvas.drawOval(lastX, lastY, x, y, paint);
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_FILL)
        {
            if(act == MotionEvent.ACTION_DOWN)
            {
                clearCanvas(currentCanvas);
                int cx = (int)x, cy = (int)y;
                int origColor = currentBitmap.getPixel(cx, cy);

                currentCanvas.drawBitmap(currentBitmap, 0, 0, null);
                floodFill_array(bufferBitmap, new Point(cx, cy), origColor, currentColor);
            }
            else if(act == MotionEvent.ACTION_MOVE)
            {
            }
            else if(act == MotionEvent.ACTION_UP)
            {
                deBuffer();
            }
        }
        else if(mode == MODE_SELECT)
        {
            Paint paintW = new Paint();
            paintW.setStyle(Paint.Style.FILL);
            paintW.setColor(Color.WHITE);

            Paint paint2 = new Paint();
            paint2.setStrokeWidth(3);
            paint2.setStyle(Paint.Style.STROKE);
            float itvl[] = {10.0f, 10.0f};

            if(select_selected && act == MotionEvent.ACTION_DOWN)
            {
                if(selectSX - x > SELECT_TOL || x - selectSX - origW > SELECT_TOL ||
                        selectSY - y > SELECT_TOL || y - selectSY - origH > SELECT_TOL) {
                    selectEndMove();
                }
            }

            if(!select_selected)
            {
                if (act == MotionEvent.ACTION_DOWN) {
                    selectSX = selectTX = (int)x;
                    selectSY = selectTY = (int)y;
                } else if (act == MotionEvent.ACTION_MOVE) {
                    selectTX = (int)x;
                    selectTY = (int)y;
                    clearCanvas(currentCanvas);
                    paint2.setColor(Color.BLACK);
                    paint2.setPathEffect(new DashPathEffect(itvl, 0.0f));
                    currentCanvas.drawRect(
                            selectSX, selectSY, selectTX, selectTY, paint2);
                    paint2.setColor(Color.WHITE);
                    paint2.setPathEffect(new DashPathEffect(itvl, 10.0f));
                    currentCanvas.drawRect(
                            selectSX, selectSY, selectTX, selectTY, paint2);
                } else if (act == MotionEvent.ACTION_UP) {
                    if(selectSX > selectTX) {
                        int tmp = selectSX;
                        selectSX = selectTX;
                        selectTX = tmp;
                    }
                    if(selectSY > selectTY) {
                        int tmp = selectSY;
                        selectSY = selectTY;
                        selectTY = tmp;
                    }

                    if(selectSX < selectTX && selectSY < selectTY) {
                        select_selected = true;
                        origSX = selectSX;
                        origSY = selectSY;
                        origW = selectTX - selectSX;
                        origH = selectTY - selectSY;
                        clipBitmap = Bitmap.createBitmap(currentBitmap, origSX, origSY, origW, origH);
                    }
                }
            }
            else
            {
                if (act == MotionEvent.ACTION_DOWN) {
                    lastX = x;
                    lastY = y;
                } else if (act == MotionEvent.ACTION_MOVE) {
                    float xdiff = x - lastX;
                    float ydiff = y - lastY;
                    int paintX = selectSX + (int)xdiff;
                    int paintY = selectSY + (int)ydiff;

                    clearCanvas(currentCanvas);
                    currentCanvas.drawRect(origSX, origSY, origSX + origW, origSY + origH, paintW);
                    currentCanvas.drawBitmap(clipBitmap, paintX, paintY, null);

                    paint2.setColor(Color.BLACK);
                    paint2.setPathEffect(new DashPathEffect(itvl, 0.0f));
                    currentCanvas.drawRect(paintX, paintY, paintX + origW, paintY + origH, paint2);
                    paint2.setColor(Color.WHITE);
                    paint2.setPathEffect(new DashPathEffect(itvl, 10.0f));
                    currentCanvas.drawRect(paintX, paintY, paintX + origW, paintY + origH, paint2);
                } else if (act == MotionEvent.ACTION_UP) {
                    float xdiff = x - lastX;
                    float ydiff = y - lastY;
                    selectSX += (int)xdiff;
                    selectSY += (int)ydiff;
                }
            }
        }

        //Log.d("Debug", event.toString());

        invalidate();

        return true;
    }

    private void floodFill_array(Bitmap bmp, Point pt, int targetColor, int replacementColor)
    {
        if(targetColor == replacementColor)
            return;

        int width, height;
        int[] arrPixels;

        width = bmp.getWidth();
        height = bmp.getHeight();

        arrPixels = new int[width*height];
        bmp.getPixels(arrPixels, 0, width, 0, 0, width, height);

        Queue<Point> q = new LinkedList<>();
        q.add(pt);

        while (q.size() > 0) {
            Point n = q.poll();
            if (arrPixels[width*n.y + n.x] != targetColor)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (arrPixels[width*w.y + w.x] == targetColor)) {

                arrPixels[width*w.y + w.x] = replacementColor;  // setPixel

                if ((w.y > 0) && (arrPixels[width*(w.y-1) + w.x] == targetColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < height - 1)
                        && (arrPixels[width*(w.y+1) + w.x] == targetColor))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }

            while ((e.x < width - 1)
                    && (arrPixels[width*e.y + e.x] == targetColor)) {

                arrPixels[width*e.y + e.x] = replacementColor;  // setPixel

                if ((e.y > 0) && (arrPixels[width*(e.y-1) + e.x] == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < height - 1)
                        && (arrPixels[width*(e.y+1) + e.x] == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }

        bmp.setPixels(arrPixels, 0, width, 0, 0, width, height);
    }
}
