# Android Things Camera Library
Android Things Camera Library is designed to use Raspberry Pi Camera module with Android Thing for various camera support related applications. 
This library builds with the Android framework’s default Camera API. You can capture the images with Android Things Camera Library by setting up a very few lines of code.

## Requirements
- Android Things compatible board.
- Raspberry Pi 3 camera module.
- Latest Android Things Developer Preview 1 or Later.

## Using Android Things Camera Library in your application
If you are building with Gradle, simply add the following line to the dependencies section of your build.gradle file:
```
dependencies {
     compile 'com.bilal:android-things-camera:1.0.0'
}
```
## Getting Started with your own App

- Declare camera permission in Manifest:
```
<uses-permission android:name="android.permission.CAMERA" />
```

- Create instance of camera library class:
```
private InitializeCamera mInitializeCamera;
```

- Initialize camera library with specified fields :
```
//Recommended configurations IMAGE_WIDTH = 640px, IMAGE_HEIGHT = 480px, MAX_IMAGES = 1;
//initializeCamera(Current Context, Reference Of OnPictureAvailableListener, IMAGE_WIDTH, IMAGE_HEIGHT, MAX_IMAGES)

mInitializeCamera = new InitializeCamera(this, this, 640, 480, 1);
```

- Simply capture the image by calling **captureImage()**;
```
mInitializeCamera.captureImage();
```

- Implement the OnPictureAvailableListener, so when captured picture is ready/avilable you can pick that from this Listener :
```
implements OnPictureAvailableListener
	
	@Override
    public void onPictureAvailable(byte[] imageBytes) {
        // Here imageBuf returns the Actual captured image in byte Array
    }	
```

- When you are done with application, call **mInitializeCamera.releaseCameraResources();** to free the Camera resources and stop the execution of the all Background Threads.

```
@Override
    protected void onDestroy() {
        super.onDestroy();

        //Make sure to release the camera resources.
        if (mInitializeCamera != null) {
            mInitializeCamera.releaseCameraResources();
        }
    }
```

## License

Copyright 2017 Bilal Shaikh.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
