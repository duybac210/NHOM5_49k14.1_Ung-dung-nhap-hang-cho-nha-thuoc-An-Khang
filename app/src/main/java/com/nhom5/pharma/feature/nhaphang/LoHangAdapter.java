package com.nhom5.pharma.feature.nhaphang;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.nhom5.pharma.R;

import java.util.Locale;

public class LoHangAdapter extends FirestoreRecyclerAdapter<LoHang, LoHangAdapter.LoHangViewHolder> {

    public LoHangAdapter(@NonNull FirestoreRecyclerOptions<LoHang> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LoHangViewHolder holder, int position, @NonNull LoHang model) {
        String soLo = getSnapshots().getSnapshot(position).getId();
        holder.tvMaDon.setText(soLo);

        holder.tvNgayNhap.setText(TextUtils.isEmpty(model.getMaNhapHang()) ? "-" : model.getMaNhapHang());

        double thanhTien = model.getSoLuong() * model.getDonGiaNhap();
        holder.tvTongTien.setText(String.format(Locale.getDefault(), "%,.0fđ", thanhTien));

        String maSP = TextUtils.isEmpty(model.getMaSP()) ? "-" : model.getMaSP();
        holder.tvTrangThai.setText(maSP);
        holder.tvTrangThai.setTextColor(Color.parseColor("#1E3A8A"));

        holder.itemView.setOnClickListener(v -> {
            String maNhapHang = model.getMaNhapHang();
            if (!TextUtils.isEmpty(maNhapHang)) {
                Intent intent = new Intent(v.getContext(), ChiTietNhapHangActivity.class);
                intent.putExtra("NHAP_HANG_ID", maNhapHang);
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public LoHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nhap_hang, parent, false);
        return new LoHangViewHolder(view);
    }

    static class LoHangViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaDon;
        TextView tvNgayNhap;
        TextView tvTongTien;
        TextView tvTrangThai;

        public LoHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaDon = itemView.findViewById(R.id.tvMaDon);
            tvNgayNhap = itemView.findViewById(R.id.tvNgayNhap);
            tvTongTien = itemView.findViewById(R.id.tvTongTien);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
        }
    }
}

