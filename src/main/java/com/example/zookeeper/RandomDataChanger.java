package com.example.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.UUID;

public class RandomDataChanger implements Watcher,Runnable {

    ZooKeeper zoo;
    private String ZK_PARENT_NODE = "/zkdata";
    public RandomDataChanger() throws IOException {
        init();
    }

    private void init() throws IOException {
         zoo = new ZooKeeper("localhost:2181",200,null);

    }
    @Override
    public void process(WatchedEvent event) {
        System.out.println("event "+event.toString());
    }

    @Override
    public void run() {
        while(true){
            String uuid = UUID.randomUUID().toString();
            try {
                // here we changed the data of the znode every 5 seconds
                zoo.setData(ZK_PARENT_NODE, uuid.getBytes(), -1);
            } catch (KeeperException | InterruptedException  e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        RandomDataChanger randomDataChanger = new RandomDataChanger();
        randomDataChanger.run();
    }
}
