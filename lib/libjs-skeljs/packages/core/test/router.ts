import { createRouter, createWebHashHistory } from 'vue-router';

export const links = {
    "": 'List of Test Samples',
    "Project": "component Project",
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
    "AutoColumnsDemo": "component AutoColumnsDemo",
    "DialogDemo": 'dialog demo',
    "LayoutDemo": 'model-c layout demo',
    "TextBoxDemo": "component TextBoxDemo",
    "UiDialogDemo": 'jquery-ui dialog demo',
};

export const routes = [
    { path: '/', component: () => import('./Index.vue') },
    { path: '/Project', component: () => import('../src/ui/demo/Project.vue') },
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
    { path: '/AutoColumnsDemo', component: () => import('./layout/AutoColumnsDemo.vue') },
    { path: '/DialogDemo', component: () => import('./layout/DialogDemo.vue') },
    { path: '/LayoutDemo', component: () => import('./layout/LayoutDemo.vue') },
    { path: '/TextBoxDemo', component: () => import('./layout/TextBoxDemo.vue') },
    { path: '/UiDialogDemo', component: () => import('./layout/UiDialogDemo.vue') },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
});

export default router;
