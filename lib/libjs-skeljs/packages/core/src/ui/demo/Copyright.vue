<script setup lang="ts">

import $ from 'jquery';

import { computed, onMounted, ref } from "vue";
import { } from "./types";

import Icon from '../Icon.vue';

const model = defineModel();

interface Props {
    autohide?: boolean
    delay?: number
    icon?: string
    date?: string
    author?: string
    org?: string
    notice?: string
    count?: number | string
}

const props = withDefaults(defineProps<Props>(), {
    autohide: true,
    delay: 5000,
    icon: 'far-copyright',
    author: 'My Name',
    org: 'My Company',
    notice: "All rights reserved.",
    count: 1234567,
});

const dateRange = computed(() => {
    let date = props.date;
    if (date == null) {
        let now = new Date();
        let year = now.getFullYear();
        date = "" + year;
    }
    return date;
});

const countIsNumber = computed(() => typeof props.count == 'number');

const rootElement = ref<HTMLElement>();
onMounted(() => {
    if (props.autohide) {
        setTimeout(hide, props.delay);
    }
});

function show() {
    $(rootElement.value!).slideDown();
}

function hide() {
    $(rootElement.value!).slideUp();
}

function format(n) {
    let s = '';
    while (n != 0) {
        let mod = n % 1000;
        n = Math.floor(n / 1000);
        if (s.length)
            s = ',' + s;
        s = mod + s;
    }
    return s;
}

defineExpose({ show, hide });

</script>

<template>
    <div class="copyright" ref="rootElement">
        <ul class="info">

            <li class="head">
                <Icon :name="icon" />

                <span class="literal">
                    Copyright
                </span>
            </li>

            <li class="date">
                {{ dateRange }}
            </li>

            <li class="author" v-if="author != null">
                <Icon name="far-user-circle" />
                <span> {{ author }} </span>
            </li>

            <li class="org" v-if="org != null">
                <Icon name="far-building" />
                <span> {{ org }}</span>
            </li>

            <li class="notice">
                <Icon name="far-exclamation-circle" />
                <span> {{ notice }} </span>
            </li>
        </ul>

        <div class="count" v-if="count != null">
            <Icon name="far-eye" />
            <span class="label"> Visit Count: </span>
            <span class="number" v-if="countIsNumber">
                {{ format(count) }}
            </span>
            <div id="statcounter">
                <img class="statcounter">
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.copyright {
    padding: 3px 2em;
    background: hsl(0, 0%, 95%);
    // border-top: solid 1px hsl(0, 0%, 80%);
    color: hsl(0, 0%, 20%);

    display: flex;
    flex-direction: row;

    font-family: monospace;
    font-weight: 300;
    font-size: 80%;

}

.info {
    flex: 1;
    display: flex;
    flex-direction: row;
    list-style: none;
    margin: 0;
    padding: 0;

    >li:not(:first-child) {
        margin-left: 1em;

        span {
            margin-left: .5em;
        }
    }

    .notice {
        font-style: italic;
    }
}

.author,
.org,
.notice,
.count {
    .icon {
        position: relative;
        font-size: 50%;
        // top: -30%;
        color: hsl(220, 20%, 60%);
    }
}

.number {
    // font-weight: normal;
    color: hsl(190, 40%, 50%);
}

.count {}
</style>
