package com.termux.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ApkPatcher {
    public static void main(String[] args) {
        if (args.length > 0) {
            patchApk(args[0]);
        }
    }

    public static void patchApk(String apkFile) {
        try {
            File apk = new File(apkFile);
            if (!apk.exists()) return;
            byte[] apkBytes = new byte[(int) apk.length()];
            try (FileInputStream fis = new FileInputStream(apk)) { 
                fis.read(apkBytes); 
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(apkBytes));
            ZipOutputStream zos = new ZipOutputStream(baos);
            ZipEntry entry;
            byte[] searchBytes = "com.termux".getBytes("UTF-8");
            byte[] replaceBytes = com.termux.shared.termux.TermuxConstants.TERMUX_PACKAGE_NAME.getBytes("UTF-8");
            byte[] buf = new byte[8192];
            
            while ((entry = zis.getNextEntry()) != null) {
                ZipEntry newEntry = new ZipEntry(entry.getName());
                zos.putNextEntry(newEntry);
                if (entry.getName().endsWith(".dex")) {
                    ByteArrayOutputStream dexBaos = new ByteArrayOutputStream();
                    int len;
                    while ((len = zis.read(buf)) > 0) dexBaos.write(buf, 0, len);
                    byte[] dex = dexBaos.toByteArray();
                    
                    for (int i = 0; i <= dex.length - searchBytes.length; i++) {
                        boolean match = true;
                        for (int j = 0; j < searchBytes.length; j++) {
                            if (dex[i + j] != searchBytes[j]) { match = false; break; }
                        }
                        if (match) {
                            for (int j = 0; j < replaceBytes.length; j++) dex[i + j] = replaceBytes[j];
                            i += searchBytes.length - 1;
                        }
                    }
                    zos.write(dex);
                } else {
                    int len;
                    while ((len = zis.read(buf)) > 0) zos.write(buf, 0, len);
                }
                zos.closeEntry();
            }
            zos.close();
            try (FileOutputStream fos = new FileOutputStream(apk)) { 
                fos.write(baos.toByteArray()); 
            }
        } catch (Exception e) {
            android.util.Log.e("ApkPatcher", "Failed to patch APK: " + apkFile, e);
        }
    }
}
