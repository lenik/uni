import $ from 'jquery';
import params from '../cgi/QueryString';

var win: any = window;

$(() => {

    win.appModel = {
        version: '1.0',
        view: 'list',
        dataFilter: $.extend({}, location.params),
    };

});
