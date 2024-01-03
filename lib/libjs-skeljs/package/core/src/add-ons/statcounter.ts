
import $ from 'jquery';

export var config = {
     project: 11389146,
    invisible: 0,
    security: "cb1bef8f",
};

export default function install(el, cfg) {
    if (typeof el == 'string')
        el = $(el);
    if (cfg == null)
        cfg = config;
    window.sc_project = cfg.project;
    window.sc_invisible = cfg.invisible;
    window.sc_security = cfg.security;

    var url;
    switch (document.location.protocol) {
        case "file:":
            el.text("(n/a counter @dev)");
            return;
        case "http:":
            url = "http://www.statcounter.com/counter/counter.js";
            break;
        case "https:":
            url = "https://secure.statcounter.com/counter/counter.js";
            break;
    }
    // el.html("<script type='text/javascript' src='" + url + "'></script>");
    
    var img = "//c.statcounter.com/" + config.project 
        + "/" + config.invisible
        + "/" + config.security
        + "/0/";
    $('img.statcounter', el).attr('src', img);
}
