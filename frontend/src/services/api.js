import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'https://se.ifmo.ru/~s413022/api'
});