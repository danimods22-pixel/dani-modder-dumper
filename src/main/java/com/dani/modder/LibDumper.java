package com.dani.modder;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class LibDumper {
    private Context context;
    private static final String LIB_NAME = "libgame.so";

    public LibDumper(Context context) {
        this.context = context;
    }

    public String dumpLibrary(String processName, String outputDir) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("✓ Library Dumping Started\n");
        result.append("Library: ").append(LIB_NAME).append("\n");

        try {
            // Get PID
            String packageName = extractPackageName(processName);
            String pid = getPid(packageName);
            
            if (pid == null || pid.isEmpty()) {
                return "✗ Process not found";
            }

            result.append("PID: ").append(pid).append("\n");

            // Find base address
            String baseAddress = findBaseAddress(pid);
            if (baseAddress == null) {
                return result.append("✗ ").append(LIB_NAME).append(" not found in process memory").toString();
            }

            result.append("Base Address: 0x").append(baseAddress).append("\n");

            // Get library size
            long libSize = getLibrarySize(pid, baseAddress);
            result.append("Library Size: ").append(formatBytes(libSize)).append("\n");

            // Dump memory
            String dumpPath = outputDir + "/dump.bin";
            boolean success = dumpMemorySection(pid, baseAddress, libSize, dumpPath);

            if (success) {
                result.append("\n✓ Successfully dumped to: ").append(dumpPath).append("\n");
                result.append("Base: 0x").append(baseAddress).append("\n");
                result.append("Size: ").append(formatBytes(libSize));
            } else {
                result.append("✗ Failed to dump library");
            }

        } catch (Exception e) {
            return "✗ Error: " + e.getMessage();
        }

        return result.toString();
    }

    private String extractPackageName(String processName) {
        if (processName.contains("(")) {
            return processName.substring(processName.indexOf("(") + 1, processName.indexOf(")"));
        }
        return processName;
    }

    private String getPid(String packageName) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("pidof", packageName);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        reader.close();
        process.waitFor();
        return line;
    }

    private String findBaseAddress(String pid) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("cat", "/proc/" + pid + "/maps");
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains(LIB_NAME)) {
                String[] parts = line.split("-");
                reader.close();
                process.waitFor();
                return parts[0].trim();
            }
        }

        reader.close();
        process.waitFor();
        return null;
    }

    private long getLibrarySize(String pid, String baseAddress) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("cat", "/proc/" + pid + "/maps");
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains(LIB_NAME)) {
                String[] parts = line.split("-");
                long start = Long.parseLong(parts[0].trim(), 16);
                long end = Long.parseLong(parts[1].split(" ")[0].trim(), 16);
                reader.close();
                process.waitFor();
                return end - start;
            }
        }

        reader.close();
        process.waitFor();
        return 0;
    }

    private boolean dumpMemorySection(String pid, String baseAddress, long size, String outputPath) {
        try {
            String memPath = "/proc/" + pid + "/mem";
            long baseAddr = Long.parseLong(baseAddress, 16);

            FileInputStream fis = new FileInputStream(memPath);
            DataInputStream dis = new DataInputStream(fis);
            FileOutputStream fos = new FileOutputStream(outputPath);
            DataOutputStream dos = new DataOutputStream(fos);

            dis.skipBytes((int) baseAddr);
            byte[] buffer = new byte[8192];
            long remaining = size;

            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int read = dis.read(buffer, 0, toRead);
                if (read < 0) break;
                dos.write(buffer, 0, read);
                remaining -= read;
            }

            dos.close();
            fos.close();
            dis.close();
            fis.close();

            return new File(outputPath).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
