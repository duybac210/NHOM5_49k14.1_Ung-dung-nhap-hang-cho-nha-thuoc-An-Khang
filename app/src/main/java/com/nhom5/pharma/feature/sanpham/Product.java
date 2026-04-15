package com.nhom5.pharma.feature.sanpham;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

@IgnoreExtraProperties
public class Product {
    private String maID;
    private String ten;
    private double giaVon;
    private double giaBan;
    private String hangSanXuat;
    private String nuocSanXuat;
    private int trangThai;
    @ServerTimestamp
    private Date createdAt;

    public Product() {}

    public String getMaID() { return maID; }
    public void setMaID(String maID) { this.maID = maID; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public double getGiaVon() { return giaVon; }
    public void setGiaVon(double giaVon) { this.giaVon = giaVon; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public String getHangSanXuat() { return hangSanXuat; }
    public void setHangSanXuat(String hangSanXuat) { this.hangSanXuat = hangSanXuat; }

    public String getNuocSanXuat() { return nuocSanXuat; }
    public void setNuocSanXuat(String nuocSanXuat) { this.nuocSanXuat = nuocSanXuat; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
