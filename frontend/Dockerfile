# Stage 1: Build
FROM node:20-alpine AS builder
WORKDIR /app

# Copy package files first for better layer caching
COPY package.json pnpm-lock.yaml ./

# Install dependencies
RUN npm install -g pnpm && pnpm install --frozen-lockfile

# Copy all necessary files for build
COPY vite.config.ts tsconfig.json ./
COPY tailwind.config.js postcss.config.js ./
COPY index.html ./
COPY src ./src

# Copy public directory if it exists (optional)
COPY public ./public 2>/dev/null || echo "No public directory to copy"

# Build environment variables (can be overridden at build time)
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
ARG VITE_ENV
ENV VITE_ENV=$VITE_ENV

# Build the application
RUN pnpm run build

# Stage 2: Production with Nginx
FROM nginx:alpine

# Remove default nginx html
RUN rm -rf /usr/share/nginx/html/*

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose standard HTTP port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Start nginx in foreground
CMD ["nginx", "-g", "daemon off;"]