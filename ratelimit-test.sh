#!/bin/bash

# 1. 로그인 요청
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"admin", "password":"admin"}' \
  | jq -r '.accessToken')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
  echo "❌ 로그인 실패. 토큰을 가져오지 못했습니다."
  exit 1
fi

echo "✅ 로그인 성공. 토큰: $TOKEN"

# 2. API 호출 70회
for i in {1..70}; do
  RESPONSE=$(curl -s -D - -o /dev/null \
    -H "Authorization: Bearer $TOKEN" \
    http://localhost:8080/reservations/my)

  CODE=$(echo "$RESPONSE" | grep HTTP | awk '{print $2}')
  RETRY=$(echo "$RESPONSE" | grep -i "Retry-After" | awk '{print $2}')
  
  if [ -n "$RETRY" ]; then
    echo "$i : $CODE (Retry-After: ${RETRY}s)"
  else
    echo "$i : $CODE"
  fi
done

