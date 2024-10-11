package com.shunli.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     */
    public static void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("创建文件夹失败");
            }
        }
    }

    // 创建文件
    public static void createFile(String path) {
        File file = new File(path);
        file.delete();
        try {
            boolean createNewFile = file.createNewFile();
            if (!createNewFile) {
                throw new RuntimeException("创建文件失败, path: " + path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String filePath, byte[] data, int offset) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(data, offset, data.length - offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
