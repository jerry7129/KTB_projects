FROM node:22-alpine

ENV NODE_ENV=production

WORKDIR /app
RUN chown node:node /app

USER node

COPY --chown=node:node package*.json ./
RUN npm ci --omit=dev && npm cache clean --force

COPY --chown=node:node . .

EXPOSE 3000

CMD ["node", "app.js"]