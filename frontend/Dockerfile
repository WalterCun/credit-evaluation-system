FROM node:20-alpine AS builder
WORKDIR /app

COPY package.json pnpm-lock.yaml .npmrc ./
RUN npm install -g pnpm@10.33.0 && pnpm install --frozen-lockfile

COPY vite.config.ts tsconfig.json ./
COPY tailwind.config.js postcss.config.js ./
COPY index.html ./
COPY src ./src

ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
ARG VITE_ENV
ENV VITE_ENV=$VITE_ENV

RUN pnpm run build

FROM nginx:1.27-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
