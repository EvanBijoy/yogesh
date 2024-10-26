package org.tungabhadra.yogesh.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import com.qualcomm.qti.snpe.NeuralNetwork;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraHelper implements SurfaceHolder.Callback {
    private final Context context;
    private final SurfaceView cameraSurfaceView;
    private final SurfaceView overlaySurfaceView;
    private final NeuralNetwork neuralNetwork;
    private final ExecutorService cameraExecutor;
    private final OverlayHelper overlayHelper;

    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;
    private Preview preview;

    public CameraHelper(Context context, SurfaceView cameraSurfaceView,
                        SurfaceView overlaySurfaceView, NeuralNetwork neuralNetwork) {
        this.context = context;
        this.cameraSurfaceView = cameraSurfaceView;
        this.overlaySurfaceView = overlaySurfaceView;
        this.neuralNetwork = neuralNetwork;
        this.cameraExecutor = Executors.newSingleThreadExecutor();
        this.overlayHelper = new OverlayHelper(overlaySurfaceView);

        // Set up surface holder callbacks
        cameraSurfaceView.getHolder().addCallback(this);
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;

        // Camera selector
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Preview use case
        preview = new Preview.Builder().build();

        // Image analysis use case
        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        try {
            cameraProvider.unbindAll();
            Camera camera = cameraProvider.bindToLifecycle(
                    (LifecycleOwner) context,
                    cameraSelector,
                    preview,
                    imageAnalysis
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeImage(ImageProxy imageProxy) {
        try {
            Bitmap bitmap = ImageUtils.imageToBitmap(imageProxy);
            runInference(bitmap);
        } finally {
            imageProxy.close();
        }
    }

    private void runInference(Bitmap bitmap) {
        // TODO: Implement the actual inference logic here
        // 1. Prepare the input tensor from bitmap
        // 2. Run the neural network
        // 3. Process the output to get keypoints
        // 4. Use overlayHelper to draw the keypoints

        // Example pseudocode:
        // float[] inputTensor = prepareInputTensor(bitmap);
        // float[] output = neuralNetwork.execute(inputTensor);
        // PointF[] keypoints = processOutput(output);
        // overlayHelper.drawKeypoints(keypoints);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // Surface is created, bind camera use cases
        bindCameraUseCases();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Clean up if needed
    }

    public void shutdown() {
        cameraExecutor.shutdown();
    }
}