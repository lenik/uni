import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "App": 'Core Test Site',
    "CgiDemo": 'CGI utils',
    "CmdButtonsDemo": 'grouped cmd buttons demo',
    "DateTimeDemo": "DateTime for various timezone",
    "DialogDemo": 'dialog demo',
    "FieldRowDemo": 'field row demo',
    "IconDemo": 'icon demo',
    "": 'List of Test Samples',
    "JsonEditorDemo": 'JSON Editor demo',
    "LayoutDemo": 'model-c layout demo',
    "MenuDemo": 'menu demo',
    "NodeTreeDemo": 'recursive node tree demo',
    "UiDialogDemo": 'jquery-ui dialog demo',
};

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        { path: '/', component: () => import('./Index.vue') },
        { path: '/App', component: () => import('./App.vue') },
        { path: '/CgiDemo', component: () => import('./CgiDemo.vue') },
        { path: '/CmdButtonsDemo', component: () => import('./CmdButtonsDemo.vue') },
        { path: '/DateTimeDemo', component: () => import('./DateTimeDemo.vue') },
        { path: '/DialogDemo', component: () => import('./DialogDemo.vue') },
        { path: '/FieldRowDemo', component: () => import('./FieldRowDemo.vue') },
        { path: '/IconDemo', component: () => import('./IconDemo.vue') },
        { path: '/JsonEditorDemo', component: () => import('./JsonEditorDemo.vue') },
        { path: '/LayoutDemo', component: () => import('./LayoutDemo.vue') },
        { path: '/MenuDemo', component: () => import('./MenuDemo.vue') },
        { path: '/NodeTreeDemo', component: () => import('./NodeTreeDemo.vue') },
        { path: '/UiDialogDemo', component: () => import('./UiDialogDemo.vue') },
    ]
});

export default router;
