package com.github.cloudgyb.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZK CRUD 基本操作
 */
public class ZkCRUD {
    private final static Logger logger = LoggerFactory.getLogger(ZkCRUD.class);

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString("localhost:2181")
                .connectionTimeoutMs(1000)
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();
        client.start();
        if (client.checkExists().forPath("/test") == null) {
            client.create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath("/test");
        }
        String idPrefix = "/test/ID-";
        int n = 100;
        Thread thread = new Thread(() -> {
            int m = 100;
            while (m-- > 0) {
                String s = null;
                try {
                    s = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(idPrefix);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                logger.info(s);
                logger.info(s.substring(idPrefix.length()));
            }
        });
        thread.start();
        while (n-- > 0) {
            String s = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(idPrefix);
            logger.info(s);
            logger.info(s.substring(idPrefix.length()));
        }
        thread.join();
        client.close();
    }
}
