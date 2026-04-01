package com.nhom5.pharma.feature.dangnhap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom5.pharma.R;

public class DangNhapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DangNhapFragment())
                .commit();
        }
    }
}