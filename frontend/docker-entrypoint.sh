#!/bin/sh
set -eu

: "${BACKEND_URL:=http://localhost:8080}"

# Renders nginx.conf.template -> the real nginx config, substituting only
# ${BACKEND_URL} (the third-arg list keeps envsubst from also mangling
# nginx's own $uri/$host variables, which look similar).
envsubst '${BACKEND_URL}' < /etc/nginx/templates/nginx.conf.template > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
