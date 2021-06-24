package com.example.drsfordealer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.drsfordealer.R;
import com.example.drsfordealer.activity.BillScanPage;
import com.google.zxing.Result;

public class ScanQRCode extends AppCompatActivity {

    private CodeScanner scanner;
    private CodeScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.color_PureWhite));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        scannerView = (CodeScannerView) findViewById(R.id.scanner_view);
        scanner = new CodeScanner(this, scannerView);

        scanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BillScanPage.resultTxt.setText(result.getText());
                        onBackPressed();

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        scanner.startPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.startPreview();
    }

}