import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "App": 'Core Test Site',
    "": 'List of Test Samples',
    "IconDemo": 'icon demo',
    "MenuDemo": 'menu demo',
    "NodeTreeDemo": 'recursive node tree demo',
    "CgiDemo": 'CGI utils',
    "CmdButtonsDemo": 'grouped cmd buttons demo',
    "DateTimeDemo": "DateTime for various timezone",
    "FieldRowDemo": 'field row demo',
    "JsonEditorDemo": 'JSON Editor demo',
    "BigInt": "component BigInt",
    "DateFormats": "Table of date formats",
    "JsonWithRefs": 'JSON with (circular) references: parser and formatter test.',
    "DialogDemo": 'dialog demo',
    "LayoutDemo": 'model-c layout demo',
    "UiDialogDemo": 'jquery-ui dialog demo',
};

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        { path: '/App', component: () => import('./App.vue') },
        { path: '/', component: () => import('./Index.vue') },
        { path: '/IconDemo', component: () => import('./common/IconDemo.vue') },
        { path: '/MenuDemo', component: () => import('./composite/MenuDemo.vue') },
        { path: '/NodeTreeDemo', component: () => import('./composite/NodeTreeDemo.vue') },
        { path: '/CgiDemo', component: () => import('./fn/CgiDemo.vue') },
        { path: '/CmdButtonsDemo', component: () => import('./input/CmdButtonsDemo.vue') },
        { path: '/DateTimeDemo', component: () => import('./input/DateTimeDemo.vue') },
        { path: '/FieldRowDemo', component: () => import('./input/FieldRowDemo.vue') },
        { path: '/JsonEditorDemo', component: () => import('./input/JsonEditorDemo.vue') },
        { path: '/BigInt', component: () => import('./lang/BigInt.vue') },
        { path: '/DateFormats', component: () => import('./lang/DateFormats.vue') },
        { path: '/JsonWithRefs', component: () => import('./lang/JsonWithRefs.vue') },
        { path: '/DialogDemo', component: () => import('./layout/DialogDemo.vue') },
        { path: '/LayoutDemo', component: () => import('./layout/LayoutDemo.vue') },
        { path: '/UiDialogDemo', component: () => import('./layout/UiDialogDemo.vue') },
    ]
});

export default router;
