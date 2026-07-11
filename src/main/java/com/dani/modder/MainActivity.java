package com.dani.modder;

import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView logView;
    private EditText processInput;
    private Button attachBtn, dumpBtn, searchBtn, exportBtn;
    private ListView appsListView;
    private FridaAttacher fridaAttacher;
    private LibDumper libDumper;
    private MemorySearcher memorySearcher;
    private ExportLogger exportLogger;
    private StringBuilder logBuffer;
    private static final String DUMP_DIR = "/sdcard/DaniModder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize components
        logView = findViewById(R.id.logView);
        processInput = findViewById(R.id.processInput);
        attachBtn = findViewById(R.id.attachBtn);
        dumpBtn = findViewById(R.id.dumpBtn);
        searchBtn = findViewById(R.id.searchBtn);
        exportBtn = findViewById(R.id.exportBtn);
        appsListView = findViewById(R.id.appsListView);

        logBuffer = new StringBuilder();
        fridaAttacher = new FridaAttacher(this);
        libDumper = new LibDumper(this);
        memorySearcher = new MemorySearcher(this);
        exportLogger = new ExportLogger(this);

        // Create dump directory
        createDumpDirectory();

        // Load installed games
        loadInstalledApps();

        // Button listeners
        attachBtn.setOnClickListener(v -> attachFrida());
        dumpBtn.setOnClickListener(v -> dumpMemory());
        searchBtn.setOnClickListener(v -> showSearchDialog());
        exportBtn.setOnClickListener(v -> exportLogs());
    }

    private void createDumpDirectory() {
        File dumpDir = new File(DUMP_DIR);
        if (!dumpDir.exists()) {
            if (dumpDir.mkdirs()) {
                log("✓ Dump directory created at: " + DUMP_DIR);
            } else {
                log("✗ Failed to create dump directory");
            }
        }
    }

    private void loadInstalledApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> appNames = new ArrayList<>();

        for (ApplicationInfo app : packages) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appNames.add(pm.getApplicationLabel(app).toString() + " (" + app.packageName + ")");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNames);
        appsListView.setAdapter(adapter);
        appsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            processInput.setText(selected);
            log("Selected: " + selected);
        });
    }

    private void attachFrida() {
        String processName = processInput.getText().toString().trim();
        if (processName.isEmpty()) {
            Toast.makeText(this, "Pilih proses terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        log("\n[FRIDA ATTACH]");
        log("Target: " + processName);
        log("Attaching Frida...");
        
        new Thread(() -> {
            try {
                String result = fridaAttacher.attachFrida(processName);
                runOnUiThread(() -> log(result));
            } catch (Exception e) {
                runOnUiThread(() -> log("✗ Error: " + e.getMessage()));
            }
        }).start();
    }

    private void dumpMemory() {
        String processName = processInput.getText().toString().trim();
        if (processName.isEmpty()) {
            Toast.makeText(this, "Pilih proses terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        log("\n[MEMORY DUMP]");
        log("Searching libgame.so...");
        
        new Thread(() -> {
            try {
                String result = libDumper.dumpLibrary(processName, DUMP_DIR);
                runOnUiThread(() -> log(result));
            } catch (Exception e) {
                runOnUiThread(() -> log("✗ Error: " + e.getMessage()));
            }
        }).start();
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Memory Value");

        View dialogView = getLayoutInflater().inflate(R.layout.search_dialog, null);
        builder.setView(dialogView);

        EditText searchValue = dialogView.findViewById(R.id.searchValue);
        EditText searchType = dialogView.findViewById(R.id.searchType);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String value = searchValue.getText().toString().trim();
            String type = searchType.getText().toString().trim();
            searchInMemory(value, type);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void searchInMemory(String value, String type) {
        log("\n[MEMORY SEARCH]");
        log("Type: " + type + " | Value: " + value);
        log("Searching...");
        
        new Thread(() -> {
            try {
                String result = memorySearcher.search(value, type);
                runOnUiThread(() -> log(result));
            } catch (Exception e) {
                runOnUiThread(() -> log("✗ Error: " + e.getMessage()));
            }
        }).start();
    }

    private void exportLogs() {
        log("\n[EXPORT LOG]");
        try {
            String filename = "DaniModder_" + new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) + ".txt";
            String filepath = DUMP_DIR + "/" + filename;
            exportLogger.exportLog(logBuffer.toString(), filepath);
            log("✓ Log exported to: " + filepath);
        } catch (Exception e) {
            log("✗ Export failed: " + e.getMessage());
        }
    }

    private void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] " + message + "\n";
        logBuffer.append(logEntry);
        logView.append(logEntry);
    }
}
