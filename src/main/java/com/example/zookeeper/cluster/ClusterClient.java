package com.example.zookeeper.cluster;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.zookeeper.CreateMode.*;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ClusterClient implements Watcher,Runnable {

    String zkPath = "/cluster";
    ZooKeeper zk;

    public ClusterClient() throws InterruptedException, IOException, KeeperException {
        init();
    }

    void init() throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("localhost:2181",2000,null);


        // we build ephemeral node and sequential, which will be deleted
        // when session ends, then the cluster manager will get notified about it
        zk.create(zkPath+"/inst","inst".getBytes(),OPEN_ACL_UNSAFE,EPHEMERAL_SEQUENTIAL);


    }

    @Override
    public void run() {

        synchronized (this){
            while(true){
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Node created "+event.toString());
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        ClusterClient clusterClient = new ClusterClient();
        clusterClient.run();
    }
}
