package com.example.drsfordealer.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.RationDealerSessionManager;
import com.example.drsfordealer.database.RationItemModel;
import com.example.drsfordealer.helpers.FieldHelper;
import com.example.drsfordealer.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

public class UpdateRation extends AppCompatActivity {

    private MaterialButton materialButtonShowMonths;
    private TextView textViewMonth;
    private TextInputLayout editTextRicePrice, editTextRiceQty;
    private TextInputLayout editTextWheatPrice, editTextWheatQty;
    private TextInputLayout editTextSugarPrice, editTextSugarQty;
    private TextInputLayout editTextKerosenePrice, editTextKeroseneQty;
    private List<String> allMonthYearList;
    private  HashMap<String, String> dealerDetails;
    private int monthIndex;
    private  RationItemModel rice, wheat, sugar, kerosene;
    private RationDealerSessionManager dealerSessionManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ration);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_LiteSeaGreen));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        materialButtonShowMonths = findViewById(R.id.mb_see_months);
        MaterialButton materialButtonUpload = findViewById(R.id.mb_upload_to_database);
        textViewMonth = findViewById(R.id.tv_update_for_month);
        editTextRicePrice = findViewById(R.id.met_update_ration_rice_price);
        editTextRiceQty = findViewById(R.id.met_update_ration_rice_qty);
        editTextWheatPrice = findViewById(R.id.met_update_ration_wheat_price);
        editTextWheatQty = findViewById(R.id.met_update_ration_wheat_qty);
        editTextSugarPrice = findViewById(R.id.met_update_ration_sugar_price);
        editTextSugarQty = findViewById(R.id.met_update_ration_sugar_qty);
        editTextKerosenePrice = findViewById(R.id.met_update_ration_kerosene_price);
        editTextKeroseneQty = findViewById(R.id.met_update_ration_kerosene_qty);

        textViewMonth.setText(null);
        materialButtonShowMonths.setVisibility(View.GONE);

        materialButtonUpload.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(this)) {
                showOfflineDialog();
                return;
            }

            if (!FieldHelper.validatePriceOrQty(editTextRicePrice) | !FieldHelper.validatePriceOrQty(editTextRiceQty) |
                    !FieldHelper.validatePriceOrQty(editTextWheatPrice) | !FieldHelper.validatePriceOrQty(editTextWheatQty) |
                    !FieldHelper.validatePriceOrQty(editTextSugarPrice) | !FieldHelper.validatePriceOrQty(editTextSugarQty) |
                    !FieldHelper.validatePriceOrQty(editTextKerosenePrice) | !FieldHelper.validatePriceOrQty(editTextKeroseneQty)) {

                Toast.makeText(this, "One or more fields are empty", Toast.LENGTH_SHORT).show();

            } else {
                 rice = new RationItemModel(
                        ""+ Objects.requireNonNull(editTextRiceQty.getEditText()).getText().toString(),
                        ""+ Objects.requireNonNull(editTextRicePrice.getEditText()).getText().toString());
                wheat = new RationItemModel(
                        ""+ Objects.requireNonNull(editTextWheatQty.getEditText()).getText().toString(),
                        ""+ Objects.requireNonNull(editTextWheatPrice.getEditText()).getText().toString());
                sugar = new RationItemModel(
                        ""+ Objects.requireNonNull(editTextSugarQty.getEditText()).getText().toString(),
                        ""+ Objects.requireNonNull(editTextSugarPrice.getEditText()).getText().toString());
                kerosene = new RationItemModel(
                        ""+ Objects.requireNonNull(editTextKeroseneQty.getEditText()).getText().toString(),
                        ""+ Objects.requireNonNull(editTextKerosenePrice.getEditText()).getText().toString());

                Log.v("____ITEM_R__", rice.getItemName()+" "+ rice.getMaxPerHead()+" "+ rice.getPrice()+" "+rice.getSubtotal());
                Log.v("____ITEM_W__", wheat.getItemName()+" "+ wheat.getMaxPerHead()+" "+ wheat.getPrice()+" "+wheat.getSubtotal());

                if (textViewMonth.getText().toString().isEmpty()) {
                    showMonthListDialog(allMonthYearList, true);
                }
                else askForConfirmation();
            }

        });

        materialButtonShowMonths.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(this)) {
                showOfflineDialog();
                return;
            }
            textViewMonth.setText(null);
            showMonthListDialog(allMonthYearList, false);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        setupAllEmptyMonth();
    }
    @Override
    public void onBackPressed() {
        ConstraintLayout constraintLayout = findViewById(R.id.cl_update_ration);
        Intent intent = new Intent(UpdateRation.this, DealerDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UpdateRation.this, constraintLayout, "transition_dashboard");
        startActivity(intent, options.toBundle());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupAllEmptyMonth() {

        String currentYear = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        List<String> allMonthList = new ArrayList<>();
        allMonthYearList = new ArrayList<>();
        Collections.addAll(allMonthList, new DateFormatSymbols().getShortMonths());
        for (String s : allMonthList) {
            allMonthYearList.add(currentYear + "_" + s);
            Log.v("__MONTHS__", currentYear + s);
        }

        List<String> availableMonthList = new ArrayList<>();
        dealerSessionManager = new RationDealerSessionManager(this);
        dealerDetails = dealerSessionManager.getDealerDetailsFromSession();
        String id = dealerDetails.get(RationDealerSessionManager.KEY_DEALER_ID);
        Query query = FirebaseDatabase.getInstance().getReference("Ration_Supplies").orderByChild("ID").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assert id != null;
                DataSnapshot snapshot = dataSnapshot.child(id).child("Month");
                for (DataSnapshot s : snapshot.getChildren()) {
                    availableMonthList.add(s.getKey());
                }
                allMonthYearList.removeIf(availableMonthList::contains);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showMonthListDialog(List<String> monthList, boolean forUpload) {

        List<String> formattedMonthList = getOnlySelectedMonthsList(monthList);

        ArrayAdapter<String> timeSlotsAdapter = new ArrayAdapter<>(this, R.layout.custom_simple_list_layout_1, R.id.list_txt, formattedMonthList);

        Dialog dialog = new Dialog(UpdateRation.this);
        dialog.setContentView(R.layout.layout_dialog_month_list);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        ListView timeSlotLV = dialog.findViewById(R.id.list_view_time_slot_picker);
        timeSlotLV.setAdapter(timeSlotsAdapter);
        timeSlotLV.setOnItemClickListener((parent, view, position, id) -> {
            dialog.dismiss();
            monthIndex = position;
            textViewMonth.setText(formatDateTxt(monthList.get(position)));
            Log.v("______NOTE____", "month  " + monthList.get(position));
            materialButtonShowMonths.setVisibility(View.VISIBLE);
            if (forUpload) askForConfirmation();

        });
        dialog.show();

    }

    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Upload Data")
                .setMessage("Are you sure about what you are doing?\n" +
                        "Clicking Yes will commit all data to the database and you won't be able to change them!")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (!UtilHelper.isConnected(this)) {
                        showOfflineDialog();
                        return;
                    }
                    String id = dealerDetails.get(RationDealerSessionManager.KEY_DEALER_ID);
                    assert id != null;
                    DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference("Ration_Supplies").child(id).child("Month").child(allMonthYearList.get(monthIndex));
                    uploadRef.child("Rice").setValue(rice);
                    uploadRef.child("Wheat").setValue(wheat);
                    uploadRef.child("Sugar").setValue(sugar);
                    uploadRef.child("Kerosene").setValue(kerosene);
                    Toast.makeText(this, "Data uploaded...", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private List<String> getOnlySelectedMonthsList(List<String> rawList) {
        List<String> tempList = new ArrayList<>();
        for (String s : rawList) {
            tempList.add(formatDateTxt(s));
        }
        return tempList;
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
            Intent intent = new Intent(UpdateRation.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dealerSessionManager.logoutFromSession();
            startActivity(intent);
        });

        dialog.show();
    }

}