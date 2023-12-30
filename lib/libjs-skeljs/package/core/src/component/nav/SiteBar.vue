<script setup lang="ts">

import {} from "../../skel/fonts.js";
import 'font-awesome/css/font-awesome.css';

interface Item {
    label: string
    href: string
    iconfa: string
}

defineProps<{
    title: string
    items: Item[]
}>();

</script>

<template>
    <div class="sitebar">
        <a class="icon">
            <slot name="icon">
                <svg version="1.1" viewBox="0 0 1000 1000" width="32" height="32" style="fill: #ffd">
                    <path
                        d="M974,581.3C959.7,479.1,829.1,474,829.1,474c0-32.7-43.5-51.9-43.5-51.9h-625c-40.2,0-46.1,56.1-46.1,56.1c8.4,315,198.5,418.9,198.5,418.9h327.5c46.9-34.3,111.4-134,111.4-134c10.1,3.4,79.6-0.9,107.2-6.7C886.7,750.5,988.2,683.4,974,581.3z M339.8,842.7C180.7,727.1,196.6,483.3,196.6,483.3h88C236,621.5,332.3,795.8,345.7,820C359.1,844.3,339.8,842.7,339.8,842.7z M851.6,699.4c-41.1,24.4-71.3,8.4-71.3,8.4c42.7-59.5,46.1-174.2,46.1-174.2c7.5,2.5,13.4-28.5,67.1,26C947.1,613.9,892.7,675.1,851.6,699.4z" />
                    <path d="M90.3,990h738.4c36.3,0,65.7-31,65.7-69.4H24.7C24.7,959,54,990,90.3,990z" />
                    <path
                        d="M324.6,82c0,0-130.6,79.7-48.6,154.1c82,74.5,74.5,102.2,26.8,167.6c0,0,89.6-45.2,77.8-114c-11.8-68.7-98-103-79.6-150.7C319.7,91.3,324.6,82,324.6,82z" />
                    <path
                        d="M612.9,40.2c0,0-130.7,79.7-48.5,154.1c82,74.5,74.4,102.2,26.7,167.6c0,0,89.7-45.2,77.9-114c-11.7-68.7-98-103-79.7-150.7C607.9,49.4,612.9,40.2,612.9,40.2z" />
                    <path
                        d="M477.2,10c0,0-81.3,6.7-96.4,85.5c-15,78.8,36,108.1,75.4,147.5c39.3,39.3,22.6,113-21.8,165.1c0,0,79.6-27.7,98-103.9c18.4-76.3-19-103.1-57.7-147.4C435.8,112.3,439.4,31.8,477.2,10z" />
                </svg>
            </slot>
        </a>

        <ul class="inline-block site-nav flex-1">
            <li v-if="title != null">
                <a href="#">{{ title }}</a>
            </li>

            <li v-for="(it, i) in items" :key="i">
                <a :href="it.href">
                    <i class="fa" :class="it.iconfa" v-if="it.iconfa != null"></i>
                    {{ it.label }}</a>
            </li>
        </ul>

        <div class="site-nav user-nav">
            <slot name="right">
                <ul class="inline-block">
                    <li><a><i class="fa fa-user"></i></a>
                    </li>
                    <li><a><i class="fa fa-share-alt"></i></a>
                    </li>
                </ul>
            </slot>
        </div>
    </div>
</template>

<style>
</style>
<style lang="scss" scoped>
.sitebar {
    display: flex;
    box-sizing: border-box;

    background: #888;
    padding: 0 1em;
    width: 100%;

    >* {
        vertical-align: middle;
    }

    .icon {}

    .site-nav {
        display: inline-block;
        margin-left: 1em;
        font-weight: bold;
        font-family: sans;
        font-size: 120%;
        color: #fff;

        >* {
            display: inline-block;
        }

        ul {}

        li {
            margin: 0 .2em;
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
}
</style>