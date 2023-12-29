import $ from "jquery";

import { createApp } from "vue/dist/vue.esm-bundler";
import { createPinia } from 'pinia'

// import router from './router'
import App from "./App.vue";

const app = createApp(App);
app.use(createPinia());
// app.use(router);

app.mount("#app");

$(function () {


    $(document.body).on("keydown", function (e) {
        var el = $(e.target);
        if (el.hasClass("editable")) return;
        if (el.is("input, select")) return;

        switch (e.key) {
            case "Escape":
            case "Space":
            case "Delete":
                return;
        }
    });

    // fullScreen();
});
