package com.practice.replication.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private CircularList<String> slaveDataSourceNameList;

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        slaveDataSourceNameList = new CircularList<>(
                targetDataSources.keySet()
                        .stream()
                        .map(Object::toString)
                        .filter(string -> string.contains("slave"))
                        .collect(Collectors.toList())
        );
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey;

        // 읽기 전용 트랜잭션인 경우 슬레이브 DB로 라우팅
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            dataSourceKey = slaveDataSourceNameList.getOne();
            log.info("[READ] Routing to: {}", dataSourceKey);
        } else {
            dataSourceKey = "master";
            log.info("[WRITE] Routing to: {}", dataSourceKey);
        }

        return dataSourceKey;
    }
}