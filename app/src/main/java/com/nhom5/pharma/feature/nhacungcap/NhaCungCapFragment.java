package com.nhom5.pharma.feature.nhacungcap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nhom5.pharma.R;

public class NhaCungCapFragment extends Fragment {

    private RecyclerView recyclerView;
    private SupplierAdapter adapter;
    private FirebaseFirestore db;

    public NhaCungCapFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nha_cung_cap, container, false);
        
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewNhaCungCap);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        setupRecyclerView();

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CreateSupplierActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {
        Query query = db.collection("NhaCungCap").orderBy("createdAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Supplier> options = new FirestoreRecyclerOptions.Builder<Supplier>()
                .setQuery(query, Supplier.class)
                .build();

        adapter = new SupplierAdapter(options);
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
