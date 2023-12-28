import $ from "jquery";

import skel from "skeljs";
import Vue from "skeljs/js/enh/vue.js";
import * as Url from "skeljs/js/io/url.js";

import * as site from "../site.js";

class SiteModel extends Vue {
    constructor() {
        super();
        $.extend(this, site.model, {
            version: "1.0",
            stat: {
                views: 0,
                stars: 0,
                upvotes: 0,
                downvotes: 0,
            },
        });
    }
}

class ViewModel extends Vue {
    constructor() {
        super();
        $.extend(this, {
            view: "about",
            views: {
                about: {
                    label: "About",
                    iconfa: "info",
                },
                options: {
                    label: "Options",
                    iconfa: "cog",
                },
            },
        });
    }

    init(s) {}
}

$(function () {
    var site = new SiteModel();
    site.$mount("#proj-info");
    var view = new ViewModel();
    view.$mount("#view-container");

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

class Car {
    constructor(name) {
        this.name = name;
        console.log("Car::ctor");
    }
    run() {
        console.log("Car::run");
    }
    toString() {
        return "Car<" + this.name + ">";
    }
}

class Model extends Car {
    constructor(name, mod) {
        super(name);
        this.model = mod;
    }
    run() {
        console.log("Model::run(" + this.model + ")");
    }
    show() {
        return "model: " + this.model;
    }
    toString() {
        return this.show();
    }
}
