package org.tungabhadra.yogesh;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.qualcomm.qti.snpe.NeuralNetwork;
import org.tungabhadra.yogesh.helpers.CameraHelper;
import org.tungabhadra.yogesh.helpers.ModelHelper;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private NeuralNetwork neuralNetwork;
    private CameraHelper cameraHelper;
    private ModelHelper modelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkCameraPermission()) {
            initializeComponents();
        } else {
            requestCameraPermission();
        }
    }

    private void initializeComponents() {
        // Initialize model
        modelHelper = new ModelHelper();
        try {
            neuralNetwork = modelHelper.loadModel(getApplication());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load neural network", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize views
        SurfaceView cameraSurfaceView = findViewById(R.id.cameraSurfaceView);
        SurfaceView overlaySurfaceView = findViewById(R.id.overlaySurfaceView);

        // Initialize camera helper with both surfaces
        cameraHelper = new CameraHelper(this, cameraSurfaceView, overlaySurfaceView, neuralNetwork);
        cameraHelper.startCamera();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CAMERA
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeComponents();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (neuralNetwork != null) {
            neuralNetwork.release();
        }
        if (cameraHelper != null) {
            cameraHelper.shutdown();
        }
    }
}