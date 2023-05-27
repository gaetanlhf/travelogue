package fr.insset.ccm.m1.sag.travelogue.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.insset.ccm.m1.sag.travelogue.databinding.ActivityCameraBinding;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;

public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding viewBinding;
    private ImageCapture imageCapture = null;
    private ExecutorService cameraExecutor;
    private final String TAG = "CameraXApp";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
        viewBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener(view -> {
            takePhoto();
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void takePhoto() {

    }

    private void startCamera() throws ExecutionException, InterruptedException {
//        ProcessCameraProvider cameraProviderFuture = ProcessCameraProvider.getInstance(this).get();
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        ProcessCameraProvider cameraProvider;
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get();
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Preview
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(viewBinding.viewFinder.getSurfaceProvider());

            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview);

            } catch(Exception exc) {
                Log.e(TAG, "Use case binding failed", exc);
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private void requestPermissions() {

    }

    private Boolean allPermissionsGranted() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permissions = PermissionHelper.concatAll(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissions);
        }
        return PermissionHelper.areAllPermissionsGranted(this, permissions);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
