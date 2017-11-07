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

package com.bilal.androidthingscameralib;

import android.content.Context;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by BLACK (Bilal Shaikh) on 11/6/2017.
 */

public class InitializeCamera implements ImageReader.OnImageAvailableListener {

    public static final String TAG = InitializeCamera.class.getSimpleName();

    //Initialize camera library class
    private CameraHelper mCameraHelper;

    //Handler for running Camera tasks in the background.
    private Handler mCameraHandler = new Handler();

    //An additional thread for running Camera tasks that shouldn't block the UI.
    private HandlerThread mCameraHandlerThread;

    OnPictureAvailableListener mOnPictureAvailableListener;

    public InitializeCamera(Context mContext, OnPictureAvailableListener mOnPictureAvailableListener, int imageHeight, int imageWidth, int maxSize) {

        this.mOnPictureAvailableListener = mOnPictureAvailableListener;

        // Creates new Handler Thread for camera operations.
        mCameraHandlerThread = new HandlerThread("CameraBackground");
        mCameraHandlerThread.start();

        // Initialize Camera class.
        mCameraHelper = CameraHelper.getInstance();

        //Recommended configurations IMAGE_WIDTH = 640px, IMAGE_HEIGHT = 480px,MAX_IMAGES = 1;
        mCameraHelper.initializeCameraHelper(mContext, mCameraHandler, this, imageHeight, imageWidth, maxSize);
    }

    public void releaseCameraResources() {

        if (mCameraHandlerThread != null && mCameraHelper != null) {
            Log.d(TAG, " Camera resources released");
            mCameraHelper.shutDown();
            mCameraHandlerThread.quitSafely();
        }
    }

    public void captureImage() {
        mCameraHelper.takePicture();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {

        Image image = reader.acquireNextImage();

        ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
        final byte[] imageBytes = new byte[imageBuf.remaining()];
        imageBuf.get(imageBytes);
        image.close();

        //Post image Bytes Data to Main UI Thread for displaying it in Image View
        mCameraHandler.post(new Runnable() {
            @Override
            public void run() {
                mOnPictureAvailableListener.onPictureAvailable(imageBytes);
                Log.d(TAG, " Image Captured Successfully");
            }
        });
    }
}
