FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json .
RUN npm install
COPY src ./src
COPY index.html .
COPY tsconfig.json .
COPY vite.config.ts .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
COPY package.json .
RUN npm install --only=production
COPY --from=builder /app/dist ./dist
EXPOSE 5173
CMD ["npx", "vite", "preview", "--host", "0.0.0.0", "--port", "5173"]