package com.az.flashlight;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;


public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.az.flashlight";
    private boolean hasFlashlight;

    private Camera camera;

    private CameraManager cameraManager;
    private String cameraId;


    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            initialize();
                            if (call.method.equals("on")) {
                                turnLight(true);
                                result.success(null);
                            } else if (call.method.equals("off")) {
                                turnLight(false);
                                result.success(null);
                            }else{
                                result.notImplemented();
                            }
                        }
                );
    }

    private void initialize() {
        hasFlashlight = false;
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing.equals(CameraCharacteristics.LENS_FACING_BACK)) {
                        cameraId = id;
                        hasFlashlight = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            if(camera == null) {
                try {
                    camera = Camera.open();
                    System.out.println("Opened Camera Successfully");
                    System.out.println(hasFlashlight);
                } catch (Exception e) {
                    System.out.println("Failed to get camera: " + e.toString());
                    camera = null;
                }
            }
            hasFlashlight = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        }
    }

    private void turnLight(boolean on) {
        if (hasFlashlight) {
            if (VERSION.SDK_INT > +VERSION_CODES.M) {
                try {
                    cameraManager.setTorchMode(cameraId, on);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                if (camera != null) {
                System.out.println("IN THE BLOCK");
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(on ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    if(on) camera.startPreview();
                    else{
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                }
            }
        }
    }
}
