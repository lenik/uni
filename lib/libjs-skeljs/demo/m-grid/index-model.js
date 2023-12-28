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
        
        m3: [],
        m5: [],
        m9: [],
        m15: [],
        m30: [],
        m45: [],
        
        init: function(s) {
        }
    };
    
    var ul = $("#plans");
    if (ul.length)
        for (var li of ul[0].children) {
            var c = li.className;
            if (c == null) c= 'null';
            var list = appModel[c];
            if (list == null) continue;
            list.push({
                style: '',
                html: li.innerHTML
            });
        }
    
    app = new Vue({
        el: "#views",
        data: appModel,
        computed: {
        },

        watch: {
        },

        methods: {
            style: function(c, i) {
                var list = appModel['m' + c];
                if (list == null) return '';
                var item = list[i];
                if (item == null) return '';
                return item.style;
            },
            html: function(c, i) {
                var list = appModel['m' + c];
                if (list == null) return '';
                var item = list[i];
                if (item == null) return '';
                return item.html;
            }
        }
    });

});
