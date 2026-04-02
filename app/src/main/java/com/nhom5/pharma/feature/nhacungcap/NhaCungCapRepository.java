package com.nhom5.pharma.feature.nhacungcap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

    // Lấy toàn bộ danh sách nhà cung cấp
    public Query getAllNhaCungCap() {
        return collection.orderBy("tenNCC", Query.Direction.ASCENDING);
    }

    // Thêm nhà cung cấp mới
    public Task<DocumentReference> addNhaCungCap(NhaCungCap ncc) {
        return collection.add(ncc);
    }

    // Cập nhật thông tin
    public Task<Void> updateNhaCungCap(NhaCungCap ncc) {
        if (ncc.getId() == null) {
            // Trường hợp chưa có ID (đề phòng)
            return collection.document().set(ncc);
        }
        return collection.document(ncc.getId()).set(ncc);
    }

    // Xóa nhà cung cấp
    public Task<Void> deleteNhaCungCap(String id) {
        return collection.document(id).delete();
    }
}
