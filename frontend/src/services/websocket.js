import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';

export function createWebSocket(onMessage) {
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = new Client({
        webSocketFactory: () => socket,
        onConnect: () => {
            stompClient.subscribe('/topic/groups', msg => {
                onMessage(JSON.parse(msg.body));
            });
        },
    });
    stompClient.activate();
    return stompClient;
}
