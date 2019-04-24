package com.nigel.wenreader.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author nigel
 * @description
 * @since 2018-09-19
 */
public class IOUtils {
    public static void close(Closeable closeable){
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
            //close error
        }
    }
}
