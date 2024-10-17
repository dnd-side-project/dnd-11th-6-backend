#!/bin/bash

IP=127.0.0.1
IS_DEV1=$(docker ps | grep dev1)

if [ -z "$IS_DEV1" ];then
	BLUE_PORT="8081"
	GREEN_PORT="8080"
	CONTAINER_NAME="dev1"
	STOP_CONTAINER_NAME="dev2"
else
	BLUE_PORT="8080"
        GREEN_PORT="8081"
	CONTAINER_NAME="dev2"
	STOP_CONTAINER_NAME="dev1"
fi

if [ $GREEN_PORT == "none" ]
then
	echo "블루와 그린을 임으로 지정합니다.\n"
	BLUE_PORT="8080"
	GREEN_PORT="8081"
fi

echo -e "그린($GREEN_PORT)과 블루($BLUE_PORT) 서버 확인 ${CONTAINER_NAME} 컨테이너를 실행합니다."

docker compose pull $CONTAINER_NAME
docker compose up -d $CONTAINER_NAME

for retry in {1..10}
do
	RESPONSE=$(curl -s http://$IP:$GREEN_PORT/actuator/health)
	GREEN_HEALTH=$(echo ${RESPONSE} | grep 'UP' | wc -l)
	if [ $GREEN_HEALTH -eq 1 ]
	then
		break
	else
		echo -e "$IP:$GREEN_PORT 가 켜져있지 않습니다. 10초 슬립하고 다시 헬스체크를 수행합니다."
		sleep 10
	fi
done

if [ $GREEN_HEALTH -eq 0 ]
then
	echo -e "$IP:$GREEN_PORT 가 작동하지 않습니다."
	exit 0
else
	echo -e "$IP:$GREEN_PORT 가 정상적으로 실행 중입니다."
fi

echo "set \$service_url $CONTAINER_NAME;" | sudo tee ./conf/service-url.inc
sudo docker exec nginx nginx -s reload

echo "$STOP_CONTAINER_NAME 컨테이너 종료"
docker compose stop $STOP_CONTAINER_NAME
docker compose rm -f $STOP_CONTAINER_NAME