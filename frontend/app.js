import express from 'express';
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import client from 'prom-client';

const app = express();
const metricsApp = express();

client.collectDefaultMetrics({
    prefix: 'ktb_board_express_',
});

dotenv.config();

const port = process.env.PORT; 
const metricsPort = process.env.METRICS_PORT || 9090;

// 현재 파일의 URL에서 디렉토리 경로를 추출
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

app.use(express.static(__dirname));

app.get('/config.js', (req, res) => {
    const apiBaseUrl = process.env.API_BASE_URL || '';
    res.set('Cache-Control', 'no-store');
    res.type('application/javascript').send(
        `window.__APP_CONFIG__ = ${JSON.stringify({
            API_BASE_URL: apiBaseUrl,
        })};`,
    );
});

app.get('/health', (req, res) => {
    res.status(200).send('ok');
});

metricsApp.get('/metrics', async (req, res) => {
    try {
        res.set('Content-Type', client.register.contentType);
        res.end(await client.register.metrics());
    } catch (error) {
        res.status(500).end(error.message);
    }
});

app.get('/', (req, res) => {
    res.redirect('/html/index.html');
});

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});

metricsApp.listen(metricsPort, () => {
    console.log(`Metrics server is running on port ${metricsPort}`);
});
