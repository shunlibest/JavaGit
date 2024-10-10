package com.shunli.objects;


import com.shunli.Repository;
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

    private final ObjectId objectId;
    private final List<TreeObject> children = new ArrayList<>();

    protected TreeObject(ObjectId objectId, String fileMode, String fileName) {
        super(objectId);
        this.fileMode = fileMode;
        this.fileName = fileName;
        this.objectId = objectId;
    }

    public static TreeObject parse(String commitId) {
        // 对于commit对象引用的第一个tree对象, 这个引用不包含名称（name）和模式（mode）,代表了工作区的根目录
        TreeObject commitObject = new TreeObject(ObjectId.fromString(commitId), "", "/");
        parse(commitObject);
        return commitObject;
    }

    private static void parse(TreeObject object) {
        File directory = Repository.getInstance().getDirectory();
        object.buffer = ObjectFileReader.readObjectFile(directory.getAbsolutePath(), object.id);
    }

    /**
     * 解析tree对象
     */
    public void parse() {
        byte[] data = buffer;
        int index = 0;

        // 跳过tree对象开头的: tree+size 部分
        index = findNextZeroByte(data, index);

        while (index < data.length - 1) {
            // 解析文件模式
            int modeStart = index + 1;
            int modeEnd = findNextSpaceByte(data, modeStart);
            String mode = new String(data, modeStart, modeEnd - modeStart);

            // 解析文件名
            int nameStart = modeEnd + 1;
            int nameEnd = findNextZeroByte(data, nameStart);
            String name = new String(data, nameStart, nameEnd - nameStart);

            // 解析 SHA-1 值
            byte[] sha1Bytes = new byte[20];
            System.arraycopy(data, nameEnd + 1, sha1Bytes, 0, 20);
            ObjectId sha1Id = ObjectId.fromString(sha1Bytes);
            index = nameEnd + 20;  // Skip SHA-1 bytes

            TreeObject childObject = new TreeObject(sha1Id, mode, name);
            children.add(childObject);
        }
    }


    private static int findNextSpaceByte(byte[] data, int startIndex) {
        return findNextByte(data, startIndex, (byte) ' ');
    }

    private static int findNextZeroByte(byte[] data, int startIndex) {
        return findNextByte(data, startIndex, (byte) 0);
    }

    private static int findNextByte(byte[] data, int startIndex, byte b) {
        for (int i = startIndex; i < data.length; i++) {
            if (data[i] == b) {
                return i;
            }
        }
        return -1;
    }

    public List<TreeObject> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return fileMode + " " + fileName + " " + objectId;
    }
}
