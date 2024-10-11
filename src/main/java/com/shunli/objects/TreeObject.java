package com.shunli.objects;


import com.shunli.Repository;
import com.shunli.utils.FileUtils;
import com.shunli.utils.RawParseUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shunli
 * <p>
 * 树对象
 */
public class TreeObject extends BaseObject {

    protected byte[] buffer;

    private final String fileMode;
    private final String fileName;
    private final String filePath;

    private final ObjectId objectId;
    // tree对象, 引用的可能是子文件夹(tree对象)或者文件(blob对象)
    private final List<BaseObject> children = new ArrayList<>();

    protected TreeObject(ObjectId objectId, String fileMode, String fileName) {
        super(objectId);
        this.fileMode = fileMode;
        this.fileName = fileName;
        this.objectId = objectId;
        this.filePath = fileName;
    }

    public static TreeObject getRootTreeObject(String commitId) {
        // 对于commit对象引用的第一个tree对象, 这个引用不包含名称（name）和模式（mode）,代表了工作区的根目录
        return new TreeObject(ObjectId.fromString(commitId), "", "/");
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
        if (writeToFile) {
            FileUtils.createFolder(writeFilePath);
        }
        readObjectFile();
        byte[] data = buffer;

        if (data == null) {
            return;
        }

        int index = 0;

        // 跳过tree对象开头的: tree+size 部分
        index = RawParseUtils.findNextZeroByte(data, index);

        while (index < data.length - 1) {
            // 解析文件模式
            int modeStart = index + 1;
            int modeEnd = RawParseUtils.findNextSpaceByte(data, modeStart);
            String mode = new String(data, modeStart, modeEnd - modeStart);

            // 解析文件名
            int nameStart = modeEnd + 1;
            int nameEnd = RawParseUtils.findNextZeroByte(data, nameStart);
            String name = new String(data, nameStart, nameEnd - nameStart);

            // 解析 SHA-1 值
            byte[] sha1Bytes = new byte[20];
            System.arraycopy(data, nameEnd + 1, sha1Bytes, 0, 20);
            ObjectId sha1Id = ObjectId.fromString(sha1Bytes);
            index = nameEnd + 20;  // Skip SHA-1 bytes

            BaseObject childObject = generateObject(sha1Id, mode, name);
            childObject.parse(writeToFile, new File(writeFilePath, name).getAbsolutePath());
            children.add(childObject);
        }
    }

    private BaseObject generateObject(ObjectId objectId, String fileMode, String fileName) {

        if (fileMode.equals("40000")) {
            return new TreeObject(objectId, fileMode, fileName);
        } else {
            return new BlobObject(objectId, fileMode, fileName);
        }
    }

    public List<BaseObject> getChildren() {
        return children;
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
