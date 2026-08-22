# ---- Build stage: compile the Angular app into static files ----
FROM node:20-alpine AS build
WORKDIR /build
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

# ---- Runtime stage: nginx serving the static build + reverse-proxying /api ----
FROM nginx:1.27-alpine

RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
COPY --from=build /build/dist/tasktracker-frontend /usr/share/nginx/html

# Overridden at runtime (docker-compose / Kubernetes ConfigMap) with the
# real backend address; this default only matters if the container is run
# standalone with nothing else configured.
ENV BACKEND_URL=http://localhost:8080

EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
