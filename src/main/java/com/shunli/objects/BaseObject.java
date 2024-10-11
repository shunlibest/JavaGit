package com.shunli.objects;


/**
 * @author shunli
 * 基础Object对象, 有4种类型: blob、commit、tag 和 tree
 */
public abstract class BaseObject {

    protected ObjectId id;


    public BaseObject(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return "";
    }

    public abstract void parse(boolean writeToFile, String writeFilePath);
}
