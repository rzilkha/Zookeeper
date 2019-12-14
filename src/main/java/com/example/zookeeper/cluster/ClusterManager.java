package com.example.zookeeper.cluster;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ClusterManager implements Runnable {
    String zkPath = "/cluster";
    ZooKeeper zk;


    public ClusterManager() throws InterruptedException, IOException, KeeperException {
        init();
    }

    void init() throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("localhost:2181",2000,null);

        if(zk.exists(zkPath,null)==null){
            zk.create(zkPath,"".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        }
    }

    void start() throws KeeperException, InterruptedException {
        Watcher liveWatcher = new Watcher() {

            @Override
            public void process(WatchedEvent event) {
                System.out.println("event "+event.toString());

                if(event.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        List<String> childs = zk.getChildren(zkPath, this);
                        System.out.println("cluster changed! setting is "+childs);
                    } catch (KeeperException | InterruptedException  e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        zk.getChildren(zkPath,liveWatcher);
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

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        ClusterManager clusterManager = new ClusterManager();
        clusterManager.start();
        clusterManager.run();
    }
}
