import { createRouter, createWebHashHistory } from 'vue-router';
import { routeRecordsFromTree, ui, uiExtract } from '@skeljs/core/src/ui/routes';

import Index from "./Index.vue";
import Main from "./Main.vue";
import Page1 from "./Page1.vue";
import Page2 from "./Page2.vue";
import Page3 from "./Page3.vue";
import Page4 from "./Page4.vue";

export const root = uiExtract([
    Index,
    {
        ...ui(Main, 'main'),
        ...ui(Page1, 'page1'),
        ...ui(Page2, 'page2'),
        ...ui(Page3, 'page3'),
        ...ui(Page4, 'page4'),
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
