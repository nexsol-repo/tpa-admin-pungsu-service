#!/bin/bash

# 사용법: ./deploy.sh [dev|prod]
TARGET_ENV=$1
APP_NAME="tpa-admin-api"
BASE_PATH="/home/nex3/app/${APP_NAME}"

if [ -z "$TARGET_ENV" ]; then
  echo "⚠️ 사용법: ./deploy.sh [dev|prod]"
  exit 1
fi

# 환경별 Nginx 설정 파일 및 기본 포트 지정
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

# 1. 환경 파일 복사 (서버에 미리 작성해둔 .env.dev/prod 활용)
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ 서버의 ${BASE_PATH} 경로에 ${ENV_FILE} 파일이 없습니다."
  exit 1
fi

# 2. Blue-Green 포트 스위칭
CURRENT_PORT_FILE="${BASE_PATH}/current_port_${TARGET_ENV}.txt"
if [ -f "$CURRENT_PORT_FILE" ]; then
    CURRENT_PORT=$(cat "$CURRENT_PORT_FILE")
else
    CURRENT_PORT="$DEFAULT_PORT"
fi

if [ "$CURRENT_PORT" == "$DEFAULT_PORT" ]; then
    TARGET_PORT=$((DEFAULT_PORT + 1))
else
    TARGET_PORT="$DEFAULT_PORT"
fi

echo "🔄 포트 전환: ${CURRENT_PORT} -> ${TARGET_PORT}"

# 3. 컨테이너 실행
export HOST_PORT=$TARGET_PORT
export TARGET_ENV=$TARGET_ENV
export DOCKER_IMAGE="${APP_NAME}:${TARGET_ENV}"
export COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_PORT}"

# docker-compose.yml 사용
docker compose -f docker-compose.yml -p $COMPOSE_PROJECT_NAME up -d

# 4. Health Check
echo "🏥 Health Check 중..."
for i in {1..10}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)
  if [ "$STATUS" == "200" ]; then
    echo "✅ 성공!"
    break
  fi
  echo "⏳ 대기... ($i/10)"
  sleep 5
  if [ $i -eq 10 ]; then
    echo "❌ 실패: 새 컨테이너가 정상적으로 작동하지 않습니다."
    docker stop $COMPOSE_PROJECT_NAME && docker rm $COMPOSE_PROJECT_NAME
    exit 1
  fi
done

# 5. Nginx 트래픽 전환
echo "🔄 Nginx 트래픽 전환..."
sudo sed -i "s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/g" $NGINX_CONF
sudo nginx -s reload

# 6. 이전 컨테이너 제거
if [ "$CURRENT_PORT" != "$TARGET_PORT" ]; then
    OLD_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${CURRENT_PORT}"
    echo "🛑 이전 컨테이너 제거: ${OLD_PROJECT_NAME}"
    docker stop $OLD_PROJECT_NAME && docker rm $OLD_PROJECT_NAME
fi

echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"
echo "🎉 배포 완료!"