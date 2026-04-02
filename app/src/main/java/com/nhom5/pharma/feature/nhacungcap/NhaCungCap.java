package com.nhom5.pharma.feature.nhacungcap;

import java.io.Serializable;

public class NhaCungCap implements Serializable {
    private String id; // ID tự sinh của Firestore
    private String maNCC; // Mã NCC (ví dụ: NCC0001)
    private String tenNCC;
    private String maSoThue;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private double tongMua; // Tổng giá trị nhập hàng

    public NhaCungCap() {}

    public NhaCungCap(String id, String maNCC, String tenNCC, String maSoThue, String soDienThoai, String email, String diaChi, double tongMua) {
        this.id = id;
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.maSoThue = maSoThue;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.tongMua = tongMua;
    }

    // Getters và Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public double getTongMua() { return tongMua; }
    public void setTongMua(double tongMua) { this.tongMua = tongMua; }
}
