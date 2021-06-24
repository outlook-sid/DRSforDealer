package com.example.drsfordealer.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.RationDealerSessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Objects;

public class DealerDashboardPage extends AppCompatActivity {

    private MaterialButton materialButtonScanQR, materialButtonUpdateRation, materialButtonViewRation;
    private RationDealerSessionManager dealerSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_dashboard_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_Pale));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TextView textViewDealerName = findViewById(R.id.tv_dealer_name);
        TextView textViewDealerID = findViewById(R.id.tv_dealer_id);
        TextView textViewDealerLocation1 = findViewById(R.id.tv_dealer_location_1);
        TextView textViewDealerLocation2 = findViewById(R.id.tv_dealer_location_2);
        TextView textViewDealerLocation3 = findViewById(R.id.tv_dealer_location_3);

        materialButtonUpdateRation = findViewById(R.id.mb_update_ration);
        materialButtonViewRation = findViewById(R.id.mb_view_ration);
        materialButtonScanQR = findViewById(R.id.mb_scan_qr);

        dealerSessionManager = new RationDealerSessionManager(DealerDashboardPage.this);
        HashMap<String, String> dealerDetails = dealerSessionManager.getDealerDetailsFromSession();

        textViewDealerName.setText(dealerDetails.get(RationDealerSessionManager.KEY_DEALER_NAME));
        textViewDealerID.setText(dealerDetails.get(RationDealerSessionManager.KEY_DEALER_ID));
        textViewDealerLocation1.setText(dealerDetails.get(RationDealerSessionManager.KEY_DEALER_LOCATION_1));
        textViewDealerLocation2.setText(dealerDetails.get(RationDealerSessionManager.KEY_DEALER_LOCATION_2));
        textViewDealerLocation3.setText(dealerDetails.get(RationDealerSessionManager.KEY_DEALER_LOCATION_3));

        TextView textViewSignOut = findViewById(R.id.tv_dashboard_logout);


        textViewSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dealerSessionManager.logoutFromSession();
            startActivity(intent);

        });


        materialButtonUpdateRation.setOnClickListener(v -> {
            Intent intent = new Intent(DealerDashboardPage.this, UpdateRation.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DealerDashboardPage.this, materialButtonUpdateRation, "transition_Update_Ration");
            startActivity(intent, options.toBundle());
        });

        materialButtonViewRation.setOnClickListener(v -> {
            Intent intent = new Intent(DealerDashboardPage.this, ViewRationPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DealerDashboardPage.this, materialButtonViewRation, "transition_View_Ration");
            startActivity(intent, options.toBundle());
        });

        materialButtonScanQR.setOnClickListener(v -> {
            Intent intent = new Intent(DealerDashboardPage.this, BillScanPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DealerDashboardPage.this, materialButtonScanQR, "transition_Bill_Scan");
            startActivity(intent, options.toBundle());
        });

    }

    @Override
    public void onBackPressed() {

        Dialog dialog = new Dialog(DealerDashboardPage.this);
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

}