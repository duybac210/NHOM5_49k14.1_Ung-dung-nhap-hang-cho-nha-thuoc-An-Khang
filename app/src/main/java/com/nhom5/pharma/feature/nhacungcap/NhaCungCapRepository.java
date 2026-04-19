package com.nhom5.pharma.feature.nhacungcap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NhaCungCapRepository {
    private static NhaCungCapRepository instance;
    private final FirebaseFirestore db;
    private final CollectionReference collection;

    private NhaCungCapRepository() {
        db = FirebaseFirestore.getInstance();
        collection = db.collection("NhaCungCap");
    }

    public static synchronized NhaCungCapRepository getInstance() {
        if (instance == null) {
            instance = new NhaCungCapRepository();
        }
        return instance;
    }

    // SỬA LẠI: Lấy toàn bộ danh sách, không lọc phức tạp để CHỐNG VĂNG APP
    public Query getAllNhaCungCap() {
        // Trả về collection thuần túy nhất để đảm bảo 100% không bị lỗi Index
        return collection;
    }

    public Task<Void> updateNhaCungCap(NhaCungCap ncc) {
        return collection.document(ncc.getId()).set(ncc);
    }

    public Task<Void> deactivateNhaCungCap(String id) {
        return collection.document(id).update("trangThai", false);
    }
}
