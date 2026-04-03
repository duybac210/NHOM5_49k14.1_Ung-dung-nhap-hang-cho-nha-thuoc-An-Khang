package com.nhom5.pharma.feature.nhaphang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom5.pharma.R;

public class TaoDonNhapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_don_nhap);
        
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }
}