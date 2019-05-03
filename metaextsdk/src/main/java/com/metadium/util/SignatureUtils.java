package com.metadium.util;

import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * EC signature utility
 */
public class SignatureUtils {
    /**
     * EC signature to hex string
     * @param signatureData ec signature
     * @return hex string
     */
    public static String signatureDataToString(Sign.SignatureData signatureData) {
        ByteBuffer buffer = ByteBuffer.allocate(65);
        buffer.put(signatureData.getR());
        buffer.put(signatureData.getS());
        buffer.put(signatureData.getV());
        return Numeric.toHexString(buffer.array());
    }

    /**
     * hex string to ec signature
     * @param signature hex string
     * @return ec signature
     */
    public static Sign.SignatureData stringToSignatureData(String signature) {
        byte[] bytes = Numeric.hexStringToByteArray(signature);
        return new Sign.SignatureData(bytes[64], Arrays.copyOfRange(bytes, 0, 32), Arrays.copyOfRange(bytes, 32, 64));
    }
}
