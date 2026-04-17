package com.nhom5.pharma.feature.nhaphang;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.nhom5.pharma.feature.lohang.LoHangFilterType;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhapHangRepository {
    private static NhapHangRepository instance;
    private final FirebaseFirestore db;

    private NhapHangRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized NhapHangRepository getInstance() {
        if (instance == null) {
            instance = new NhapHangRepository();
        }
        return instance;
    }

    public Query getAllNhapHang() {
        return db.collection("NhapHang")
                .orderBy("ngayTao", Query.Direction.DESCENDING);
    }

    public Query getAllLoHang() {
        return db.collection("LoHang")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING);
    }

    // Tìm kiếm theo Mã đơn (Document ID) thời gian thực
    public Query searchByMaDon(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllNhapHang();
        }
        
        return db.collection("NhapHang")
                .orderBy(FieldPath.documentId())
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");
    }

    public Query searchLoHang(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllLoHang();
        }

        String keyword = searchText.trim();
        return db.collection("LoHang")
                .orderBy(FieldPath.documentId())
                .startAt(keyword)
                .endAt(keyword + "\uf8ff");
    }

    public Query getLoHangByFilter(int filterType) {
        switch (filterType) {
            case LoHangFilterType.EXPIRING_SOON:
            case LoHangFilterType.EXPIRED:
                // 2 filter nay can tinh (hanSuDung - ngayNhap), xu ly o Adapter.
                return getAllLoHang();
            case LoHangFilterType.LOW_STOCK:
                return getAllLoHang();
            case LoHangFilterType.EXPIRY_ASC:
                return db.collection("LoHang")
                        .orderBy("hanSuDung", Query.Direction.ASCENDING);
            case LoHangFilterType.EXPIRY_DESC:
                return db.collection("LoHang")
                        .orderBy("hanSuDung", Query.Direction.DESCENDING);
            case LoHangFilterType.ALL:
            default:
                return getAllLoHang();
        }
    }

    public Task<DocumentSnapshot> getNhapHangById(String id) {
        return db.collection("NhapHang").document(id).get();
    }

    public Task<DocumentSnapshot> getSupplierById(String maNCC) {
        return db.collection("NhaCungCap").document(maNCC).get();
    }

    // Lấy thông tin tài khoản người nhập từ mã người nhập (String ID)
    public Task<DocumentSnapshot> getUserById(String maNguoiNhap) {
        return db.collection("TaiKhoan").document(maNguoiNhap).get();
    }

    public Task<QuerySnapshot> getLoHangByNhapHangId(String nhapHangId) {
        return db.collection("LoHang")
                .whereEqualTo("maNhapHang", nhapHangId)
                .get();
    }

    public Task<DocumentSnapshot> getLoHangById(String soLo) {
        return db.collection("LoHang").document(soLo).get();
    }

    public Task<DocumentSnapshot> getProductById(String maSP) {
        return db.collection("SanPham").document(maSP).get();
    }

    public Task<Void> upsertLoHang(String soLo, LoHang loHang) {
        if (soLo == null || soLo.trim().isEmpty()) {
            throw new IllegalArgumentException("soLo khong duoc rong");
        }
        if (loHang == null) {
            throw new IllegalArgumentException("loHang khong duoc null");
        }

        loHang.setSoLo(soLo.trim());
        return db.collection("LoHang")
                .document(soLo.trim())
                .set(loHang.toFirestoreMap(), SetOptions.merge());
    }

    public Task<Void> updateLoHangNgaySanXuat(String soLo, Date ngaySanXuat) {
        if (soLo == null || soLo.trim().isEmpty()) {
            throw new IllegalArgumentException("soLo khong duoc rong");
        }
        if (ngaySanXuat == null) {
            throw new IllegalArgumentException("ngaySanXuat khong duoc null");
        }

        Map<String, Object> update = new HashMap<>();
        update.put("ngaySanXuat", ngaySanXuat);
        return db.collection("LoHang")
                .document(soLo.trim())
                .set(update, SetOptions.merge());
    }

    public Task<Void> updateLoHangDonGiaNhap(String soLo, double donGiaNhap) {
        if (soLo == null || soLo.trim().isEmpty()) {
            throw new IllegalArgumentException("soLo khong duoc rong");
        }

        Map<String, Object> update = new HashMap<>();
        update.put("donGiaNhap", donGiaNhap);
        return db.collection("LoHang")
                .document(soLo.trim())
                .set(update, SetOptions.merge());
    }

    public Task<Void> createSampleNhapHangWithLoHang() {
        WriteBatch batch = db.batch();

        DocumentReference nhapHangRef = db.collection("NhapHang").document();
        String nhapHangId = nhapHangRef.getId();

        Map<String, Object> nhapHangData = new HashMap<>();
        nhapHangData.put("maNCC", "NCC_DEMO");
        nhapHangData.put("maNguoiNhap", "USER_DEMO");
        nhapHangData.put("trangThai", true);
        nhapHangData.put("tongTien", 250000d);
        nhapHangData.put("ghiChu", "Tao tu app Android");
        nhapHangData.put("ngayTao", FieldValue.serverTimestamp());
        nhapHangData.put("ngayCapNhat", FieldValue.serverTimestamp());
        batch.set(nhapHangRef, nhapHangData, SetOptions.merge());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 180);
        Date hanSuDung = calendar.getTime();

        String soLo = "LO" + System.currentTimeMillis();
        Map<String, Object> loHangData = new HashMap<>();
        loHangData.put("soLo", soLo);
        loHangData.put("maNhapHang", nhapHangId);
        loHangData.put("maSP", "SP_DEMO");
        loHangData.put("soLuong", 100d);
        loHangData.put("donGiaNhap", 2500d);
        loHangData.put("ngayNhap", FieldValue.serverTimestamp());
        loHangData.put("hanSuDung", hanSuDung);
        loHangData.put("ngayTao", FieldValue.serverTimestamp());
        batch.set(db.collection("LoHang").document(soLo), loHangData, SetOptions.merge());

        return batch.commit();
    }

    public Task<Void> replaceLoHangByNhapHangId(String nhapHangId, List<LoHang> loHangs) {
        if (nhapHangId == null || nhapHangId.trim().isEmpty()) {
            throw new IllegalArgumentException("nhapHangId khong duoc rong");
        }

        WriteBatch batch = db.batch();
        return getLoHangByNhapHangId(nhapHangId).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException() != null
                        ? task.getException()
                        : new IllegalStateException("Khong the tai danh sach lo hang hien tai");
            }

            for (DocumentSnapshot doc : task.getResult()) {
                batch.delete(doc.getReference());
            }

            if (loHangs != null) {
                for (LoHang loHang : loHangs) {
                    if (loHang == null) {
                        continue;
                    }
                    loHang.setMaNhapHang(nhapHangId);
                    String soLo = loHang.getSoLo();
                    if (soLo == null || soLo.trim().isEmpty()) {
                        continue;
                    }
                    batch.set(db.collection("LoHang").document(soLo.trim()), loHang.toFirestoreMap(), SetOptions.merge());
                }
            }

            return batch.commit();
        });
    }


    public Task<Void> deleteNhapHang(String nhapHangId) {
        WriteBatch batch = db.batch();
        batch.delete(db.collection("NhapHang").document(nhapHangId));

        return getLoHangByNhapHangId(nhapHangId).continueWithTask(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    batch.delete(doc.getReference());
                }
            }
            return batch.commit();
        });
    }
}
