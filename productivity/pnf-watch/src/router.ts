import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "Project": "component Project",
    "App": "component App",
    "Clock": "component Clock",
    "": 'Directory',
    "Main": "component Main",
    "Progress": "component Progress",
};

export const routes = [
    {
        name: 'Project', 
        path: '/Project', 
        component: () => import('../node_modules/skel01-core/src/ui/demo/Project.vue')
    },
    {
        name: 'App', 
        path: '/App', 
        component: () => import('./App.vue')
    },
    {
        name: 'Clock', 
        path: '/Clock', 
        component: () => import('./Clock.vue')
    },
    {
        name: 'Index', 
        path: '', 
        component: () => import('./Index.vue')
    },
    {
        name: 'Main', 
        path: '/Main', 
        component: () => import('./Main.vue')
    },
    {
        name: 'Progress', 
        path: '/Progress', 
        component: () => import('./Progress.vue')
    },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
});

export default router;
