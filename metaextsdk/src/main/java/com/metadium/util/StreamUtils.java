package com.metadium.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * stream utils
 */
public class StreamUtils {
    /**
     * all read in input stream
     * @param is intput stream
     * @return read bytes
     * @throws IOException
     */
    public static byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = is.read(buf)) != -1) {
            byteBuffer.write(buf, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
