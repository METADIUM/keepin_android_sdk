package com.metadium.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Secured file
 */
public class SecureFileUtils {
    private static final String KEYSTORE_SECURE_FILE = "secure_file";

    /**
     * file save
     * @param context android context
     * @param file    file to save
     * @param bin     data to save
     * @return saved
     */
    public static boolean save(Context context, File file, byte[] bin) {
        FileOutputStream outputStream = null;
        try {
            KeyStoreUtils.createKeyStoreEntryRsaEcbPkcs1Padding(context, KEYSTORE_SECURE_FILE);


            byte[] encryptedBin = KeyStoreUtils.encryptAesWithRsa(KEYSTORE_SECURE_FILE, bin);

            outputStream = new FileOutputStream(file);
            outputStream.write(encryptedBin);
        }
        catch (Exception e) {
            return false;
        }
        finally {
            if (outputStream != null) try { outputStream.close(); } catch (Exception e) {}
        }
        return true;
    }

    /**
     * read file
     * @param context android context
     * @param file    file to read
     * @return file data
     */
    public static byte[] load(Context context, File file) {
        try {
            return KeyStoreUtils.decryptAesWithRSA(KEYSTORE_SECURE_FILE, StreamUtils.readBytes(new FileInputStream(file)));
        }
        catch (Exception e) {
            return null;
        }
    }


}
