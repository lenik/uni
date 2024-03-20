import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "": "component ",
    "App": "component App",
    "Main": "component Main",
};

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        { path: '/', component: () => import('./Index.vue') },
        { path: '/App', component: () => import('./App.vue') },
        { path: '/Main', component: () => import('./Main.vue') },
    ]
});

export default router;
