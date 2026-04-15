package com.nhom5.pharma.feature.sanpham;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhom5.pharma.R;

public class SanPhamFragment extends Fragment {

    private RecyclerView recyclerView;
    private SanPhamAdapter adapter;
    private boolean isSelectMode = false;

    public SanPhamFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_san_pham, container, false);

        if (getActivity() != null && getActivity().getIntent() != null) {
            isSelectMode = getActivity().getIntent().getBooleanExtra("SELECT_MODE", false);
        }

        recyclerView = view.findViewById(R.id.recyclerViewSanPham);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        com.google.firebase.firestore.Query query = FirebaseFirestore.getInstance()
                .collection("products")
                .orderBy("tenSanPham");

        FirestoreRecyclerOptions<SanPham> options = new FirestoreRecyclerOptions.Builder<SanPham>()
                .setQuery(query, SanPham.class)
                .build();

        adapter = new SanPhamAdapter(options, sanPham -> {
            if (isSelectMode) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("product_id", sanPham.getId());
                resultIntent.putExtra("product_name", sanPham.getTenSanPham());
                resultIntent.putExtra("product_price", sanPham.getGiaVon());
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}
