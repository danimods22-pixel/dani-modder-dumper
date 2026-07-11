package com.dani.modder;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;

public class ExportLogger {
    private Context context;

    public ExportLogger(Context context) {
        this.context = context;
    }

    public void exportLog(String logContent, String filepath) throws Exception {
        File file = new File(filepath);
        File parent = file.getParentFile();

        if (!parent.exists()) {
            parent.mkdirs();
        }

        FileWriter fw = new FileWriter(file);
        fw.write("=== DANI MODDER LOG ===\n");
        fw.write("Generated: " + System.currentTimeMillis() + "\n");
        fw.write("======================================\n\n");
        fw.write(logContent);
        fw.write("\n\n=== END OF LOG ===");
        fw.close();
    }
}
