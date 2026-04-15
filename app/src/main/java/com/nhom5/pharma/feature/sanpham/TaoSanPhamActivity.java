package com.nhom5.pharma.feature.sanpham;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nhom5.pharma.R;
import com.nhom5.pharma.util.SuccessDialogHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public class TaoSanPhamActivity extends AppCompatActivity {

    private EditText etName, etCostPrice, etSellingPrice, etManufacturer, etCountry;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_san_pham);

        db = FirebaseFirestore.getInstance();
        initViews();

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> generateIDAndSave());
    }

    private void initViews() {
        etName = findViewById(R.id.etProductName);
        etCostPrice = findViewById(R.id.etCostPrice);
        etSellingPrice = findViewById(R.id.etSellingPrice);
        etManufacturer = findViewById(R.id.etManufacturer);
        etCountry = findViewById(R.id.etCountry);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void generateIDAndSave() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        db.collection("SanPham")
                .orderBy("maID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String nextId = "SP00001";
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastId = queryDocumentSnapshots.getDocuments().get(0).getString("maID");
                        if (lastId != null && lastId.startsWith("SP")) {
                            int num = Integer.parseInt(lastId.substring(2));
                            nextId = String.format(Locale.getDefault(), "SP%05d", num + 1);
                        }
                    }
                    saveProduct(nextId);
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProduct(String maID) {
        double costPrice = Double.parseDouble(etCostPrice.getText().toString().trim().isEmpty() ? "0" : etCostPrice.getText().toString());
        double sellingPrice = Double.parseDouble(etSellingPrice.getText().toString().trim().isEmpty() ? "0" : etSellingPrice.getText().toString());

        Map<String, Object> product = new HashMap<>();
        product.put("maID", maID);
        product.put("tenSP", etName.getText().toString().trim());
        product.put("giavon", costPrice);
        product.put("giaBan", sellingPrice);
        product.put("hangSX", etManufacturer.getText().toString().trim());
        product.put("nuocSX", etCountry.getText().toString().trim());
        product.put("trangThai", true);
        product.put("ngayTao", com.google.firebase.firestore.FieldValue.serverTimestamp());
        product.put("ngayCapNhat", com.google.firebase.firestore.FieldValue.serverTimestamp());

        // Legacy / cross-screen aliases so other screens still read the same data safely.
        product.put("tenSanPham", etName.getText().toString().trim());
        product.put("giaVon", costPrice);
        product.put("hangSanXuat", etManufacturer.getText().toString().trim());
        product.put("nuocSanXuat", etCountry.getText().toString().trim());
        product.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("SanPham").document(maID).set(product)
                .addOnSuccessListener(aVoid -> {
                    SuccessDialogHelper.showSuccessDialog(this, "Tạo sản phẩm thành công!", () -> {
                        new Handler().postDelayed(this::finish, 1500);
                    });
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
