import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "": 'Directory',
    "Project": 'Project',
    "AjaxArrayDemo": 'Ajax with row=array',
    "AjaxObjectDemo": 'Ajax with row=object',
    "DataAdminDemo": 'Data Admin demo',
    "DataObjvDemo": 'Data row by object[]',
    "DataTabDemo": 'Table data modeled in DataTab type',
    "PersonAdmin": 'Person Admin',
};

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        { path: '/', component: () => import('./Index.vue') },
        { path: '/Project', component: () => import('@skeljs/core/src/ui/demo/Project.vue') },
        { path: '/AjaxArrayDemo', component: () => import('./DataTables/AjaxArrayDemo.vue') },
        { path: '/AjaxObjectDemo', component: () => import('./DataTables/AjaxObjectDemo.vue') },
        { path: '/DataAdminDemo', component: () => import('./DataTables/DataAdminDemo.vue') },
        { path: '/DataObjvDemo', component: () => import('./DataTables/DataObjvDemo.vue') },
        { path: '/DataTabDemo', component: () => import('./DataTables/DataTabDemo.vue') },
        { path: '/PersonAdmin', component: () => import('../src/net/bodz/lily/schema/contact/PersonAdmin.vue') },
    ]
});

export default router;
