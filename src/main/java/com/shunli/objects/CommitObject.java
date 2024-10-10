package com.shunli.objects;


import com.shunli.Repository;
import com.shunli.utils.RawParseUtils;

import java.io.File;

/**
 * @author shunli
 * <p>
 * commit对象
 */
public class CommitObject extends BaseObject {

    // 存储commit对象的buffer
    protected byte[] buffer;

    private ObjectId treeId;
    private ObjectId parentIds;
    private String author;
    private String committer;
    private String fullMessage;

    protected CommitObject(ObjectId id) {
        super(id);
    }

    public static CommitObject parse(String commitId) {

        CommitObject commitObject = new CommitObject(ObjectId.fromString(commitId));

        parse(commitObject);
        return commitObject;
    }

    private static void parse(CommitObject object) {
        File directory = Repository.getInstance().getDirectory();
        object.buffer = ObjectFileReader.readObjectFile(directory.getAbsolutePath(), object.id);
    }

    /**
     * 解析提交消息, 这里为了简单起见, 直接将提交消息解析为字符串
     * <p>
     * 在git中会以类似于词法分析的方式, 一个byte一个byte的解析提交消息
     */
    public void parse() {
        byte[] raw = buffer;
        String content = new String(raw);
        String[] lines = content.split("\n");

        for (String line : lines) {
            if (line.startsWith("commit ")) {
                this.treeId = ObjectId.fromString(line.substring(16));
            } else if (line.startsWith("parent ")) {
                this.parentIds = ObjectId.fromString(line.substring(7));
            } else if (line.startsWith("author ")) {
                author = line.substring(7);
            } else if (line.startsWith("committer ")) {
                committer = line.substring(10);
            } else {
                fullMessage = line;
            }
        }
    }

    /**
     * 解析完整的提交消息并将其解码为字符串。
     *
     * @return 将提交消息解码为字符串。永不为空。
     */
    public final String getFullMessage() {
        byte[] raw = buffer;
        int msgB = RawParseUtils.commitMessage(raw, 0);
        if (msgB < 0) {
            return ""; //$NON-NLS-1$
        }
        return RawParseUtils.decode(raw, msgB, raw.length);
    }

    public ObjectId getTreeId() {
        return treeId;
    }

    public ObjectId getParentIds() {
        return parentIds;
    }

    public String getAuthor() {
        return author;
    }

    public String getCommitter() {
        return committer;
    }
}
