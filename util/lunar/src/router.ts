import { createRouter, createWebHashHistory } from 'vue-router';
import { routeRecordsFromTree, ui, uiExtract } from '@skeljs/core/src/ui/routes';

import Index from "./Index.vue";
import Main from "./Main.vue";
import About from "./About.vue";
import Project from '@skeljs/core/src/ui/demo/Project.vue';

export const root = uiExtract([
    Index,
    {
        ...ui(Main, 'main'),
        ...ui(About, 'about'),
        ...ui(Project, 'project'),
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
