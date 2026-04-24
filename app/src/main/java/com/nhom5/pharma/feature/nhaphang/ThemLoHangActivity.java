package com.nhom5.pharma.feature.nhaphang;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhom5.pharma.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ThemLoHangActivity extends AppCompatActivity {

    private Spinner spnProductPicker;
    private EditText etMfgDate, etExpDate, etImportPrice, etQuantity;
    private Calendar calendarMfg = Calendar.getInstance();
    private Calendar calendarExp = Calendar.getInstance();
    private FirebaseFirestore db;
    private List<String> productNames = new ArrayList<>();
    private List<String> productIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_lo_hang);
        db = FirebaseFirestore.getInstance();

        initViews();
        setupProductSpinner();
        
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        
        etMfgDate.setOnClickListener(v -> showDatePicker(calendarMfg, etMfgDate));
        etExpDate.setOnClickListener(v -> showDatePicker(calendarExp, etExpDate));
        
        findViewById(R.id.btnSave).setOnClickListener(v -> saveBatchData());
    }

    private void initViews() {
        spnProductPicker = findViewById(R.id.spnProductPicker);
        etMfgDate = findViewById(R.id.etMfgDate);
        etExpDate = findViewById(R.id.etExpDate);
        etImportPrice = findViewById(R.id.etImportPrice);
        etQuantity = findViewById(R.id.etQuantity);
    }

    private void setupProductSpinner() {
        // Lấy danh sách thuốc từ Intent truyền qua từ màn hình 1
        ArrayList<String> names = getIntent().getStringArrayListExtra("SELECTED_PRODUCT_NAMES");
        ArrayList<String> ids = getIntent().getStringArrayListExtra("SELECTED_PRODUCT_IDS");

        if (names != null && ids != null) {
            productNames.addAll(names);
            productIds.addAll(ids);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_spinner_item, productNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnProductPicker.setAdapter(adapter);
        }
    }

    private void showDatePicker(Calendar cal, EditText target) {
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            target.setText(sdf.format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveBatchData() {
        if (etExpDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Hạn sử dụng là bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trả kết quả về màn hình TaoDonNhapActivity để lưu cùng lúc
        Intent data = new Intent();
        data.putExtra("product_id", productIds.get(spnProductPicker.getSelectedItemPosition()));
        data.putExtra("mfg_date", calendarMfg.getTimeInMillis());
        data.putExtra("exp_date", calendarExp.getTimeInMillis());
        data.putExtra("price", Double.parseDouble(etImportPrice.getText().toString()));
        data.putExtra("quantity", Double.parseDouble(etQuantity.getText().toString()));
        
        setResult(RESULT_OK, data);
        finish();
    }
}