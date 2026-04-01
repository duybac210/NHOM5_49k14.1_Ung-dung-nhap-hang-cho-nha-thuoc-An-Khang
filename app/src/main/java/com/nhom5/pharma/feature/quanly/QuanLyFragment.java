package com.nhom5.pharma.feature.quanly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhom5.pharma.R;
import com.nhom5.pharma.feature.dangnhap.DangNhapActivity;

public class QuanLyFragment extends Fragment {

    private RelativeLayout btnBasicInfo, btnChangePassword, btnBackup, btnLogout;
    private LinearLayout expandableBasicInfo;
    private ImageView ivArrowBasicInfo;
    private TextView tvUserNameHeader, tvFullNameDetail, tvPhoneDetail, tvEmailDetail, tvAddressDetail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isBasicInfoExpanded = false;

    public QuanLyFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quan_ly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header views
        tvUserNameHeader = view.findViewById(R.id.tvUserNameHeader);

        // Basic Info Detail views
        tvFullNameDetail = view.findViewById(R.id.tvFullNameDetail);
        tvPhoneDetail = view.findViewById(R.id.tvPhoneDetail);
        tvEmailDetail = view.findViewById(R.id.tvEmailDetail);
        tvAddressDetail = view.findViewById(R.id.tvAddressDetail);

        // Action views
        btnBasicInfo = view.findViewById(R.id.btnBasicInfo);
        expandableBasicInfo = view.findViewById(R.id.expandableBasicInfo);
        ivArrowBasicInfo = view.findViewById(R.id.ivArrowBasicInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnBackup = view.findViewById(R.id.btnBackup);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadUserInfo();
        setupListeners();
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmailDetail.setText(user.getEmail());
            db.collection("Users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String phone = documentSnapshot.getString("phone");
                        String address = documentSnapshot.getString("address");
                        
                        if (fullName != null) {
                            tvUserNameHeader.setText(fullName);
                            tvFullNameDetail.setText(fullName);
                        }
                        if (phone != null) tvPhoneDetail.setText(phone);
                        if (address != null) tvAddressDetail.setText(address);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Không thể tải thông tin chi tiết", Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void setupListeners() {
        btnBasicInfo.setOnClickListener(v -> {
            isBasicInfoExpanded = !isBasicInfoExpanded;
            if (isBasicInfoExpanded) {
                expandableBasicInfo.setVisibility(View.VISIBLE);
                ivArrowBasicInfo.setRotation(90);
            } else {
                expandableBasicInfo.setVisibility(View.GONE);
                ivArrowBasicInfo.setRotation(0);
            }
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Triển khai hướng 1: Thông báo đồng bộ thời gian thực
        btnBackup.setOnClickListener(v -> showBackupInfoDialog());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), DangNhapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void showBackupInfoDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Sao lưu & Phục hồi")
                .setIcon(R.drawable.ic_backup)
                .setMessage("Dữ liệu của bạn luôn được hệ thống tự động đồng bộ hóa và sao lưu thời gian thực trên đám mây của Pharma An Khang.\n\n" +
                        "Trong trường hợp thay đổi thiết bị hoặc cài đặt lại ứng dụng, toàn bộ dữ liệu sẽ tự động được phục hồi ngay khi bạn đăng nhập tài khoản.")
                .setPositiveButton("Đã hiểu", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showChangePasswordDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etCurrentPassword = dialog.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialog.findViewById(R.id.etConfirmPassword);
        TextView tvCurrentLabel = dialog.findViewById(R.id.tvCurrentPasswordLabel);
        View layoutCurrent = dialog.findViewById(R.id.layoutCurrentPassword);
        
        ImageView ivEye1 = dialog.findViewById(R.id.ivEyeIcon1);
        ImageView ivEye2 = dialog.findViewById(R.id.ivEyeIcon2);
        Button btnSave = dialog.findViewById(R.id.btnSavePassword);

        View.OnClickListener eyeToggle = v -> {
            EditText target = (v.getId() == R.id.ivEyeIcon1) ? etNewPassword : etConfirmPassword;
            ImageView eye = (ImageView) v;
            if (target.getTransformationMethod() instanceof PasswordTransformationMethod) {
                target.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eye.setImageResource(R.drawable.ic_eye);
            } else {
                target.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eye.setImageResource(R.drawable.ic_eye_off);
            }
            target.setSelection(target.length());
        };
        ivEye1.setOnClickListener(eyeToggle);
        ivEye2.setOnClickListener(eyeToggle);

        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String p1 = etNewPassword.getText().toString().trim();
                String p2 = etConfirmPassword.getText().toString().trim();
                String cp = etCurrentPassword.getText().toString().trim();
                boolean isReAuthVisible = layoutCurrent.getVisibility() == View.VISIBLE;
                
                boolean basicValid = !p1.isEmpty() && p1.equals(p2) && p1.length() >= 6;
                if (isReAuthVisible) {
                    btnSave.setEnabled(basicValid && !cp.isEmpty());
                } else {
                    btnSave.setEnabled(basicValid);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        etNewPassword.addTextChangedListener(tw);
        etConfirmPassword.addTextChangedListener(tw);
        etCurrentPassword.addTextChangedListener(tw);

        btnSave.setOnClickListener(v -> {
            String newP = etNewPassword.getText().toString().trim();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;

            if (layoutCurrent.getVisibility() == View.VISIBLE) {
                String currentP = etCurrentPassword.getText().toString().trim();
                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentP))
                    .addOnCompleteListener(reAuthTask -> {
                        if (reAuthTask.isSuccessful()) {
                            user.updatePassword(newP).addOnCompleteListener(finalTask -> {
                                if (finalTask.isSuccessful()) {
                                    Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                user.updatePassword(newP).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                            tvCurrentLabel.setVisibility(View.VISIBLE);
                            layoutCurrent.setVisibility(View.VISIBLE);
                            btnSave.setEnabled(false);
                            Toast.makeText(getContext(), "Vì lý do bảo mật, vui lòng xác nhận mật khẩu hiện tại", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }
}