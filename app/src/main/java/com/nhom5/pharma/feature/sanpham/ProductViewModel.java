package com.nhom5.pharma.feature.sanpham;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection("SanPham");

    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private boolean legacySeedCleanupAttempted = false;
    private ListenerRegistration listener;

    public LiveData<List<Product>> getProducts() { return products; }

    public void listenToProducts() {
        startListening();
    }

    private void startListening() {
        if (listener != null) return;

        cleanupLegacySeedOnce();
        ProductSchemaSync.syncOnce(db);

        listener = productsRef.orderBy(com.google.firebase.firestore.FieldPath.documentId(), Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ProductVM", "Listen failed: " + error.getMessage());
                        return;
                    }
                    if (value == null) return;

                    List<Product> list = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        if (Product.isLegacyBadProductId(doc.getId())) continue;
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            list.add(p);
                        }
                    }

                    Collections.sort(list, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            long n1 = Product.extractProductNumber(o1.getId());
                            long n2 = Product.extractProductNumber(o2.getId());
                            if (n1 != n2) {
                                if (n1 < 0) return 1;
                                if (n2 < 0) return -1;
                                return Long.compare(n1, n2);
                            }
                            String id1 = o1.getId() == null ? "" : o1.getId();
                            String id2 = o2.getId() == null ? "" : o2.getId();
                            return id1.compareTo(id2);
                        }
                    });

                    products.setValue(list);
                });
    }

    private void cleanupLegacySeedOnce() {
        if (legacySeedCleanupAttempted) return;
        legacySeedCleanupAttempted = true;

        productsRef.document("SP00001")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        productsRef.document("SP00001").delete();
                    }
                });
    }

    public void saveProduct(Product product) {
        if (product == null) return;

        Map<String, Object> data = buildProductData(product, false);

        if (product.getId() != null && !product.getId().trim().isEmpty()) {
            data.put("maID", product.getId().trim());
            productsRef.document(product.getId().trim()).set(data);
            return;
        }

        db.collection("SanPham")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String nextId = Product.buildNextProductId(snapshot.getDocuments());
                    product.setId(nextId);

                    Map<String, Object> createData = buildProductData(product, true);
                    createData.put("maID", nextId);

                    productsRef.document(nextId).set(createData);
                })
                .addOnFailureListener(e -> Log.e("ProductVM", "Create product failed: " + e.getMessage(), e));
    }

    private Map<String, Object> buildProductData(Product product, boolean isCreate) {
        Map<String, Object> data = new HashMap<>();
        data.put("maID", product.getId());
        data.put("tenSP", product.getTenSP());
        data.put("giavon", product.getGiavon());
        data.put("maVach", product.getMaVach() != null ? product.getMaVach() : "");
        data.put("moTa", product.getMoTa() != null ? product.getMoTa() : "");
        data.put("hangSX", product.getHangSX());
        data.put("nuocSX", product.getNuocSX());
        data.put("trangThai", product.layTrangThaiBoolean());
        data.put("ngayCapNhat", FieldValue.serverTimestamp());

        if (isCreate) {
            data.put("ngayTao", FieldValue.serverTimestamp());
        } else {
            data.put("tenSanPham", FieldValue.delete());
            data.put("giaVon", FieldValue.delete());
            data.put("hangSanXuat", FieldValue.delete());
            data.put("nuocSanXuat", FieldValue.delete());
        }

        return data;
    }

    public void deleteProduct(String id) {
        if (id != null) productsRef.document(id).delete();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listener != null) listener.remove();
    }
}
