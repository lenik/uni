
import { createRouter, createWebHashHistory } from 'vue-router';
import { RouterComponent } from 'vue-router';

import { routeRecordsFromTree, ui, uiExtract } from '@skeljs/core/src/ui/routes';

import type { UiNode } from '@skeljs/core/src/ui/tree/ui-node';
import { extract } from '@skeljs/core/src/ui/tree/ui-node';

import Index from "./Index.vue";
import DataTabDemo from "./DataTables/DataTabDemo.vue";
import DataObjvDemo from "./DataTables/DataObjvDemo.vue";
import AjaxArrayDemo from "./DataTables/AjaxArrayDemo.vue";
import AjaxObjectDemo from "./DataTables/AjaxObjectDemo.vue";
import DataAdminDemo from "./DataTables/DataAdminDemo.vue";

export const root = uiExtract([
    Index,
    {
        ...ui(DataTabDemo, 'Table data modeled in DataTab type'),
        ...ui(DataObjvDemo, 'Data row by object[]'),
        ...ui(AjaxArrayDemo, 'Ajax with row=array'),
        ...ui(AjaxObjectDemo, 'Ajax with row=object'),
        ...ui(DataAdminDemo, 'Data Admin demo'),
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
