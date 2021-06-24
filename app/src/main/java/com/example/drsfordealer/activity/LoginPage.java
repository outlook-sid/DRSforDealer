package com.example.drsfordealer.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.LoginSessionManager;
import com.example.drsfordealer.database.RationDealerModel;
import com.example.drsfordealer.database.RationDealerSessionManager;
import com.example.drsfordealer.helpers.FieldHelper;
import com.example.drsfordealer.helpers.UtilHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    private TextInputLayout loginPageUserID_w, loginPageAccPassword_w;
    private TextInputEditText loginPageUserID, loginPageAccPassword;
    private Button goBtn;
    private CheckBox rememberMeToggleCb;
    private String enteredUserID, enteredPassword;
    private UtilHelper utilHelper;
    private LoginSessionManager rememberMeSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_LitePink));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        loginPageUserID_w = findViewById(R.id.login_ID_wrapper);
        loginPageAccPassword_w = findViewById(R.id.login_password_wrapper);
        loginPageUserID = findViewById(R.id.login_ID);
        loginPageAccPassword = findViewById(R.id.login_password);
        TextView forgotPasswordTvBtn = findViewById(R.id.forgot_password_tvbtn);
        goBtn = findViewById(R.id.login_button);
        Button verifyNoBtn = findViewById(R.id.verify_phone_no_page_button);
        rememberMeToggleCb = findViewById(R.id.remember_me_toggle);

        rememberMeSessionManager = new LoginSessionManager(LoginPage.this);

        if (rememberMeSessionManager.checkLogin()) {
            rememberMeToggleCb.setChecked(true);
            HashMap<String, String> rememberMeDetails = rememberMeSessionManager.getRememberMeDetailsFromSession();
            loginPageUserID.setText(rememberMeDetails.get(LoginSessionManager.USER_ID));
            loginPageAccPassword.setText(rememberMeDetails.get(LoginSessionManager.KEY_PASSWORD));
        }

        goBtn.setOnClickListener(v -> {

            loginPageUserID_w.setError(null);
            loginPageUserID_w.setErrorEnabled(false);
            loginPageAccPassword_w.setError(null);
            loginPageAccPassword_w.setErrorEnabled(false);

            if (!UtilHelper.isConnected(LoginPage.this)) {
                showOfflineDialog();
                return;
            }
            if (!FieldHelper.validateField(loginPageUserID_w) | !FieldHelper.validateField(loginPageAccPassword_w)) {
                return;
            }

            utilHelper = new UtilHelper(LoginPage.this);
            utilHelper.showProgressBar();

            enteredPassword = Objects.requireNonNull(loginPageAccPassword.getText()).toString();
            enteredUserID = Objects.requireNonNull(loginPageUserID.getText()).toString();

            Query queryDealerNode = FirebaseDatabase.getInstance().getReference("Ration_Dealer").orderByChild("dealerID").equalTo(enteredUserID);
            queryDealerNode.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(enteredUserID).exists()) {
                        DataSnapshot snapshot = dataSnapshot.child(enteredUserID);
                        RationDealerModel dealerModel = snapshot.getValue(RationDealerModel.class);
                        assert dealerModel != null;
                        if (!enteredPassword.equals(dealerModel.getDealerPassword())) {
                            loginPageAccPassword_w.setError("incorrect password");
                            Toast.makeText(LoginPage.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            utilHelper.hideProgressBar();
                            return;
                        }
                        if (rememberMeToggleCb.isChecked()) {
                            Log.v("___LOGIN___", "inside checkbox 116");
                            rememberMeSessionManager = new LoginSessionManager(LoginPage.this);
                            rememberMeSessionManager.createRememberMeSession(dealerModel.getDealerID(), dealerModel.getDealerPassword());
                        }else rememberMeSessionManager.logoutUserFromSession();

                        RationDealerSessionManager rationDealerSessionManager = new RationDealerSessionManager(LoginPage.this);
                        rationDealerSessionManager.createRationDealerSession(
                                dealerModel.getDealerID(),
                                dealerModel.getDealerName(),
                                dealerModel.getDealerPassword(),
                                dealerModel.getDealerAccountRecoveryCode(),
                                dealerModel.getDealerLocationState(),
                                dealerModel.getDealerLocationDistrict(),
                                dealerModel.getDealerLocationMunicipality());

                        Intent intent = new Intent(LoginPage.this, DealerDashboardPage.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, goBtn, "transition_go_Button");
                        startActivity(intent, options.toBundle());
                        utilHelper.hideProgressBar();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    } else {
                        Toast.makeText(LoginPage.this, "No such user found!", Toast.LENGTH_SHORT).show();
                        loginPageUserID_w.setError("incorrect ID");
                        utilHelper.hideProgressBar();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginPage.this, "something went wrong!!", Toast.LENGTH_SHORT).show();
                    utilHelper.hideProgressBar();
                }
            });


        });

        verifyNoBtn.setOnClickListener(v -> {
            View view = (View)findViewById(R.id.loginPage_textView1);
            Snackbar.make(view, "Online sign up isn't available", Snackbar.LENGTH_LONG)
                    .setAction("OK", view1 -> {

                    })
                    .setActionTextColor(ContextCompat.getColor(this,R.color.color_LitePurple))
                    .show();

        });

        forgotPasswordTvBtn.setOnClickListener(v -> {
            View view = (View)findViewById(R.id.loginPage_textView1);
            Snackbar.make(view, "Password cannot be reset", Snackbar.LENGTH_LONG)
                    .setAction("OK", view1 -> {

                    })
                    .setActionTextColor(ContextCompat.getColor(this,R.color.color_LitePurple))
                    .show();

        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);

        List<String> permissions = new ArrayList<>();

        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), 111);
        }
    }

    @Override
    public void onBackPressed() {

        Dialog dialog = new Dialog(LoginPage.this);
        dialog.setContentView(R.layout.layout_dialog_exit);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        FloatingActionButton fabExit = dialog.findViewById(R.id.fab_exit_from_app);
        fabExit.setOnClickListener(vi -> {
            dialog.dismiss();
            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finishAffinity();
        });

        dialog.show();

    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(LoginPage.this);
        dialog.setContentView(R.layout.layout_dialog_offline);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        Button positiveButton = dialog.findViewById(R.id.dialog_positive_btn);
        Button negativeButton = dialog.findViewById(R.id.dialog_negative_btn);
        negativeButton.setText(getString(R.string.exit));
        positiveButton.setOnClickListener(vi -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(vi -> {
            dialog.dismiss();
            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //LoginPage.this.finish();
            finishAffinity();
            //this.finish();
        });

        dialog.show();
    }

}