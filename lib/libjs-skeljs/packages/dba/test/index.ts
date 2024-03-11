import { createApp } from "vue";
import { createPinia } from 'pinia'

import App from "./App.vue";
import router from './router'

let vuetify: any;
import "vuetify/styles";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";
vuetify = createVuetify({ components, directives });

const app = createApp(App);
app.use(router);
if (vuetify != null)
    app.use(vuetify);
app.use(createPinia());

app.mount("#app");