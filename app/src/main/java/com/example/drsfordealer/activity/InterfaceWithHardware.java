package com.example.drsfordealer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.BluetoothMessageSessionManager;
import com.example.drsfordealer.database.TransactionModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InterfaceWithHardware extends AppCompatActivity  {

    private int stepCount = 0;
    private LinearLayout linearLayout1, linearLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_with_hardware);

        MaterialButton materialButtonProvideRation = findViewById(R.id.mb_copy_and_connect);
        MaterialButton materialButtonCommit = findViewById(R.id.mb_complete_transaction);
         linearLayout1 = findViewById(R.id.ll_step_1);
         linearLayout2 = findViewById(R.id.ll_step_2);

        String customerID = getIntent().getStringExtra("customerID");
        TransactionModel transactionModel = (TransactionModel) getIntent().getSerializableExtra("Transaction_object");
        assert transactionModel != null;
        String stringMessageToBeSent = getString(R.string.bluetoothMessage,
                transactionModel.getTransactionRiceAmt(), transactionModel.getTransactionWheatAmt(), transactionModel.getTransactionSugarAmt(), transactionModel.getTransactionKeroseneAmt());
        BluetoothMessageSessionManager bluetoothMessageSessionManager = new BluetoothMessageSessionManager(InterfaceWithHardware.this);
        bluetoothMessageSessionManager.createBTMessageSession(stringMessageToBeSent, customerID);


        materialButtonProvideRation.setOnClickListener(v -> {
            /** Copy code to clipBoard*/
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("code_code", stringMessageToBeSent);
            clipboard.setPrimaryClip(clip);

            /** launch another app to connect to bluetooth*/
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.myapplication");
            if (launchIntent != null) {
                stepCount = 1;
                startActivity(launchIntent);//null pointer check in case package name was not found
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
            }
        });

        materialButtonCommit.setOnClickListener(v -> {
            assert customerID != null;
            DatabaseReference customerIncompleteTransactionRef = FirebaseDatabase.getInstance().getReference("Users").child(customerID).child("userIncompleteTransactions");
            customerIncompleteTransactionRef.setValue(null);
            Toast.makeText(this, "Process is complete", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, DealerDashboardPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        if (stepCount == 1){
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.GONE);
            stepCount = 0;
        }
        else super.onBackPressed();
    }
}
