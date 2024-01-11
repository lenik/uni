
import { createRouter, createWebHashHistory } from 'vue-router';
import { RouterComponent } from 'vue-router';

import { routeRecordsFromTree, ui, uiExtract } from '@skeljs/core/src/ui/routes';

import type { UiNode } from '@skeljs/core/src/ui/tree/ui-node';
import { extract } from '@skeljs/core/src/ui/tree/ui-node';

import Index from "./Index.vue";
import DataObjvDemo from "./DataTables/DataObjvDemo.vue";
import DataTabDemo from "./DataTables/DataTabDemo.vue";
import AjaxArrayDemo from "./DataTables/AjaxArrayDemo.vue";
import AjaxObjectDemo from "./DataTables/AjaxObjectDemo.vue";

export const root = uiExtract([
    Index,
    {
        ...ui(DataTabDemo, 'Data with any[]'),
        ...ui(DataObjvDemo, 'Data with DataTab'),
        ...ui(AjaxArrayDemo, 'Ajax with row=array'),
        ...ui(AjaxObjectDemo, 'Ajax with row=object'),
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
