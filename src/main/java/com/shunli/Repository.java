package com.shunli;

import java.io.File;

/**
 * @author shunli
 * <p>
 * 负责管理git仓库根路径
 */
public class Repository {
    private final File gitDir;
    // 单例
    private static Repository instance;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository("/Users/shunlihan/Documents/codeAli/GXDTaoJin_Android/.git");
        }
        return instance;
    }


    public Repository(String gitDir) {
        this.gitDir = new File(gitDir);
    }

    public File getDirectory() {
        return gitDir;
    }

    public File getWorkSpaceDir() {
        return new File("/Users/shunlihan/Downloads/workspace");
    }

}
