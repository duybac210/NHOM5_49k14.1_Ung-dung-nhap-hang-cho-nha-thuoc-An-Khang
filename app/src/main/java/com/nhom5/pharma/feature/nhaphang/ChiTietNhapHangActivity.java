package com.nhom5.pharma.feature.nhaphang;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.nhom5.pharma.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChiTietNhapHangActivity extends AppCompatActivity {

    private TextView tvOrderCodeTitle, tvTrangThaiHeader, tvNguoiNhap, tvNgayNhapMeta, tvTenNhaCungCap;
    private LinearLayout llChiTiet;
    private final List<LoHang> currentLoHangs = new ArrayList<>();
    private boolean isLoHangLoaded = false;
    private String nhapHangId;
    private NhapHangRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_nhap_hang);

        repository = NhapHangRepository.getInstance();
        nhapHangId = getIntent().getStringExtra("NHAP_HANG_ID");

        initViews();
        loadData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết đơn nhập hàng");
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());

        tvOrderCodeTitle = findViewById(R.id.tvOrderCodeTitle);
        tvTrangThaiHeader = findViewById(R.id.tvTrangThaiHeader);
        tvNguoiNhap = findViewById(R.id.tvNguoiNhap);
        tvNgayNhapMeta = findViewById(R.id.tvNgayNhapMeta);
        tvTenNhaCungCap = findViewById(R.id.tvTenNhaCungCap);
        llChiTiet = findViewById(R.id.llChiTiet);

        findViewById(R.id.btnXoa).setOnClickListener(v -> showDeleteConfirmationDialog());
        findViewById(R.id.btnLuu).setEnabled(false);
        findViewById(R.id.btnLuu).setOnClickListener(v -> syncLoHangToFirebase());
    }

    private void showDeleteConfirmationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setView(dialogView)
                .create();

        Button btnBoQua = dialogView.findViewById(R.id.btnBoQua);
        Button btnXoa = dialogView.findViewById(R.id.btnXoaConfirm);
        TextView tvMessage = dialogView.findViewById(R.id.tvDeleteMessage);

        tvMessage.setText("Xóa phiếu nhập hàng " + (nhapHangId != null ? nhapHangId : "") + "?");

        btnBoQua.setOnClickListener(v -> dialog.dismiss());
        btnXoa.setOnClickListener(v -> {
            dialog.dismiss();
            deleteOrder();
        });

        dialog.show();
    }

    private void deleteOrder() {
        if (nhapHangId == null) return;
        repository.deleteNhapHang(nhapHangId).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
            finish(); 
        });
    }

    private void loadData() {
        if (nhapHangId == null) return;
        repository.getNhapHangById(nhapHangId).addOnSuccessListener(doc -> {
            if (doc.exists()) {
                NhapHang nhapHang = doc.toObject(NhapHang.class);
                if (nhapHang != null) {
                    nhapHang.setId(doc.getId());
                    displayNhapHangInfo(nhapHang);
                    
                    if (nhapHang.getMaNCC() != null) {
                        fetchSupplierName(nhapHang.getMaNCC());
                    }
                    
                    // Truy vấn tên người dùng thay vì để cứng Admin
                    if (nhapHang.getMaNguoiNhap() != null) {
                        fetchUserName(nhapHang.getMaNguoiNhap());
                    } else {
                        tvNguoiNhap.setText("Không xác định");
                    }
                    
                    fetchLoHangList(doc.getId());
                }
            }
        });
    }

    private void displayNhapHangInfo(NhapHang nhapHang) {
        tvOrderCodeTitle.setText(nhapHang.getId());
        if (nhapHang.isTrangThai()) {
            tvTrangThaiHeader.setText("Đã nhập hàng");
            tvTrangThaiHeader.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            tvTrangThaiHeader.setText("Đã hủy");
            tvTrangThaiHeader.setTextColor(Color.parseColor("#F44336"));
        }
        
        if (nhapHang.getNgayTao() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvNgayNhapMeta.setText(sdf.format(nhapHang.getNgayTao()));
        }
    }

    private void fetchSupplierName(String maNCC) {
        repository.getSupplierById(maNCC).addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String name = doc.getString("tenNCC");
                if (name == null) name = doc.getString("TenNCC");
                tvTenNhaCungCap.setText(name);
            }
        });
    }

    private void fetchUserName(String maNguoiNhap) {
        repository.getUserById(maNguoiNhap).addOnSuccessListener(doc -> {
            if (doc.exists()) {
                // Thử cả 2 trường hợp tên trường (tenNguoiDung hoặc hoTen)
                String name = doc.getString("tenNguoiDung");
                if (name == null) name = doc.getString("hoTen");
                if (name == null) name = "Người dùng (" + maNguoiNhap + ")";
                tvNguoiNhap.setText(name);
            } else {
                tvNguoiNhap.setText("Mã: " + maNguoiNhap);
            }
        });
    }

    private void fetchLoHangList(String id) {
        llChiTiet.removeAllViews();
        currentLoHangs.clear();
        isLoHangLoaded = false;
        repository.getLoHangByNhapHangId(id).addOnSuccessListener(snapshot -> {
            for (DocumentSnapshot doc : snapshot) {
                LoHang loHang = new LoHang();
                loHang.setSoLo(doc.getId());
                loHang.setMaNhapHang(firstNonEmpty(doc, "maNhapHang", "MaNhapHang"));
                loHang.setMaSP(firstNonEmpty(doc, "maSP", "MaSP"));
                loHang.setSoLuong(firstNumber(doc, "soLuong", "SoLuong"));
                loHang.setDonGiaNhap(firstNumber(doc, "donGiaNhap", "DonGiaNhap", "giaNhap", "GiaNhap"));
                loHang.setNgayNhap(firstDate(doc, "ngayNhap", "NgayNhap", "ngayTao", "createdAt"));
                loHang.setHanSuDung(firstDate(doc, "hanSuDung", "HanSuDung", "hansudung"));
                loHang.setNgayTao(firstDate(doc, "ngayTao", "createdAt", "NgayTao"));
                currentLoHangs.add(loHang);

                View itemView = LayoutInflater.from(this).inflate(R.layout.item_chi_tiet_lo_hang, llChiTiet, false);
                ((TextView)itemView.findViewById(R.id.tvSoLo)).setText(doc.getId());
                
                Double sl = doc.getDouble("soLuong");
                Double dg = doc.getDouble("donGiaNhap");
                
                double soLuong = sl != null ? sl : 0;
                double donGia = dg != null ? dg : 0;
                
                ((TextView)itemView.findViewById(R.id.tvSoLuong)).setText(String.format(Locale.getDefault(), "%,.0f", soLuong));
                ((TextView)itemView.findViewById(R.id.tvDonGia)).setText(String.format(Locale.getDefault(), "%,.0fđ", donGia));
                ((TextView)itemView.findViewById(R.id.tvThanhTien)).setText(String.format(Locale.getDefault(), "%,.0fđ", soLuong * donGia));
                
                String maSP = doc.getString("maSP");
                if (maSP != null) {
                    repository.getProductById(maSP).addOnSuccessListener(spDoc -> {
                        if (spDoc.exists()) {
                            String tenSP = spDoc.getString("tenSP");
                            if (tenSP == null) tenSP = spDoc.getString("TenSP");
                            ((TextView)itemView.findViewById(R.id.tvTenHang)).setText(tenSP);
                        }
                    });
                }
                llChiTiet.addView(itemView);
            }
            isLoHangLoaded = true;
            findViewById(R.id.btnLuu).setEnabled(true);
        }).addOnFailureListener(e -> {
            isLoHangLoaded = false;
            findViewById(R.id.btnLuu).setEnabled(false);
            Toast.makeText(this, "Khong tai duoc lo hang: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void syncLoHangToFirebase() {
        if (nhapHangId == null) {
            return;
        }

        if (!isLoHangLoaded) {
            Toast.makeText(this, "Du lieu lo hang chua tai xong, vui long thu lai", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.replaceLoHangByNhapHangId(nhapHangId, currentLoHangs)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Đã đồng bộ lô hàng lên Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Đồng bộ thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private static String firstNonEmpty(DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            String value = snapshot.getString(key);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }

    private static double firstNumber(DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            Double value = snapshot.getDouble(key);
            if (value != null) {
                return value;
            }
            Object raw = snapshot.get(key);
            if (raw instanceof Number) {
                return ((Number) raw).doubleValue();
            }
        }
        return 0d;
    }

    private static Date firstDate(DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            Object raw = snapshot.get(key);
            if (raw instanceof Date) {
                return (Date) raw;
            }
            if (raw instanceof com.google.firebase.Timestamp) {
                return ((com.google.firebase.Timestamp) raw).toDate();
            }
        }
        return null;
    }
}
