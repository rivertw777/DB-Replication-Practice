# 1. Docker Compose 시작
echo "Docker Compose를 시작합니다..."
docker-compose up -d

# 2. Master가 준비될 때까지 대기
echo "Master 서버가 준비될 때까지 대기 중..."
sleep 10

# 2. Master 상태 확인 및 로그 파일과 위치 추출
echo "Master 상태를 확인합니다..."
MASTER_STATUS=$(docker exec mysql-master mysql -uroot -p1234 -e "SHOW MASTER STATUS\G")
LOG_FILE=$(echo "$MASTER_STATUS" | grep "File:" | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep "Position:" | awk '{print $2}')

echo "$MASTER_STATUS"
echo "Master Log File: $LOG_FILE"
echo "Master Log Position: $LOG_POS"

# 4. Slave 복제 설정
echo "Slave1 복제를 설정합니다..."
docker exec mysql-slave1 mysql -uroot -p1234 -e "
STOP SLAVE;
RESET SLAVE ALL;
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='replicator',
  MASTER_PASSWORD='replicator_password',
  MASTER_LOG_FILE='$LOG_FILE',
  MASTER_LOG_POS=$LOG_POS;
START SLAVE;
"

echo "Slave2 복제를 설정합니다..."
docker exec mysql-slave2 mysql -uroot -p1234 -e "
STOP SLAVE;
RESET SLAVE ALL;
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='replicator',
  MASTER_PASSWORD='replicator_password',
  MASTER_LOG_FILE='$LOG_FILE',
  MASTER_LOG_POS=$LOG_POS;
START SLAVE;
"

# 5. 복제 상태 확인
sleep 5
echo "복제 상태를 확인합니다..."
echo "=== Slave1 상태 ==="
docker exec mysql-slave1 mysql -uroot -p1234 -e "SHOW SLAVE STATUS\G" | grep -E "(Slave_IO_Running|Slave_SQL_Running|Seconds_Behind_Master|Last_Error)"

echo "=== Slave2 상태 ==="
docker exec mysql-slave2 mysql -uroot -p1234 -e "SHOW SLAVE STATUS\G" | grep -E "(Slave_IO_Running|Slave_SQL_Running|Seconds_Behind_Master|Last_Error)"
