package com.practice.replication.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private static final List<String> SLAVE_KEYS = List.of("slave1", "slave2");

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey;

        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            // 읽기 전용 트랜잭션인 경우 슬레이브 DB로 라우팅 (랜덤 선택)
            dataSourceKey = SLAVE_KEYS.get(ThreadLocalRandom.current().nextInt(SLAVE_KEYS.size()));
            log.info("[READ] Routing to: {}", dataSourceKey);
        } else {
            // 쓰기 트랜잭션인 경우 마스터 DB로 라우팅
            dataSourceKey = "master";
            log.info("[WRITE] Routing to: {}", dataSourceKey);
        }

        return dataSourceKey;
    }
}