import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';

const DEFAULT_WS_URL = "https://localhost:8081/ws";

export function createWebSocket(onMessage, opts = {}) {
    const url = opts.url || DEFAULT_WS_URL;

    const client = new Client({
        webSocketFactory: () => new SockJS(url),
        reconnectDelay: opts.reconnectDelay ?? 5000,
        debug: (str) => {
            console.debug('[STOMP]', str);
        },
        onConnect: (frame) => {
            client.subscribe('/topic/groups', (msg) => {
                let payload = msg.body;
                try {
                    payload = JSON.parse(msg.body);
                } catch (e) {
                    payload = {event: 'raw', body: msg.body};
                    console.warn('WS: non-JSON payload received', msg.body);
                }
                try {
                    onMessage(payload);
                } catch (e) {
                    console.error('onMessage handler error', e);
                }
            });
            console.info('STOMP connected', frame);
        },
        onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Details: ' + frame.body);
        },
        onWebSocketError: (evt) => {
            console.error('WebSocket error', evt);
        }
    });

    client.activate();
    return client;
}
