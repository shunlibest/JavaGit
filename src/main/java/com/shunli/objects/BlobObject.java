package com.shunli.objects;


import com.shunli.Repository;
import com.shunli.utils.FileUtils;
import com.shunli.utils.RawParseUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author shunli
 * <p>
 * blob对象
 */
public class BlobObject extends BaseObject {

    protected byte[] buffer;

    private final String fileMode;
    private final String fileName;
    private final ObjectId objectId;


    protected BlobObject(ObjectId objectId, String fileMode, String fileName) {
        super(objectId);
        this.fileMode = fileMode;
        this.fileName = fileName;
        this.objectId = objectId;
    }

    private void readObjectFile() {
        File directory = Repository.getInstance().getDirectory();
        buffer = ObjectFileReader.readObjectFile(directory.getAbsolutePath(), id);
    }

    /**
     * 解析tree对象
     */
    @Override
    public void parse(boolean writeToFile, String writeFilePath) {
        if (!writeToFile) {
            // 如果不需要写入文件, 那么就不用查看文件内容
            return;
        }
        readObjectFile();
        byte[] data = buffer;
        if (data == null) {
            return;
        }
        int index = 0;
        // 跳过blob对象开头的: blob + size 部分
        index = RawParseUtils.findNextZeroByte(data, index);
        index++;
        FileUtils.createFile(writeFilePath);
        FileUtils.writeToFile(writeFilePath, data, index);
    }


    @Override
    public String toString() {
        return fileMode + " " + fileName + " " + objectId;
    }

    @Override
    public String getName() {
        return fileName;
    }
}
