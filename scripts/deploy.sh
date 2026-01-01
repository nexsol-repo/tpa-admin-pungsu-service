#!/bin/bash

# 사용법: ./deploy.sh [dev|prod]
TARGET_ENV=$1
APP_NAME="tpa-admin-pungsu-api"
# Nginx 설정 내에서 바꿀 location 경로
ROUTE_PATH="/upstream/pungsu/"
BASE_PATH="/home/nex3/app/${APP_NAME}"

if [ "$TARGET_ENV" != "prod" ]; then
  echo "⚠️ 현재 설정은 main 브랜치(prod) 배포만 지원합니다."
  exit 1
fi

# Prod 환경 설정
ENV_FILE=".env.prod"
NGINX_CONF="/etc/nginx/conf.d/tpa-admin-api.conf"
DEFAULT_PORT="8091"

echo "🚀 ${APP_NAME} (${TARGET_ENV}) 배포 시작..."

# 1. 환경 파일 준비 (.env.dev -> .env)
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ .env.prod 파일이 없습니다. 서버에 파일을 생성해주세요."
  exit 1
fi

# 2. Blue-Green 포트 결정
CURRENT_PORT_FILE="${BASE_PATH}/current_port.txt"
if [ -f "$CURRENT_PORT_FILE" ]; then
    CURRENT_PORT=$(cat "$CURRENT_PORT_FILE")
else
    CURRENT_PORT="$DEFAULT_PORT"
fi


if [ "$CURRENT_PORT" == "8091" ]; then
    TARGET_PORT="8092"
else
    TARGET_PORT="8091"
fi
echo "🔄 포트 스위칭 계획: ${CURRENT_PORT} -> ${TARGET_PORT}"

# 3. 신규 컨테이너 실행
export HOST_PORT=$TARGET_PORT
export TARGET_ENV="prod"
export DOCKER_IMAGE="${APP_NAME}:${TARGET_ENV}"
export COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_PORT}"

echo "📦 컨테이너 기동: ${COMPOSE_PROJECT_NAME} (Port: ${TARGET_PORT})"
docker compose -f docker-compose.yml -p $COMPOSE_PROJECT_NAME up -d

# 4. Health Check (최대 100초 대기)
echo "🏥 서비스 헬스체크 중... (http://127.0.0.1:${TARGET_PORT}/health)"
for i in {1..20}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)
  if [ "$STATUS" == "200" ]; then
    echo "✅ 헬스체크 성공!"
    break
  fi
  echo "⏳ 대기 중... ($i/20) - HTTP 응답코드: $STATUS"
  sleep 5

  if [ $i -eq 20 ]; then
    echo "❌ 배포 실패! 앱 로그(마지막 100줄)를 출력합니다:"
    docker logs $COMPOSE_PROJECT_NAME --tail 100
    echo "🛑 신규 컨테이너를 중지하고 제거합니다."
    docker stop $COMPOSE_PROJECT_NAME && docker rm $COMPOSE_PROJECT_NAME
    exit 1
  fi
done

# 5. Nginx 트래픽 전환 (Surgical Update)
echo "🔄 Nginx 트래픽 전환 중..."
# 특정 location 블록 내의 proxy_pass 포트만 변경
sudo sed -i "/location ${ROUTE_PATH//\//\\/}/,/}/ s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/" $NGINX_CONF
sudo nginx -t && sudo nginx -s reload

# 6. 구 버전 컨테이너 제거
OLD_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${CURRENT_PORT}"
echo "🛑 이전 버전 제거: ${OLD_PROJECT_NAME}"
docker compose -p $OLD_PROJECT_NAME down || true


# 7. 현재 포트 정보 업데이트
echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"
echo "🎉 배포 성공! 현재 서비스 포트: ${TARGET_PORT}"