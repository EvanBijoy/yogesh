package org.tungabhadra.yogesh;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.qualcomm.qti.platformvalidator.PlatformValidator;
import com.qualcomm.qti.platformvalidator.PlatformValidatorUtil;

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
    }
}