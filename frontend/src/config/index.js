const config = {
    api: {
        baseURL: import.meta.env.VITE_API_URL,
    },
    websocket: {
        url: import.meta.env.VITE_WS_URL,
    }
};

if (!config.api.baseURL) {
    throw new Error('VITE_API_URL is not defined in environment variables');
}

if (!config.websocket.url) {
    throw new Error('VITE_WS_URL is not defined in environment variables');
}

console.log('API_URL:', import.meta.env.VITE_API_URL);

export default config;
