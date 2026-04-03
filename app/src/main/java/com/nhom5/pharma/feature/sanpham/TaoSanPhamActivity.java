package com.nhom5.pharma.feature.sanpham;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhom5.pharma.R;
import com.nhom5.pharma.util.SuccessDialogHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaoSanPhamActivity extends AppCompatActivity {

    private EditText etName, etCreatedTime, etCostPrice, etSellingPrice, etManufacturer, etCountry;
    private Button btnSave, btnCancel;
    private Calendar calendar = Calendar.getInstance();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_san_pham);

        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etProductName);
        etCreatedTime = findViewById(R.id.etCreatedTime);
        etCostPrice = findViewById(R.id.etCostPrice);
        etSellingPrice = findViewById(R.id.etSellingPrice);
        etManufacturer = findViewById(R.id.etManufacturer);
        etCountry = findViewById(R.id.etCountry);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        etCreatedTime.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etCreatedTime.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String costPrice = etCostPrice.getText().toString().trim();
        String sellingPrice = etSellingPrice.getText().toString().trim();

        if (name.isEmpty() || costPrice.isEmpty() || sellingPrice.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> product = new HashMap<>();
        product.put("tenSanPham", name);
        product.put("giaVon", Double.parseDouble(costPrice));
        product.put("giaBan", Double.parseDouble(sellingPrice));
        product.put("hangSanXuat", etManufacturer.getText().toString());
        product.put("nuocSanXuat", etCountry.getText().toString());
        product.put("thoiGianTao", calendar.getTimeInMillis());
        product.put("timestamp", System.currentTimeMillis());

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    SuccessDialogHelper.showSuccessDialog(TaoSanPhamActivity.this, "Tạo sản phẩm thành công!", () -> {
                        setResult(RESULT_OK);
                        finish();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}