import $ from "jquery";
import Vue from "vue/dist/vue";

export default Vue;

window.setView = function(k) {
    var prev = this.view;
    if (prev != null) {
        var $prev = $("#v-" + prev);
        var onleave = $prev.attr("onleave");
        if (onleave != null) eval(onleave);
    }
    
    var navLink = $("#view-nav>[href=" + k + "]");
    navLink.siblings().removeClass("selected");
    navLink.addClass("selected");

    this.view = k;

    var $nextView = $("#v-" + k);
    $nextView.siblings().removeClass("selected");
    $nextView.addClass("selected");
    this.viewData = $nextView.data();
    
    var onenter = $nextView.attr("onenter");
    if (onenter != null) eval(onenter);
    
    return false;
};

window.appConfig = {
    mounted: function() {
        setView(this.view);
    }
};

Vue.prototype.window = window;
