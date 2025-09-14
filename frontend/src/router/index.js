import {createRouter, createWebHistory} from 'vue-router';
import GroupList from '../views/GroupList.vue';

const routes = [
    {path: '/', name: 'GroupList', component: GroupList},
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
