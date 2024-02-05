<script lang="ts">
import $ from 'jquery';
import { computed, onMounted, ref } from "vue"

import { Command } from '../types';

import Link from '../Link.vue';
import Icon from '../Icon.vue';
import ShareMenu from './ShareMenu.vue';

export interface Props {
    title?: string
    icon?: string
    home?: string
    menu?: Command[]
}

export const defaultMenu: Command[] = [

    {
        name: 'about',
        icon: 'far-info-circle',
        href: 'route-to:about',
    },

    {
        name: 'project',
        icon: 'fab-github',
        tooltip: 'The @skeljs framework of Uni toolset.',
        href: 'route-to:project',
    },

];

</script>

<script setup lang="ts">
defineOptions({
    inheritAttrs: false
})

const model = defineModel<string>();

const props = withDefaults(defineProps<Props>(), {
    title: 'Example App',
    icon: 'far-gift',
    menu: defaultMenu,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// app state

var rootElement = ref();

onMounted(() => {
});

let timeout = 300;
let killer;

function showContextMenu(e: Event) {
    let shareMenu = $(".share-menu");
    shareMenu.slideDown();
    if (killer != null) {
        window.clearTimeout(killer);
        killer = null;
    }
}

function hideContextMenu(e: Event) {
    let shareMenu = $(".share-menu");
    if (killer != null) {
        window.clearTimeout(killer);
        killer = null;
    }
    killer = setTimeout(() => shareMenu.slideUp(), timeout);
}

function clickCommand(cmd: Command, e: Event) {
    if (cmd.run != null)
        cmd.run(e, cmd);
}

function ucfirst(s: string) {
    if (s == null || s.length == 0) return s;
    let first = s.substring(0, 1).toUpperCase();
    return first + s.substring(1);
}

</script>

<template>
    <div class="sitebar" ref="rootElement" v-bind="$attrs">
        <Icon class="app" :name="icon" :href="home" />
        <ul class="menu left">
            <li v-if="title != null">
                <Link :href="home">{{ title }}</Link>
            </li>
            <li v-for="(item, i) in menu" :key="i" :title="item.tooltip">
                <Link target="blank" :href="item.href" @click="(e) => clickCommand(item, e)">
                <Icon :name="item.icon" v-if="item.icon != null" />
                <span class="label"> {{ item.label || ucfirst(item.name) }} </span>
                </Link>
            </li>
        </ul>
        <div>
            <ul class="menu right">
                <li><a><i class="fa fa-user" @click="login()"></i></a>
                </li>
                <li><a><i class="fa fa-share-alt" @mouseenter="(e) => showContextMenu(e)"
                            @mouseleave="(e) => hideContextMenu(e)"></i> </a>
                    <ShareMenu @mouseenter="(e) => showContextMenu(e)" @mouseleave="(e) => hideContextMenu(e)" />
                </li>
            </ul>
        </div>
        <Icon class="app" :name="icon" :href="home" />
    </div>
</template>

<style></style>
<style lang="scss" scoped>
.sitebar {
    display: flex;
    flex-direction: row;
    align-items: center;
    box-sizing: border-box;

    background: #888;
    padding: 0 1em;
    width: 100%;
}

.icon.app {
    color: hsl(0, 80%, 85%);
    font-size: 120%;
    text-shadow: 0px 0px 10px #338;

    &:last-child {
        margin-left: 1em;
    }
}

.menu {
    display: flex;
    flex-direction: row;
    align-items: center;

    margin: 0;
    padding: 0;
    font-size: 120%;
    color: #fff;

    &.left {
        flex: 1;
    }

    >* {
        display: inline-block;
    }

    &.left li {
        margin: 0 0 0 1em;

        &:first-child {
            font-weight: bold;
        }

        &:not(:first-child) {
            border-style: dashed;
            border-width: 1px;
            border-color: hsla(100, 30%, 80%, 50%);
            padding: 0 .3em;
            background-color: hsl(220, 10%, 50%);
            font-size: 80%;
            font-weight: 300;

            &:hover {
                background-color: hsl(100, 30%, 30%);
            }
        }

        a {
            display: inline-flex;
            flex-direction: row;
            align-items: center;
        }

        .icon {
            // font-size: 50%;
            color: hsl(100, 30%, 80%);
        }

        .label {
            margin-left: .5em;
        }
    }

    &.right li {
        &:not(:first-child) {
            margin-left: 1em;
        }
    }

    .level1 {
        font-size: 90%;
        padding: 0.5em 0.5em;
        border-left: solid 1px gray;
        border-right: solid 1px gray;
    }

    .level2 {
        font-size: 90%;
    }


    .btn {
        padding: .1em .5em;
        border: solid 1px rgba(0, 0, 0, 0);
        border-radius: 4px;
        cursor: pointer;

        &:hover {
            background: #9ca;
        }
    }

    a {
        color: #fff;
    }
}
</style>