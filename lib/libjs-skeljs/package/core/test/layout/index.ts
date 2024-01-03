import { createApp } from "vue/dist/vue.esm-bundler";
import { createPinia } from 'pinia'

import App from "./App.vue";

const app = createApp(App);
app.use(createPinia());

app.mount("#app");
