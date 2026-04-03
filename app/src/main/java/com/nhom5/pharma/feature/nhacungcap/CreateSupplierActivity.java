package com.nhom5.pharma.feature.nhacungcap;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nhom5.pharma.R;
import com.nhom5.pharma.util.SuccessDialogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Màn hình thêm mới Nhà cung cấp
 */
public class CreateSupplierActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etAddress;
    private Button btnLuu, btnBoQua;
    private ImageView ivBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_supplier);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        initViews();

        // Xử lý sự kiện nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút Bỏ qua
        btnBoQua.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút Lưu
        btnLuu.setOnClickListener(v -> handleSaveSupplier());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        btnLuu = findViewById(R.id.btnLuu);
        btnBoQua = findViewById(R.id.btnBoQua);
        ivBack = findViewById(R.id.ivBack);
    }

    private void handleSaveSupplier() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validate dữ liệu
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuẩn bị dữ liệu lưu vào Firestore
        Map<String, Object> supplier = new HashMap<>();
        supplier.put("name", name);
        supplier.put("phone", phone);
        supplier.put("email", email);
        supplier.put("address", address);
        supplier.put("createdAt", System.currentTimeMillis());

        // Lưu vào collection "suppliers"
        db.collection("suppliers")
                .add(supplier)
                .addOnSuccessListener(documentReference -> {
                    // Hiển thị thông báo thành công bằng Helper
                    SuccessDialogHelper.showSuccessDialog(CreateSupplierActivity.this, "Thêm nhà cung cấp thành công!", () -> {
                        // Khi nhấn nút 'X' (đã cài đặt trong Helper dismiss listener)
                        setResult(RESULT_OK);
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}