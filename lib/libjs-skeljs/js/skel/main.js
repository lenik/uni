
import '../../css/skel.scss';

import $ from 'jquery';
import Vue from '../enh/vue.js';

import installStatCounter from '../add-ons/statcounter';

$(() => {
    installStatCounter('#statcounter');
});
