$(document).ready(function() {

    appModel = {
        version: '1.0',
        view: 'about',
        views: {
            about: {
                label: "About",
                iconfa: "info"
            },
            options: {
                label: "Options",
                iconfa: "cog"
            }
        },
        stat: {
            views: 0,
            stars: 0,
            upvotes: 0,
            downvotes: 0
        },
        fa_list: window.fa_list,

        init: function(s) {
        }
    };

    var clipboard = new ClipboardJS("#chars li", {
        text: function (li) {
            var name = $(".name", li).text().trim();
            return name;
        }
    });
    
    app = new Vue({
        el: "#views",
        data: appModel,
        computed: {
        },

        watch: {
        },

        methods: {
            copy: function(el) {
                
            }
        }
    });

    new Vue({ el: "#proj-info", data: app });
    new Vue({ el: "#view-nav", data: app });

});
