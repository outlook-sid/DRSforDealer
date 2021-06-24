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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.RationDealerSessionManager;
import com.example.drsfordealer.database.RationItemModel;
import com.example.drsfordealer.database.TransactionModel;
import com.example.drsfordealer.helpers.RationItemsInfoAdapter;
import com.example.drsfordealer.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BillScanPage extends AppCompatActivity {

    private LinearLayout linearLayoutErrorMsg;
    private LinearLayout linearLayout;
    public static TextView resultTxt;
    private MaterialCardView materialCardViewDemoImg;
    private TextView textViewCustomerID;
    private TextView textViewCustomerName;
    private TextView textViewSelectedDate;
    private TextView textViewScanMessage;
    private TextView textViewSelectedTime;
    private TextView textViewBillPaymentMethod;
    private TextView textViewBillSubtotal;
    private TextView customerCardCount;
    private MaterialButton materialButtonConfirmClaim, materialButtonCancelClaim;
    private MaterialCardView materialCardViewScan;
    private List<RationItemModel> rationItemList;
    private RecyclerView recyclerViewRationItems;
    private ProgressBar progressBar;
    private String today;
    private RationDealerSessionManager dealerSessionManager;
    private HashMap<String, String> dealerDetails;
    private TransactionModel passingTransactionObj;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_scan_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_LiteSeaGreen));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        progressBar = findViewById(R.id.pb_bill_scan);
        resultTxt = findViewById(R.id.tv_result_Txt);
        recyclerViewRationItems = findViewById(R.id.rv_bill_scan_item_details);
        linearLayoutErrorMsg = findViewById(R.id.ll_bill_scan_wrong_qr_code);
        linearLayout = findViewById(R.id.ll_scanned_bill_details);
        materialCardViewScan = findViewById(R.id.mcv_scan_button);
        materialButtonConfirmClaim = findViewById(R.id.mb_bill_scan_confirm_ration);
        materialButtonCancelClaim = findViewById(R.id.mb_bill_scan_decline_ration);
        materialCardViewDemoImg = findViewById(R.id.mcv_scan_qr_code_demo);

        materialCardViewDemoImg.setVisibility(View.VISIBLE);
        linearLayoutErrorMsg.setVisibility(View.GONE);
        linearLayoutErrorMsg.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);

        TextView textViewToday = findViewById(R.id.tv_bill_scan_today);
        textViewCustomerID = findViewById(R.id.tv_bill_scan_uid);
        customerCardCount = findViewById(R.id.tv_bill_scan_user_card_count);
        textViewCustomerName = findViewById(R.id.tv_bill_scan_uname);
        textViewSelectedDate = findViewById(R.id.tv_bill_scan_selected_date);
        textViewScanMessage = findViewById(R.id.tv_bill_scan_message);
        textViewSelectedTime = findViewById(R.id.tv_bill_scan_selected_time);
        textViewBillPaymentMethod = findViewById(R.id.tv_bill_scan_payment_method);
        textViewBillSubtotal = findViewById(R.id.tv_bill_scan_subtotal);

        dealerSessionManager = new RationDealerSessionManager(this);
        dealerDetails = dealerSessionManager.getDealerDetailsFromSession();

        recyclerViewRationItems.setHasFixedSize(false);
        recyclerViewRationItems.setLayoutManager(new LinearLayoutManager(this));

        LocalDateTime nowDate = LocalDateTime.now();
        today = nowDate.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        textViewToday.setText(UtilHelper.prettyDate(today));

        materialCardViewScan.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(this)) {
                showOfflineDialog();
                return;
            }
            startActivity(new Intent(getApplicationContext(), ScanQRCode.class));
        });
        materialCardViewDemoImg.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(this)) {
                showOfflineDialog();
                return;
            }
            startActivity(new Intent(getApplicationContext(), ScanQRCode.class));
        });

        materialButtonCancelClaim.setOnClickListener(v -> {
            linearLayout.setVisibility(View.GONE);
            materialCardViewDemoImg.setVisibility(View.VISIBLE);
        });

    }


    private String sCustomerID;

    private void setAllValues() {
        String stringQRCode = resultTxt.getText().toString();
        if (stringQRCode.isEmpty()) {
            Log.v("_____NOTE_____", "no qr code coming");
            return;
        }
        materialCardViewDemoImg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutErrorMsg.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        Query queryTransaction;
        try {
            queryTransaction = FirebaseDatabase.getInstance().getReference("Transactions").child(stringQRCode);
        } catch (com.google.firebase.database.DatabaseException e) {
            Log.v("_____NOTE_____", "wrong qr code ");
            linearLayoutErrorMsg.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            materialButtonCancelClaim.setClickable(true);
            materialButtonConfirmClaim.setClickable(true);
            materialCardViewScan.setClickable(true);
            return;
        }

        queryTransaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.VISIBLE);
                materialButtonCancelClaim.setClickable(false);
                materialButtonConfirmClaim.setClickable(false);
                materialCardViewScan.setClickable(false);

                if (!dataSnapshot.exists()) {
                    Log.v("_____NOTE_____", "wrong qr code ");
                    linearLayoutErrorMsg.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    materialButtonCancelClaim.setClickable(true);
                    materialButtonConfirmClaim.setClickable(true);
                    materialCardViewScan.setClickable(true);
                    return;
                }

                TransactionModel transaction = dataSnapshot.getValue(TransactionModel.class);
                assert transaction != null;
                Log.v("_____NOTE_____", "phone no " + transaction.getAccountID());
                Log.v("_____NOTE_____", "name " + transaction.getAccountName());

                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("hh:mm");
                String timeString = LocalDateTime.now().format(dateFormat);
                Log.v("_____NOTE_____", " locale " + timeString);
                Log.v("_____NOTE_____", "is in correct time? " + isInCorrectTime(timeString, transaction.getTransactionBookedTime()));
                Log.v("_____NOTE_____", "is in correct day? " + isInCorrectDay(today, transaction.getTransactionBookedDate()));

                String dealerID = dealerDetails.get(RationDealerSessionManager.KEY_DEALER_ID);
                if (!transaction.getDealerID().equals(dealerID)) {
                    linearLayoutErrorMsg.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    materialButtonCancelClaim.setClickable(true);
                    materialButtonConfirmClaim.setClickable(true);
                    materialCardViewScan.setClickable(true);
                    return;
                }

                if (!(isInCorrectTime(timeString, transaction.getTransactionBookedTime()) & isInCorrectDay(today, transaction.getTransactionBookedDate()))) {
                    textViewScanMessage.setText(getString(R.string.customer_in_wrong_time));
                    textViewScanMessage.setTextColor(ContextCompat.getColor(BillScanPage.this, R.color.color_Brown));
                }

                passingTransactionObj = transaction;
                sCustomerID = transaction.getAccountID();
                textViewCustomerID.setText(transaction.getAccountID());
                textViewCustomerName.setText(transaction.getAccountName());
                customerCardCount.setText(String.valueOf(Integer.parseInt(transaction.getAccountCardCount()) + 1));
                textViewSelectedDate.setText(UtilHelper.prettyDate(transaction.getTransactionBookedDate()));
                textViewSelectedTime.setText(transaction.getTransactionBookedTime());
                textViewBillPaymentMethod.setText(transaction.getTransactionPaymentMethod());
                textViewBillSubtotal.setText(transaction.getTransactionSubtotal());

                rationItemList = new ArrayList<>(4);
                rationItemList.add(transaction.getItemRiceInfo());
                rationItemList.add(transaction.getItemWheatInfo());
                rationItemList.add(transaction.getItemSugarInfo());
                rationItemList.add(transaction.getItemKeroseneInfo());

