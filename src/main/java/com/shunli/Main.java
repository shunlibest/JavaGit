package com.shunli;

import com.shunli.objects.CommitObject;

public class Main {
    public static void main(String[] args) {


        CommitObject commitObject = CommitObject.parse("3c0f5dcb76c22188fb5c3e9dcd0565438cd777f6");
        System.out.println(commitObject.getFullMessage());

        commitObject.parse();

        System.out.println(commitObject.getTreeId());
        System.out.println(commitObject.getAuthor());
    }
}