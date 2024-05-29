#!/bin/sh
# Load .env file
if [ -f .env ]; then
    export $(cat .env | xargs)
fi

# Switch blue-green
if [ "$1" = "blue" ]; then
    export BLUE_WEIGHT=""
    export GREEN_WEIGHT="down"
    echo "Switch to BLUE"
elif [ "$1" = "green" ]; then # Starting point
    export BLUE_WEIGHT="down"
    export GREEN_WEIGHT=""
    echo "Switch to GREEN"
elif [ "$1" = "restart" ]; then # Starting point
    export BLUE_WEIGHT=""
    export GREEN_WEIGHT="down"
    echo "Start with blue"
else
    echo "WRONG PARAMETER!!!"
    exit
fi

# Render conf
# 환경변수 값으로 nginx.conf 파일 생성
envsubst '\
    $SERVER_DOMAIN,\
    $GERRIT_PORT,\
    $JENKINS_PORT,\
    $BLUE_WEIGHT,\
    $GREEN_WEIGHT,\
    $API_PORT,\
    $DEV_API_PORT,\
    $OPS_API_PORT,\
    $GREEN_API_PORT,\
    $BLUE_API_PORT,\
    $SSL_CERT_PATH,\
    $SSL_KEY_PATH\
    ' < ./http.conf.template > ./http.conf

envsubst '\
    $SERVER_DOMAIN,\
    $BLUE_WEIGHT,\
    $GREEN_WEIGHT,\
    $SOCKET_PORT,\
    $DEV_SOCKET_PORT_OUT,\
    $DEV_SOCKET_PORT,\
    $OPS_SOCKET_PORT_OUT,\
    $OPS_SOCKET_PORT,\
    $BLUE_SOCKET_PORT,\
    $GREEN_SOCKET_PORT,\
    ' < ./stream.conf.template > ./stream.conf

if [ "$1" = "restart" ]; then
    docker compose down && docker compose up --build -d
fi


# Reload nginx container
docker exec nginx nginx -s reload
