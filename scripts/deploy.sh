#!/bin/bash

# 사용법: ./deploy.sh [dev|prod]
TARGET_ENV=$1
APP_NAME="tpa-admin-api"
BASE_PATH="/home/nex3/app/${APP_NAME}"

if [ -z "$TARGET_ENV" ]; then
  echo "⚠️ 사용법: ./deploy.sh [dev|prod]"
  exit 1
fi

if [ "$TARGET_ENV" == "prod" ]; then
  ENV_FILE=".env.prod"
  NGINX_CONF="/etc/nginx/conf.d/${APP_NAME}-prod.conf"
  DEFAULT_PORT="8091"
else
  ENV_FILE=".env.dev"
  NGINX_CONF="/etc/nginx/conf.d/${APP_NAME}-dev.conf"
  DEFAULT_PORT="8083"
fi

echo "🚀 ${TARGET_ENV} 배포 시작..."

# 1. 환경 파일 복사
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ 서버의 ${BASE_PATH} 경로에 ${ENV_FILE} 파일이 없습니다."
  exit 1
fi

# 2. 포트 결정
CURRENT_PORT_FILE="${BASE_PATH}/current_port_${TARGET_ENV}.txt"
if [ -f "$CURRENT_PORT_FILE" ]; then
    CURRENT_PORT=$(cat "$CURRENT_PORT_FILE")
else
    CURRENT_PORT="$DEFAULT_PORT"
fi

[ "$CURRENT_PORT" == "8083" ] && TARGET_PORT="8084" || TARGET_PORT="8083"
echo "🔄 포트 전환: ${CURRENT_PORT} -> ${TARGET_PORT}"

# 3. 컨테이너 실행
export HOST_PORT=$TARGET_PORT
export TARGET_ENV=$TARGET_ENV
export DOCKER_IMAGE="${APP_NAME}:${TARGET_ENV}"
export COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_PORT}"

docker compose -f docker-compose.yml -p $COMPOSE_PROJECT_NAME up -d

# 4. Health Check (로그 확인 추가)
echo "🏥 Health Check 중... (http://127.0.0.1:${TARGET_PORT}/health)"
for i in {1..12}; do # 60초까지 대기 (JVM 구동 시간 고려)
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)
  if [ "$STATUS" == "200" ]; then
    echo "✅ 성공!"
    break
  fi
  echo "⏳ 대기... ($i/12) - 상태코드: $STATUS"
  sleep 5

  if [ $i -eq 12 ]; then
    echo "❌ 실패: 컨테이너 에러 로그 확인:"
    docker logs $COMPOSE_PROJECT_NAME --tail 50
    echo "❌ 배포를 중단하고 롤백합니다."
    docker stop $COMPOSE_PROJECT_NAME && docker rm $COMPOSE_PROJECT_NAME
    exit 1
  fi
done

# 5. Nginx 트래픽 전환
echo "🔄 Nginx 트래픽 전환 중..."
# 설정 파일의 127.0.0.1:808x 부분을 찾아 현재 포트로 변경
sudo sed -i "s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/g" $NGINX_CONF
sudo nginx -t && sudo nginx -s reload

# 6. 이전 컨테이너 제거
OLD_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${CURRENT_PORT}"
if [ "$(docker ps -a -q -f name=$OLD_PROJECT_NAME)" ]; then
    echo "🛑 이전 컨테이너 제거: ${OLD_PROJECT_NAME}"
    docker stop $OLD_PROJECT_NAME && docker rm $OLD_PROJECT_NAME
fi

echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"
echo "🎉 배포 완료!"