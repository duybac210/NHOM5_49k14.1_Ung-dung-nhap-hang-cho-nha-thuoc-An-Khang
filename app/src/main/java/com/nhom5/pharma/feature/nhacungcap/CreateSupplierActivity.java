package com.nhom5.pharma.feature.nhacungcap;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nhom5.pharma.R;
import com.nhom5.pharma.util.SuccessDialogHelper;

import java.util.HashMap;
import java.util.Map;

public class CreateSupplierActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etAddress;
    private Button btnLuu, btnBoQua;
    private ImageView ivBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_supplier);

        db = FirebaseFirestore.getInstance();
        initViews();

        ivBack.setOnClickListener(v -> finish());
        btnBoQua.setOnClickListener(v -> finish());
        btnLuu.setOnClickListener(v -> generateNCCIDAndSave());
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

    private void generateNCCIDAndSave() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên nhà cung cấp", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLuu.setEnabled(false);
        // Lấy NCC mới nhất từ collection "NhaCungCap"
        db.collection("NhaCungCap")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String nextId = "NCC0001";
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        if (lastId.startsWith("NCC")) {
                            try {
                                int num = Integer.parseInt(lastId.substring(3));
                                nextId = String.format("NCC%04d", num + 1);
                            } catch (Exception e) {
                                // Fallback if ID format is weird
                            }
                        }
                    }
                    saveSupplier(nextId);
                })
                .addOnFailureListener(e -> {
                    btnLuu.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveSupplier(String maID) {
        Map<String, Object> supplier = new HashMap<>();
        supplier.put("maID", maID);
        supplier.put("tenNhaCungCap", etName.getText().toString().trim());
        supplier.put("phone", etPhone.getText().toString().trim());
        supplier.put("email", etEmail.getText().toString().trim());
        supplier.put("address", etAddress.getText().toString().trim());
        supplier.put("trangThai", 1);
        supplier.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        // Lưu với ID tự tạo NCCxxxx
        db.collection("NhaCungCap").document(maID).set(supplier)
                .addOnSuccessListener(aVoid -> {
                    SuccessDialogHelper.showSuccessDialog(this, "Thêm nhà cung cấp thành công!", () -> {
                        new Handler().postDelayed(this::finish, 1500);
                    });
                })
                .addOnFailureListener(e -> {
                    btnLuu.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
