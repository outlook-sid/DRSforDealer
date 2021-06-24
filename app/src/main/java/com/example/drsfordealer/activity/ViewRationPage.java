package com.example.drsfordealer.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.RationDealerSessionManager;
import com.example.drsfordealer.database.RationItemModel;
import com.example.drsfordealer.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ViewRationPage extends AppCompatActivity {

    private TextView textViewMonthTxt, textViewMonthTxtAlt;
    private TextView textViewPriceRice, textViewMaxRice, textViewPriceWheat, textViewMaxWheat;
    private TextView textViewPriceSugar, textViewMaxSugar, textViewPriceKerosene, textViewMaxKerosene;
    private LinearLayout linearLayoutMiddle, linearLayoutMiddleAlt;
    private List<String> monthList;
    private HashMap<String, RationItemModel> hashMap;
    private ProgressBar progressBar;
    private MaterialButton materialButtonShowMonthList;
    private RationDealerSessionManager dealerSessionManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ration_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_LiteSeaGreen));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        linearLayoutMiddle = findViewById(R.id.ll_view_ration_middle);
        linearLayoutMiddleAlt = findViewById(R.id.ll_view_ration_middle_alt);

        textViewMonthTxt = findViewById(R.id.tv_month_text);
        textViewMonthTxtAlt = findViewById(R.id.tv_month_text_alt);

        textViewPriceRice = findViewById(R.id.tv_view_ration_rice_price);
        textViewMaxRice = findViewById(R.id.tv_view_ration_rice_amt_per_card);
        textViewPriceWheat = findViewById(R.id.tv_view_ration_wheat_price);
        textViewMaxWheat = findViewById(R.id.tv_view_ration_wheat_amt_per_card);
        textViewPriceSugar = findViewById(R.id.tv_view_ration_sugar_price);
        textViewMaxSugar = findViewById(R.id.tv_view_ration_sugar_amt_per_card);
        textViewPriceKerosene = findViewById(R.id.tv_view_ration_kerosene_price);
        textViewMaxKerosene = findViewById(R.id.tv_view_ration_kerosene_amt_per_card);

        progressBar = findViewById(R.id.pb_view_ration);

        materialButtonShowMonthList = findViewById(R.id.mb_view_months);
        MaterialButton materialButtonGoBack = findViewById(R.id.mb_go_back_to_home);

        dealerSessionManager = new RationDealerSessionManager(ViewRationPage.this);

        linearLayoutMiddleAlt.setVisibility(View.GONE);

        materialButtonShowMonthList.setOnClickListener(v -> {
            linearLayoutMiddle.setVisibility(View.INVISIBLE);
            if (!UtilHelper.isConnected(this)) {
                showOfflineDialog();
                return;
            }
            showMonthListDialog();
        });

        materialButtonGoBack.setOnClickListener(v -> onBackPressed());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        linearLayoutMiddle.setVisibility(View.INVISIBLE);

        if (!UtilHelper.isConnected(this)) {
            showOfflineDialog();
            linearLayoutMiddle.setVisibility(View.INVISIBLE);
            return;
        }

        monthList = new ArrayList<>();
        Collections.addAll(monthList, new DateFormatSymbols().getShortMonths());
        String rawDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM"));
        String date = rawDate.split("_")[0] + "_" + monthList.get(Integer.parseInt(rawDate.split("_")[1]) - 1);
        Log.v("______NOTE____", "date:" + date);
        setAllValues(date);

    }

    @Override
    public void onBackPressed() {
        ConstraintLayout constraintLayout = findViewById(R.id.cl_view_ration);
        Intent intent = new Intent(ViewRationPage.this, DealerDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ViewRationPage.this, constraintLayout, "transition_dashboard");
        startActivity(intent, options.toBundle());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showMonthListDialog() {
        monthList = new ArrayList<>();
        Collections.addAll(monthList, new DateFormatSymbols().getShortMonths());
        ArrayAdapter<String> timeSlotsAdapter = new ArrayAdapter<>(this, R.layout.custom_simple_list_layout_1, R.id.list_txt, monthList);

        Dialog dialog = new Dialog(ViewRationPage.this);
        dialog.setContentView(R.layout.layout_dialog_month_list);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        ListView timeSlotLV = dialog.findViewById(R.id.list_view_time_slot_picker);
        timeSlotLV.setAdapter(timeSlotsAdapter);
        timeSlotLV.setOnItemClickListener((parent, view, position, id) -> {
            dialog.dismiss();
            textViewMonthTxt.setText(monthList.get(position));
            Log.v("______NOTE____", "month  " + monthList.get(position));
            String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));

            setAllValues(year + "_" + monthList.get(position));
            Log.v("______NOTE____", year + monthList.get(position));
        });
        dialog.show();

    }

    private void setAllValues(String date) {

        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> dealerDetails = dealerSessionManager.getDealerDetailsFromSession();
        linearLayoutMiddleAlt.setVisibility(View.GONE);
        linearLayoutMiddle.setVisibility(View.GONE);
        textViewMonthTxt.setText(formatDateTxt(date));
        textViewMonthTxtAlt.setText(formatDateTxt(date));

        String id = dealerDetails.get(RationDealerSessionManager.KEY_DEALER_ID);
        Query queryDealer = FirebaseDatabase.getInstance().getReference("Ration_Supplies").orderByChild("ID").equalTo(id);
        queryDealer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    assert id != null;
                    DataSnapshot s1 = snapshot.child(id).child("Month").child(date);
                    if (s1.exists()) {
                        linearLayoutMiddle.setVisibility(View.VISIBLE);
                        linearLayoutMiddleAlt.setVisibility(View.GONE);
                        materialButtonShowMonthList.setClickable(false);
                        hashMap = new HashMap<>();
                        for (DataSnapshot s : s1.getChildren()) {
                            hashMap.put(s.getKey(), s.getValue(RationItemModel.class));
                        }
                        textViewPriceRice.setText(Objects.requireNonNull(hashMap.get("Rice")).getPrice());
                        textViewMaxRice.setText(Objects.requireNonNull(hashMap.get("Rice")).getMaxPerHead());
                        textViewPriceWheat.setText(Objects.requireNonNull(hashMap.get("Wheat")).getPrice());
                        textViewMaxWheat.setText(Objects.requireNonNull(hashMap.get("Wheat")).getMaxPerHead());
                        textViewPriceSugar.setText(Objects.requireNonNull(hashMap.get("Sugar")).getPrice());
                        textViewMaxSugar.setText(Objects.requireNonNull(hashMap.get("Sugar")).getMaxPerHead());
                        textViewPriceKerosene.setText(Objects.requireNonNull(hashMap.get("Kerosene")).getPrice());
                        textViewMaxKerosene.setText(Objects.requireNonNull(hashMap.get("Kerosene")).getMaxPerHead());
                    } else {
                        linearLayoutMiddle.setVisibility(View.GONE);
                        linearLayoutMiddleAlt.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                    materialButtonShowMonthList.setClickable(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String formatDateTxt(String rawDate) {
        return rawDate.split("_")[1] + " " + rawDate.split("_")[0];
    }


    private void showOfflineDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog_offline);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        Button positiveButton = dialog.findViewById(R.id.dialog_positive_btn);
        Button negativeButton = dialog.findViewById(R.id.dialog_negative_btn);
        positiveButton.setOnClickListener(vi -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(vi -> {
            dialog.dismiss();
            Intent intent = new Intent(ViewRationPage.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dealerSessionManager.logoutFromSession();
            startActivity(intent);
        });

        dialog.show();
    }


}