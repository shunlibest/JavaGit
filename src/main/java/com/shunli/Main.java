package com.shunli;

import com.shunli.objects.CommitObject;
import com.shunli.objects.TreeObject;

import java.util.List;

public class Main {
    public static void main(String[] args) {


        CommitObject commitObject = CommitObject.parse("3c0f5dcb76c22188fb5c3e9dcd0565438cd777f6");
        System.out.println(commitObject.getFullMessage());

        commitObject.parse();

//        System.out.println(commitObject.getTreeId());
//        System.out.println(commitObject.getAuthor());
//        System.out.println(commitObject.getParentIds());
//

        TreeObject treeObject = TreeObject.parse(commitObject.getTreeId().toString());
        treeObject.parse();

        List<TreeObject> children = treeObject.getChildren();

        for (TreeObject child : children) {
            System.out.println(child.toString());
        }
    }
}