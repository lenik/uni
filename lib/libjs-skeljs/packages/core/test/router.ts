
import { createRouter, createWebHashHistory } from 'vue-router';
import { RouterComponent } from 'vue-router';

import { routeRecordsFromTree, ui, uiExtract } from '../src/ui/routes';

import type { UiNode } from '../src/ui/tree/ui-node';
import { extract } from '../src/ui/tree/ui-node';

import Index from "./Index.vue";
import LayoutDemo from "./LayoutDemo.vue";
import MenuDemo from "./MenuDemo.vue";
import NodeTreeDemo from "./NodeTreeDemo.vue";
import UiDialogDemo from "./UiDialogDemo.vue";
import DialogDemo from "./DialogDemo.vue";
import IconDemo from "./IconDemo.vue";
import FieldRowDemo from "./FieldRowDemo.vue";
import CmdButtonsDemo from "./CmdButtonsDemo.vue";
import CgiDemo from "./CgiDemo.vue";

export const root = uiExtract([
    Index,
    {
        ...ui(LayoutDemo, 'model-c layout demo'),
        ...ui(MenuDemo, 'menu demo'),
        ...ui(NodeTreeDemo, 'recursive node tree demo'),
        ...ui(UiDialogDemo, 'jquery-ui dialog demo'),
        ...ui(DialogDemo, 'dialog demo'),
        ...ui(IconDemo, 'icon demo'),
        ...ui(FieldRowDemo, 'field row demo'),
        ...ui(CmdButtonsDemo, 'grouped cmd buttons demo'),
        ...ui(CgiDemo, 'CGI utils'),
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
