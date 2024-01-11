
import { createRouter, createWebHashHistory } from 'vue-router';
import { RouterComponent } from 'vue-router';

import { routeRecordsFromTree, ui, uiExtract } from '../src/ui/routes';

import type { UiNode } from '../src/ui/tree/ui-node';
import { extract } from '../src/ui/tree/ui-node';

import Index from "./Index.vue";
import LayoutDemo from "./LayoutDemo.vue";
import MenuDemo from "./MenuDemo.vue";
import NodeTreeDemo from "./NodeTreeDemo.vue";

export const root = uiExtract([
    Index,
    {
        ...ui(LayoutDemo, 'model-c layout demo'),
        ...ui(MenuDemo, 'menu demo'),
        ...ui(NodeTreeDemo, 'recursive node tree demo'),
    }, {
        label: 'index page'
    }
]);

const records = routeRecordsFromTree(root);

const router = createRouter({
    history: createWebHashHistory(),
    routes: records,
});

export default router;
