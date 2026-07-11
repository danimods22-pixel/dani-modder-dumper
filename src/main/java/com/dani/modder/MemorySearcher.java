package com.dani.modder;

import android.content.Context;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MemorySearcher {
    private Context context;
    private static final String DUMP_FILE = "/sdcard/DaniModder/dump.bin";

    public MemorySearcher(Context context) {
        this.context = context;
    }

    public String search(String value, String type) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("✓ Memory Search Started\n");
        result.append("Type: ").append(type).append(" | Value: ").append(value).append("\n\n");

        File dumpFile = new File(DUMP_FILE);
        if (!dumpFile.exists()) {
            return "✗ Dump file not found. Please dump memory first.";
        }

        try {
            List<Long> results = new ArrayList<>();

            switch (type.toLowerCase()) {
                case "int":
                    results = searchInteger(value);
                    break;
                case "float":
                    results = searchFloat(value);
                    break;
                case "string":
                    results = searchString(value);
                    break;
                default:
                    return "✗ Unknown type. Use: int, float, or string";
            }

            result.append("Found ").append(results.size()).append(" matches:\n");
            for (int i = 0; i < Math.min(results.size(), 50); i++) {
                result.append("  [0x").append(String.format("%08X", results.get(i))).append("] ");
                if ((i + 1) % 4 == 0) result.append("\n");
            }

            if (results.size() > 50) {
                result.append("\n... and ").append(results.size() - 50).append(" more");
            }

        } catch (Exception e) {
            return "✗ Search failed: " + e.getMessage();
        }

        return result.toString();
    }

    private List<Long> searchInteger(String value) throws Exception {
        List<Long> results = new ArrayList<>();
        int searchValue = Integer.parseInt(value);
        byte[] pattern = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(searchValue).array();

        RandomAccessFile raf = new RandomAccessFile(DUMP_FILE, "r");
        byte[] buffer = new byte[65536];
        long offset = 0;
        int read;

        while ((read = raf.read(buffer)) != -1) {
            for (int i = 0; i <= read - 4; i++) {
                if (matches(buffer, i, pattern)) {
                    results.add(offset + i);
                }
            }
            offset += read;
        }
        raf.close();

        return results;
    }

    private List<Long> searchFloat(String value) throws Exception {
        List<Long> results = new ArrayList<>();
        float searchValue = Float.parseFloat(value);
        byte[] pattern = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(searchValue).array();

        RandomAccessFile raf = new RandomAccessFile(DUMP_FILE, "r");
        byte[] buffer = new byte[65536];
        long offset = 0;
        int read;

        while ((read = raf.read(buffer)) != -1) {
            for (int i = 0; i <= read - 4; i++) {
                if (matches(buffer, i, pattern)) {
                    results.add(offset + i);
                }
            }
            offset += read;
        }
        raf.close();

        return results;
    }

    private List<Long> searchString(String value) throws Exception {
        List<Long> results = new ArrayList<>();
        byte[] pattern = value.getBytes("UTF-8");

        RandomAccessFile raf = new RandomAccessFile(DUMP_FILE, "r");
        byte[] buffer = new byte[65536];
        long offset = 0;
        int read;

        while ((read = raf.read(buffer)) != -1) {
            for (int i = 0; i <= read - pattern.length; i++) {
                if (matches(buffer, i, pattern)) {
                    results.add(offset + i);
                }
            }
            offset += read;
        }
        raf.close();

        return results;
    }

    private boolean matches(byte[] buffer, int offset, byte[] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            if (buffer[offset + i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }
}
