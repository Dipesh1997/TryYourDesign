package com.example.tryyourdesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaintingActivity extends AppCompatActivity {

    myView2 mv;
    BetaDialogFragment apd;
    private static final String DEMO_PHOTO_PATH = "MyDemoPhotoDir";
    EZPhotoPickStorage ezPhotoPickStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Debug", "CREATTTTTTTE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);
        mv = (myView2) findViewById(R.id.canvasView);
        apd = new BetaDialogFragment();
        ezPhotoPickStorage = new EZPhotoPickStorage(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE) {
            try {
                Bitmap pickedPhoto = ezPhotoPickStorage.loadLatestStoredPhotoBitmap(1000);
                mv.showPickedPhoto(pickedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            try {
                Bitmap pickedPhoto = ezPhotoPickStorage.loadLatestStoredPhotoBitmap(700);
                mv.showPickedPhoto(pickedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_gallery) {
            EZPhotoPickConfig config = new EZPhotoPickConfig();
            config.photoSource = PhotoSource.GALLERY;
            config.needToExportThumbnail = false;
            config.isAllowMultipleSelect = false;
            config.storageDir = DEMO_PHOTO_PATH;
            config.exportingThumbSize = 200;
            config.exportingSize = 1000;
            EZPhotoPick.startPhotoPickActivity(PaintingActivity.this, config);
            return true;
        }
        if (id == R.id.action_camera) {
            EZPhotoPickConfig config = new EZPhotoPickConfig();
            config.photoSource = PhotoSource.CAMERA;
            config.storageDir = DEMO_PHOTO_PATH;
            config.needToAddToGallery = false;
            config.exportingSize = 1000;
            EZPhotoPick.startPhotoPickActivity(PaintingActivity.this, config);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonClicked(View view)
    {
        mv.drawCircle();
    }

    public void undoClicked(View view)
    {
        mv.undo();
    }

    public void selectTools(View view)
    {
        apd.show(getFragmentManager(), "Beta");
    }

    public void selectPencil(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_PENCIL);
    }
    public void selectLine(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_LINE);
    }
    public void selectRectangle(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_RECTANGLE);
    }
    public void selectCircle(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_CIRCLE);
    }

    public void selectFill(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_FILL);
    }

    public void selectColor(View view)
    {
        AlphaDialogFragment2 bpd = new AlphaDialogFragment2();
        bpd.show(getFragmentManager(), "Alpha");
    }

    public void selectSelect(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_SELECT);
        mv.select_selected = false;
    }
    public void selectPolygon(View view)
    {
        apd.dismiss();
        mv.setMode(mv.MODE_POLYGON);
    }

    public void clickSave(View view)
    {
        mv.saveBitmap();
    }

}


