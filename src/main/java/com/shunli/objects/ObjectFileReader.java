package com.shunli.objects;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ObjectFileReader {

    /**
     * 读取对象文件, 规则
     * 其分为两部分：前两位字符 和 其余部分。
     * 它使用前两位作为目录名，其余部分作为文件名（这是因为大多数文件系统不喜欢在单个目录中有太多文件，这会导致性能下降。
     * Git 的方法创建了 256 个可能的中间目录，从而将每个目录的平均文件数减少到 256 分之一）
     *
     * @param GIT_DIR    git仓库目录根目录
     * @param objectHash 对象hash
     * @return 读取到的二进制数据
     */
    public static byte[] readObjectFile(String GIT_DIR, ObjectId objectHash) {
        // 计算对象存储路径
        String subDir = objectHash.toString().substring(0, 2);
        String fileName = objectHash.toString().substring(2);

        // 计算对象存储路径
        Path path = Paths.get(GIT_DIR, "objects", subDir, fileName);

        // 读取压缩的对象文件
        try {
            FileInputStream fileInputStream = new FileInputStream(path.toFile());
            InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream, new Inflater());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] tempBuffer = new byte[1024];
            int len;
            while ((len = inflaterInputStream.read(tempBuffer)) != -1) {
                buffer.write(tempBuffer, 0, len);
            }
            // 关闭流, 释放资源(为方便阅读, 因此简写)
            inflaterInputStream.close();
            fileInputStream.close();
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(byte[] data, String filePath) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(filePath));
            fileOutputStream.write(data);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // 设置Git仓库路径和对象哈希
            String GIT_DIR = "/Users/shunlihan/Documents/codeAli/vins-mobile/.git";
            String commitHash = "3c0f5dcb76c22188fb5c3e9dcd0565438cd777f6"; // 例如 "a1b2c3d4e5..."
            String outputFilePath = "/Users/shunlihan/Downloads/aaaa.txt";

            // 读取并解压对象数据
            byte[] data = readObjectFile(GIT_DIR, ObjectId.fromString(commitHash));

            // 将解压后的数据写入指定文件
            writeToFile(data, outputFilePath);
            System.out.println("Data written to " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
