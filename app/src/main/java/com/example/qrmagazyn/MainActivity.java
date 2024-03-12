package com.example.qrmagazyn;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.qrmagazyn.databinding.ActivityMainBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WarehouseDataSource dataSource;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });
//tut deystvie pri skanere
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result ->{
    if (result.getContents() != null) {
        // If QR code is scanned successfully, retrieve the item place from the database
        String itemId = result.getContents();
        String itemPlace = dataSource.getItemPlaceById(itemId);
        if (itemPlace != null) {
            setResult(itemPlace);
        } else {
            Toast.makeText(this, "Item place not found", Toast.LENGTH_SHORT).show();
        }
    } else {
        Toast.makeText(this, "Error scanning QR code", Toast.LENGTH_SHORT).show();
    }
    });

    // Set the result (item place) to a dialog
    private void setResult(String itemPlace) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Item Place: " + itemPlace)
                .setTitle("Result")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
//tut zakanchivaetsya
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.CODE_128);
        options.setPrompt("Zeskanuj kod");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();
        dataSource = new WarehouseDataSource(this); // Initialize WarehouseDataSource
        dataSource.open(); // Open the database connection
    }

    private void initViews() {
        binding.fab.setOnClickListener(v -> checkPermissionAndShowActivity(this));
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context,"Trzeba dostep do kamery", Toast.LENGTH_SHORT).show();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void initBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close(); // Close the database connection
    }
}