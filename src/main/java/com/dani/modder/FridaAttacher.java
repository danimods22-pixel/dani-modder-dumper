package com.dani.modder;

import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FridaAttacher {
    private Context context;

    public FridaAttacher(Context context) {
        this.context = context;
    }

    public String attachFrida(String processName) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("✓ Frida Attachment Started\n");
        result.append("Process: ").append(processName).append("\n");

        try {
            // Get PID from process name
            String pid = getPidFromProcessName(processName);
            if (pid == null || pid.isEmpty()) {
                return "✗ Process not found: " + processName;
            }

            result.append("PID: ").append(pid).append("\n");
            result.append("\n--- Frida Script Ready ---\n");
            result.append("Run in terminal:\n");
            result.append("frida -H localhost:27042 -p ").append(pid).append("\n");
            result.append("\nOr use frida-server on device.\n");
            result.append("\n✓ Ready to attach!");

        } catch (Exception e) {
            return "✗ Error: " + e.getMessage();
        }

        return result.toString();
    }

    private String getPidFromProcessName(String processName) throws Exception {
        // Extract package name if format is "AppName (package.name)"
        String packageName = processName;
        if (processName.contains("(")) {
            packageName = processName.substring(processName.indexOf("(") + 1, processName.indexOf(")"));
        }

        ProcessBuilder pb = new ProcessBuilder("pidof", packageName);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        reader.close();
        process.waitFor();

        return line;
    }
}
