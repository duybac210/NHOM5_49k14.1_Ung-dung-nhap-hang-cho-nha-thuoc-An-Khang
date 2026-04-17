package com.nhom5.pharma.feature.nhaphang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom5.pharma.R;

import java.util.Date;

public class NhapHangFragment extends Fragment {

    private RecyclerView recyclerViewNhapHang;
    private EditText searchEditText;
    private FloatingActionButton fabAdd;
    private NhapHangAdapter adapter;
    private final NhapHangRepository repository = NhapHangRepository.getInstance();

    public NhapHangFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nhap_hang, container, false);
        recyclerViewNhapHang = view.findViewById(R.id.recyclerViewNhapHang);
        searchEditText = view.findViewById(R.id.searchEditText);
        fabAdd = view.findViewById(R.id.fabAdd);

        setupRecyclerView();
        setupSearchFunctionality();
        setupCreateSampleSync();
        return view;
    }

    private void setupCreateSampleSync() {
        if (fabAdd == null) {
            return;
        }

        fabAdd.setOnClickListener(v -> repository.createSampleNhapHangWithLoHang()
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Da tao va dong bo du lieu mau len Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Loi dong bo: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void setupRecyclerView() {
        Query query = repository.getAllNhapHang();
        FirestoreRecyclerOptions<NhapHang> options = new FirestoreRecyclerOptions.Builder<NhapHang>()
                .setQuery(query, snapshot -> parseNhapHang(snapshot))
                .build();

        adapter = new NhapHangAdapter(options);
        
        recyclerViewNhapHang.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("RecyclerView", "Chặn lỗi văng app");
                }
            }
        });

        recyclerViewNhapHang.setAdapter(adapter);
    }

    private void setupSearchFunctionality() {
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Query query = repository.searchByMaDon(s.toString().toUpperCase());

                    FirestoreRecyclerOptions<NhapHang> options = new FirestoreRecyclerOptions.Builder<NhapHang>()
                            .setQuery(query, snapshot -> parseNhapHang(snapshot))
                            .build();
                    adapter.updateOptions(options);
                }
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private NhapHang parseNhapHang(com.google.firebase.firestore.DocumentSnapshot snapshot) {
        NhapHang item = new NhapHang();
        item.setId(snapshot.getId());
        item.setMaNCC(firstNonEmpty(snapshot, "maNCC", "MaNCC"));
        item.setMaNguoiNhap(firstNonEmpty(snapshot, "maNguoiNhap", "MaNguoiNhap"));
        item.setTrangThai(firstBoolean(snapshot, "trangThai", "TrangThai"));
        item.setTongTien(firstNumber(snapshot, "tongTien", "TongTien", "totalAmount"));
        item.setGhiChu(firstNonEmpty(snapshot, "ghiChu", "GhiChu", "note"));
        item.setNgayTao(firstDate(snapshot, "ngayTao", "createdAt", "NgayTao"));
        item.setNgayNhap(firstDate(snapshot, "ngayNhap", "NgayNhap"));
        item.setNgayCapNhat(firstDate(snapshot, "ngayCapNhat", "updatedAt", "NgayCapNhat"));
        return item;
    }

    private static String firstNonEmpty(com.google.firebase.firestore.DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            String value = snapshot.getString(key);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }

    private static double firstNumber(com.google.firebase.firestore.DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            Object raw = snapshot.get(key);
            if (raw instanceof Number) {
                return ((Number) raw).doubleValue();
            }
            if (raw instanceof String) {
                try {
                    return Double.parseDouble(((String) raw).trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0d;
    }

    private static boolean firstBoolean(com.google.firebase.firestore.DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            Object raw = snapshot.get(key);
            if (raw instanceof Boolean) {
                return (Boolean) raw;
            }
            if (raw instanceof Number) {
                return ((Number) raw).intValue() == 1;
            }
            if (raw instanceof String) {
                String value = ((String) raw).trim().toLowerCase();
                if ("1".equals(value) || "true".equals(value)) {
                    return true;
                }
                if ("0".equals(value) || "false".equals(value)) {
                    return false;
                }
            }
        }
        return false;
    }

    private static Date firstDate(com.google.firebase.firestore.DocumentSnapshot snapshot, String... keys) {
        for (String key : keys) {
            Object raw = snapshot.get(key);
            if (raw instanceof Date) {
                return (Date) raw;
            }
            if (raw instanceof Timestamp) {
                return ((Timestamp) raw).toDate();
            }
            if (raw instanceof Number) {
                return new Date(((Number) raw).longValue());
            }
        }
        return null;
    }

    @Override public void onStart() { super.onStart(); if (adapter != null) adapter.startListening(); }
    @Override public void onStop() { super.onStop(); if (adapter != null) adapter.stopListening(); }
}
