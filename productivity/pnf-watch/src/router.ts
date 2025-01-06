import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "Project": "component Project",
    "App": "component App",
    "": 'Directory',
    "Main": "component Main",
    "Page1": "component Page1",
    "Page2": "component Page2",
    "Page3": "component Page3",
    "Page4": "component Page4",
};

export const routes = [
    { path: '/Project', component: () => import('skel01-core/src/ui/demo/Project.vue') },
    { path: '/App', component: () => import('./App.vue') },
    // { path: '/', component: () => import('./Index.vue') },
    { path: '/', component: () => import('./Main.vue') },
    { path: '/Main', component: () => import('./Main.vue') },
    { path: '/Page1', component: () => import('./Page1.vue') },
    { path: '/Page2', component: () => import('./Page2.vue') },
    { path: '/Page3', component: () => import('./Page3.vue') },
    { path: '/Page4', component: () => import('./Page4.vue') },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
});

export default router;
