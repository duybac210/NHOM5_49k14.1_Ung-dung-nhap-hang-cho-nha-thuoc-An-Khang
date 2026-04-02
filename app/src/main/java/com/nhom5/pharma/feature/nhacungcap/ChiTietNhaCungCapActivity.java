package com.nhom5.pharma.feature.nhacungcap;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom5.pharma.R;
import java.text.DecimalFormat;

public class ChiTietNhaCungCapActivity extends AppCompatActivity {

    private NhaCungCap ncc;
    private boolean isEditMode = false;
    private NhaCungCapRepository repository;

    // View cho màn hình Chi tiết
    private TextView tvTen, tvMa, tvMST, tvSDT, tvEmail, tvDiaChi, tvTongDon, tvGiaTri;
    
    // View cho màn hình Chỉnh sửa (trong layout edit)
    private EditText edtSDT, edtEmail, edtDiaChi, edtMaNCC, edtMST;
    private TextView tvEditTen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = NhaCungCapRepository.getInstance();
        
        // Nhận dữ liệu từ Intent
        ncc = (NhaCungCap) getIntent().getSerializableExtra("NHA_CUNG_CAP");
        
        showDetailLayout();
    }

    private void showDetailLayout() {
        setContentView(R.layout.activity_chi_tiet_nha_cung_cap);
        isEditMode = false;
        initDetailViews();
        bindDataToDetail();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> showDeleteDialog());
        findViewById(R.id.btnEdit).setOnClickListener(v -> showEditLayout());
    }

    private void showEditLayout() {
        setContentView(R.layout.activity_edit_nha_cung_cap);
        isEditMode = true;
        initEditViews();
        bindDataToEdit();

        findViewById(R.id.btnBackEdit).setOnClickListener(v -> showDetailLayout());
        findViewById(R.id.btnCancelEdit).setOnClickListener(v -> showDetailLayout());
        findViewById(R.id.btnSaveEdit).setOnClickListener(v -> saveChanges());
    }

    private void initDetailViews() {
        tvTen = findViewById(R.id.tvDetailTenNCC);
        tvMa = findViewById(R.id.tvDetailMaNCC);
        tvMST = findViewById(R.id.tvDetailMST);
        tvSDT = findViewById(R.id.tvDetailSDT);
        tvEmail = findViewById(R.id.tvDetailEmail);
        tvDiaChi = findViewById(R.id.tvDetailDiaChi);
        tvTongDon = findViewById(R.id.tvDetailTongDon);
        tvGiaTri = findViewById(R.id.tvDetailGiaTri);
    }

    private void bindDataToDetail() {
        if (ncc == null) return;
        tvTen.setText(ncc.getTenNCC());
        tvMa.setText("Mã NCC: " + ncc.getMaNCC());
        tvMST.setText("Mã số thuế: " + ncc.getMaSoThue());
        tvSDT.setText(ncc.getSoDienThoai());
        tvEmail.setText(ncc.getEmail());
        tvDiaChi.setText(ncc.getDiaChi());
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvGiaTri.setText(formatter.format(ncc.getTongMua()));
    }

    private void initEditViews() {
        tvEditTen = findViewById(R.id.tvEditTenNCC);
        edtMaNCC = findViewById(R.id.edtEditMaNCC);
        edtMST = findViewById(R.id.edtEditMST);
        edtSDT = findViewById(R.id.edtEditSDT);
        edtEmail = findViewById(R.id.edtEditEmail);
        edtDiaChi = findViewById(R.id.edtEditDiaChi);

        // Khóa mã NCC và MST đúng như yêu cầu
        edtMaNCC.setEnabled(false);
        edtMST.setEnabled(false);
    }

    private void bindDataToEdit() {
        tvEditTen.setText(ncc.getTenNCC());
        edtMaNCC.setText(ncc.getMaNCC());
        edtMST.setText(ncc.getMaSoThue());
        edtSDT.setText(ncc.getSoDienThoai());
        edtEmail.setText(ncc.getEmail());
        edtDiaChi.setText(ncc.getDiaChi());
    }

    private void saveChanges() {
        ncc.setSoDienThoai(edtSDT.getText().toString());
        ncc.setEmail(edtEmail.getText().toString());
        ncc.setDiaChi(edtDiaChi.getText().toString());

        repository.updateNhaCungCap(ncc).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            showDetailLayout();
        });
    }

    private void showDeleteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete_ncc);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvMsg = dialog.findViewById(R.id.tvDeleteMessage);
        tvMsg.setText("Hệ thống sẽ xóa hoàn toàn nhà cung cấp " + ncc.getTenNCC() + " nhưng vẫn giữ những giao dịch lịch sử nếu có. Bạn có chắc là muốn xóa?");

        dialog.findViewById(R.id.btnSkip).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        
        dialog.findViewById(R.id.btnConfirmDelete).setOnClickListener(v -> {
            repository.deleteNhaCungCap(ncc.getId()).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã xóa nhà cung cấp", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            });
        });

        dialog.show();
    }
}
