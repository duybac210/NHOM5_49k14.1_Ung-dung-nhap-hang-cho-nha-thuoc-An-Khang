package com.nhom5.pharma.feature.nhaphang;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class NhapHang {
    private String id;
    private String maID;
    private String maNhaCungCap;
    private String maNguoiNhap;
    private String tenNhaCungCap;
    private double tongTien;
    private int trangThai; // 1 = đã nhập, khác = chờ xử lý
    private Date ngayNhap;
    @ServerTimestamp
    private Date createdAt;
    private List<Map<String, Object>> chiTiet;

    public NhapHang() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMaID() { return maID; }
    public void setMaID(String maID) { this.maID = maID; }

    public String getMaNhaCungCap() { return maNhaCungCap; }
    public void setMaNhaCungCap(String maNhaCungCap) { this.maNhaCungCap = maNhaCungCap; }

    public String getMaNguoiNhap() { return maNguoiNhap; }
    public void setMaNguoiNhap(String maNguoiNhap) { this.maNguoiNhap = maNguoiNhap; }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<Map<String, Object>> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<Map<String, Object>> chiTiet) { this.chiTiet = chiTiet; }

    // Compatibility aliases for older screens/adapters.
    public String getMaNCC() { return maNhaCungCap; }
    public void setMaNCC(String maNCC) { this.maNhaCungCap = maNCC; }

    public Date getNgayTao() { return ngayNhap != null ? ngayNhap : createdAt; }
    public void setNgayTao(Date ngayTao) { this.createdAt = ngayTao; }

    public boolean isTrangThai() { return trangThai == 1; }
}
