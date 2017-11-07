/*
 * Copyright 2017 Bilal Shaikh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bilal.androidthingscamera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.bilal.androidthingscameralib.InitializeCamera;
import com.bilal.androidthingscameralib.OnPictureAvailableListener;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;

import java.io.IOException;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends Activity implements OnPictureAvailableListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Driver for the button press;
    private ButtonInputDriver mButtonInputDriver;

    //Initialize camera drivers;
    private InitializeCamera mInitializeCamera;

    ImageView imgCaptureImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitUI();
        InitButton();

        // Check permission to access the camera
        if (checkSelfPermission(CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Camera Permission Missing");
            return;
        }

        /**
         * InitializeCamera(
         Current Context,
         Reference Of OnPictureAvailableListener,
         IMAGE_WIDTH,
         IMAGE_HEIGHT,
         MAX_IMAGES);
         **/

        // Recommended configurations
        // IMAGE_WIDTH = 640px,
        // IMAGE_HEIGHT = 480px,
        // MAX_IMAGES = 1;

        mInitializeCamera = new InitializeCamera(this, this, 640, 480, 1);

    }

    void InitUI() {
        imgCaptureImage = (ImageView) findViewById(R.id.imgCaptureImage);
    }

    @SuppressWarnings({"MissingPermission"})
    private void InitButton() {
        try {
            mButtonInputDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForButton(),
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_ENTER);

            mButtonInputDriver.register();

        } catch (IOException e) {
            mButtonInputDriver = null;
            Log.w(TAG, "Could not open GPIO pins", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Make sure to release the camera resources.
        if (mInitializeCamera != null) {
            mInitializeCamera.releaseCameraResources();
        }

        try {
            mButtonInputDriver.close();
        } catch (IOException e) {
            Log.e(TAG, "Button driver error", e);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d(TAG, "Button pressed");
            mInitializeCamera.captureImage();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgCaptureImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPictureAvailable(byte[] imageBytes) {
        onPictureTaken(imageBytes);
    }
}
