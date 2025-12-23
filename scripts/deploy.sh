#!/bin/bash

# 사용법: ./deploy.sh [dev|prod]
TARGET_ENV=$1
APP_NAME="tpa-admin-api"
BASE_PATH="/home/nex3/app/${APP_NAME}"

if [ -z "$TARGET_ENV" ]; then
  echo "⚠️ 사용법: ./deploy.sh [dev|prod]"
  exit 1
fi

# 환경별 설정 (Nginx 및 기본 포트)
if [ "$TARGET_ENV" == "prod" ]; then
  ENV_FILE=".env.prod"
  NGINX_CONF="/etc/nginx/conf.d/${APP_NAME}-prod.conf"
  DEFAULT_PORT="8091"
else
  ENV_FILE=".env.dev"
  NGINX_CONF="/etc/nginx/conf.d/${APP_NAME}-dev.conf"
  DEFAULT_PORT="8083" # 다른 웹서버와 겹치지 않게 8083부터 시작 권장
fi

echo "🚀 ${TARGET_ENV} 배포 시작 (경로: ${BASE_PATH})..."

# 1. 서버에 미리 둔 .env 파일을 .env로 복사 (컨테이너 주입용)
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ 환경 파일(${ENV_FILE})이 존재하지 않습니다. 서버에 직접 만들어주세요."
  exit 1
fi

# 2. 실행 중인 포트 체크 및 타겟 포트 결정 (Blue-Green)
CURRENT_PORT_FILE="${BASE_PATH}/current_port_${TARGET_ENV}.txt"
if [ -f "$CURRENT_PORT_FILE" ]; then
    CURRENT_PORT=$(cat "$CURRENT_PORT_FILE")
else
    CURRENT_PORT="$DEFAULT_PORT"
fi

# 포트 스위칭 로직
if [ "$TARGET_ENV" == "dev" ]; then
  [ "$CURRENT_PORT" == "8083" ] && TARGET_PORT="8084" || TARGET_PORT="8083"
else
  [ "$CURRENT_PORT" == "8091" ] && TARGET_PORT="8092" || TARGET_PORT="8091"
fi

echo "🔄 전환 계획: ${CURRENT_PORT} -> ${TARGET_PORT}"

# 3. 새 컨테이너 가동
export HOST_PORT=$TARGET_PORT
export TARGET_ENV=$TARGET_ENV
export COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_PORT}"

docker compose -f docker-compose.yml -p $COMPOSE_PROJECT_NAME up -d --build

# 4. Health Check
echo "🏥 서비스 상태 확인 중... (Port: ${TARGET_PORT})"
for i in {1..10}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)
  if [ "$STATUS" == "200" ]; then
    echo "✅ 성공!"
    break
  fi
  echo "⏳ 대기... ($i/10)"
  sleep 5
  if [ $i -eq 10 ]; then
    echo "❌ 실패. 컨테이너 롤백."
    docker stop $COMPOSE_PROJECT_NAME && docker rm $COMPOSE_PROJECT_NAME
    exit 1
  fi
done

# 5. Nginx 설정 업데이트 및 리로드
echo "🔄 Nginx 트래픽 전환 중..."
sudo sed -i "s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/g" $NGINX_CONF
sudo nginx -s reload

# 6. 구 버전 컨테이너 정지
if [ "$CURRENT_PORT" != "$TARGET_PORT" ]; then
  OLD_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${CURRENT_PORT}"
  echo "🛑 구 버전 컨테이너 정지: ${OLD_PROJECT_NAME}"
  docker stop $OLD_PROJECT_NAME && docker rm $OLD_PROJECT_NAME
fi

echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"
echo "🎉 배포 완료!"