//                rationItemList.add(new RationItemModel("30","12", "Rice"));
//                rationItemList.add(new RationItemModel("20","12", "Wheat"));
//                rationItemList.add(new RationItemModel("20","12", "Sugar"));
//                rationItemList.add(new RationItemModel("20","12", "Kerosene"));

                linearLayout.setVisibility(View.VISIBLE);

                RationItemsInfoAdapter rationItemsInfoAdapter = new RationItemsInfoAdapter(BillScanPage.this, rationItemList);
                recyclerViewRationItems.setAdapter(rationItemsInfoAdapter);
                progressBar.setVisibility(View.GONE);
                materialButtonCancelClaim.setClickable(true);
                materialButtonConfirmClaim.setClickable(true);
                materialCardViewScan.setClickable(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        materialButtonConfirmClaim.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(this)) {
                showOfflineDialog();
                return;
            }

            Intent intent = new Intent(this, InterfaceWithHardware.class);
            intent.putExtra("Transaction_object", passingTransactionObj);
            intent.putExtra("customerID", sCustomerID);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(BillScanPage.this, materialButtonConfirmClaim, "transition_provide_ration");
            startActivity(intent, options.toBundle());
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isInCorrectTime(String localTime, String timeSlot) {

        String[] splitTime = timeSlot.split(" - ");
        String lowerBound = splitTime[0].split(" ")[0];
        String upperBound = splitTime[1].split(" ")[0];
        System.out.println("split   " + lowerBound + "  " + upperBound);
        LocalTime start = LocalTime.parse(lowerBound);
        LocalTime stop = LocalTime.parse(upperBound);

        LocalTime target = LocalTime.parse(localTime);

        return target.isAfter(start) && target.isBefore(stop);
    }

    private boolean isInCorrectDay(String localDate, String selectedDate) {
        return localDate.equals(selectedDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAllValues();
    }

    @Override
    public void onBackPressed() {
        ConstraintLayout constraintLayout = findViewById(R.id.cl_bill_scan_page);
        Intent intent = new Intent(BillScanPage.this, DealerDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(BillScanPage.this, constraintLayout, "transition_Bill_Scan");
        startActivity(intent, options.toBundle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            Intent intent = new Intent(BillScanPage.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dealerSessionManager.logoutFromSession();
            startActivity(intent);
        });

        dialog.show();
    }

}