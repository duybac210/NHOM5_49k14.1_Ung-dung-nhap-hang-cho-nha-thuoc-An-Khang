package com.nhom5.pharma.feature.nhaphang;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom5.pharma.R;
import java.text.DecimalFormat;
import java.util.List;

public class SelectedProductAdapter extends RecyclerView.Adapter<SelectedProductAdapter.ViewHolder> {

    private List<SelectedProduct> productList;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public SelectedProductAdapter(List<SelectedProduct> productList, OnQuantityChangeListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SelectedProduct product = productList.get(position);
        
        holder.tvProductName.setText(product.getTenSanPham());
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvProductPrice.setText(formatter.format(product.getDonGia()) + "đ/hộp");

        // Tránh vòng lặp vô tận khi setText kích hoạt TextWatcher
        holder.isUpdating = true;
        holder.etQuantity.setText(String.valueOf(product.getSoLuong()));
        holder.isUpdating = false;

        // Cập nhật trạng thái Chip Lô
        if (product.isHasBatch()) {
            holder.tvBatchStatus.setText("Đã có lô");
            holder.tvBatchStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvBatchStatus.setBackgroundResource(R.drawable.bg_chip_success);
        } else {
            holder.tvBatchStatus.setText("Chưa có lô");
            holder.tvBatchStatus.setTextColor(Color.parseColor("#E64A19"));
            holder.tvBatchStatus.setBackgroundResource(R.drawable.bg_chip_warning);
        }

        holder.btnPlus.setOnClickListener(v -> {
            int newQty = product.getSoLuong() + 1;
            product.setSoLuong(newQty);
            notifyItemChanged(position);
            if (listener != null) listener.onQuantityChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (product.getSoLuong() > 1) {
                int newQty = product.getSoLuong() - 1;
                product.setSoLuong(newQty);
                notifyItemChanged(position);
            } else {
                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());
            }
            if (listener != null) listener.onQuantityChanged();
        });

        // Xử lý nhập trực tiếp từ bàn phím
        holder.etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.isUpdating) return;
                try {
                    int val = Integer.parseInt(s.toString());
                    if (val > 0) {
                        product.setSoLuong(val);
                        if (listener != null) listener.onQuantityChanged();
                    }
                } catch (NumberFormatException ignored) {}
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvBatchStatus;
        EditText etQuantity;
        View btnMinus, btnPlus;
        boolean isUpdating = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            etQuantity = itemView.findViewById(R.id.etQuantity);
            tvBatchStatus = itemView.findViewById(R.id.tvBatchStatus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}