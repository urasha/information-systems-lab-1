import {createRouter, createWebHashHistory} from 'vue-router';
import GroupList from '../views/GroupList.vue';

const routes = [
    {path: '/', name: 'GroupList', component: GroupList},
];

const router = createRouter({
    history: createWebHashHistory(),
    routes,
});

export default router;
