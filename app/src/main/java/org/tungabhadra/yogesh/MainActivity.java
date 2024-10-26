package org.tungabhadra.yogesh;

import static com.qualcomm.qti.snpe.NeuralNetwork.Runtime.CPU;
import static com.qualcomm.qti.snpe.NeuralNetwork.Runtime.DSP;
import static com.qualcomm.qti.snpe.NeuralNetwork.Runtime.GPU;

import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.qualcomm.qti.platformvalidator.PlatformValidator;
import com.qualcomm.qti.platformvalidator.PlatformValidatorUtil;
import com.qualcomm.qti.snpe.NeuralNetwork;
import com.qualcomm.qti.snpe.SNPE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        PlatformValidator pv = new PlatformValidator(PlatformValidatorUtil.Runtime.DSP);
// To check in general runtime is working use isRuntimeAvailable
        boolean check = pv.isRuntimeAvailable(getApplication());
        System.out.println(check);
// To check Qualcomm (R) Neural Processing SDK runtime is working use runtimeCheck
        check = pv.runtimeCheck(getApplication());
        System.out.println(check);
//To get core version use libVersion api
        String str = pv.coreVersion(getApplication());
        System.out.println(str);
//To get core version use coreVersion api
        str = pv.coreVersion(getApplication());

        System.out.println(str);
        AssetManager assetManager = getAssets();

        try {
            InputStream is = getAssets().open("model.dlc");
            int size = is.available();

            final SNPE.NeuralNetworkBuilder builder;
            builder = new SNPE.NeuralNetworkBuilder(getApplication())
                    // Allows selecting a runtime order for the network.
                    // In the example below use DSP and fall back, in order, to GPU then CPU
                    // depending on whether any of the runtimes is available.
                    .setRuntimeOrder(DSP, GPU, CPU)
                    // Loads a model from DLC file
                    .setModel(is, size);
            final NeuralNetwork network = builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}