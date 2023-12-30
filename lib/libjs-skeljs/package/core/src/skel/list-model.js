$(document).ready(function() {

    window.appModel = {
        version: '1.0',
        view: 'list',

        css: {
            dataTable: [ "table", "table-striped", "table-hover", "table-condensed", "dataTable", "table-responsive" ]
        },
        
        dataFilter: $.extend({}, location.params),

        init: function(s) {
        }
    };

    window.app =
    new Vue({
        el: "#views",
        data: window.appModel,
        computed: {
        },

        watch: {
        },

        methods: {
        }
    });

    new Vue({ el: "#view-nav", data: app });
    
});
