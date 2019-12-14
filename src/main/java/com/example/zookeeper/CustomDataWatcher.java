package com.example.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class CustomDataWatcher implements Watcher, Runnable {

    private String ZK_PARENT_NODE = "/zkdata";

    private  ZooKeeper zooKeeper;
    public CustomDataWatcher() throws InterruptedException, IOException, KeeperException {
        init();
    }

    // first create the znode if doesn't exist
    private void init() throws IOException, KeeperException, InterruptedException {
        zooKeeper = new ZooKeeper("localhost:2181",200, null);
        Stat isExists = zooKeeper.exists(ZK_PARENT_NODE, null);
        if(isExists==null) {
            zooKeeper.create(ZK_PARENT_NODE, "new".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        }
    }
    @Override
    public void process(WatchedEvent event) {

        System.out.println("event received "+event.toString());

        if(event.getType() == Event.EventType.NodeDataChanged ){
            printData();
        }

    }

    private void printData() {
        try {
            // notification is one time, so we need to set it up again by sending
            // the watcher on the same node , this is also the reason why we start with this op
            // at the beginning of our code
            byte[] data = zooKeeper.getData(ZK_PARENT_NODE, this, null);

            System.out.println("Data Changed to : "+new String(data));
        } catch (KeeperException | InterruptedException  e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
         synchronized (this){
             try {
                 while(true) {
                     wait();
                 }
             } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
         }
    }

    public static void main(String[] args) {
        CustomDataWatcher customDataWatcher = null;
        try {
            customDataWatcher = new CustomDataWatcher();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException | KeeperException e) {
            e.printStackTrace();
        }

        // also for setting the watcher
        customDataWatcher.printData();
        customDataWatcher.run();
    }
}
