package com.shunli;

import com.shunli.objects.BaseObject;
import com.shunli.objects.CommitObject;
import com.shunli.objects.TreeObject;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        CommitObject commitObject = CommitObject.parse("2b57337276483bb2007935cfc4ddb570f4ae0f2a");
        System.out.println(commitObject.getFullMessage());

        String  directory = Repository.getInstance().getWorkSpaceDir().getAbsolutePath();

        commitObject.parse(false, "");

//        System.out.println(commitObject.getTreeId());
//        System.out.println(commitObject.getAuthor());
//        System.out.println(commitObject.getParentIds());
//

        TreeObject treeObject = TreeObject.getRootTreeObject(commitObject.getTreeId().toString());
        treeObject.parse(true, directory);

        List<BaseObject> children = treeObject.getChildren();

        printTree(treeObject.getChildren(), "*");
    }

    private static void printTree(List<BaseObject> entries, String indent) {
        for (BaseObject entry : entries) {
            System.out.println(indent + " " + entry.getName());
            if (entry instanceof TreeObject) {
                printTree(((TreeObject) entry).getChildren(), indent + "*");
            }
        }
    }
}