package com.example.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

public class First {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {


        ZooKeeper zoo = new ZooKeeper("localhost:2181",2000,null);

        List<String> childs = zoo.getChildren("/", null);

        System.out.println("childs "+childs);
    }
}
