package com.shunli.objects;

/**
 * @author shunli
 * <p>
 * 四种object类型对象, 都会有一个唯一id的标识
 * ID各种均相同, 都是SHA-1 长度160位, 由40个十六进制字符‌表示
 * eg: 0155f8bf6e569b487251767be985332a438ccc39
 * <p>
 * 目前git的最新版本已经使用了sha256, 由64 个十六进制字符
 */
public class ObjectId {
    private final String id;

    private ObjectId(String id) {
        this.id = id;
    }

    /**
     * 字符串sha1 转 ObjectId对象
     * <p>
     * 在git源码中, 使用char[] 表示, 会占用20字节, 如果使用String表示, 会占用80(40*2)字节, 这里简单起见, 直接使用String表示
     *
     * @param str 字符串
     * @return ObjectId
     */
    public static ObjectId fromString(String str) {
        if (str.length() != 40) {
            throw new RuntimeException("objectId 长度不合法: " + str);
        }
        return new ObjectId(str);
    }

    public static ObjectId fromString(byte[] sha1) {
        // 把sha1转为字符串
        String str = new String(sha1);
        return fromString(str);
    }

    @Override
    public String toString() {
        return id;
    }
}
