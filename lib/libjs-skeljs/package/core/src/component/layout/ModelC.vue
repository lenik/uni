<script setup lang="ts">

import "../../skel/skel.scss";

import NavBar from '../nav/NavBar.vue';

</script>

<template>
    <div class="flex-column full-size">

        <div class="header clear-each">
            <slot name="header">
                <slot name="site-bar"></slot>
                <slot name="project-info"></slot>
            </slot>
        </div>

        <slot name="body">
            <div class="body flex-row flex-1">

                <div class="menu">
                    <slot name="menu"></slot>
                </div>

                <div class="flex-1">
                    <slot>
                    </slot>
                </div>
            </div>
        </slot>


        <div class="footer">
            <slot name="footer">
            </slot>
        </div>
    </div>
</template>

<style scoped lang="scss">
.header {
    &.hide {
        background: #89a;
        height: 5px;

        * {
            display: none;
        }
    }
}

.footer {
    padding: 3px 2em;
    border-top: solid 1px #ccc;
    background: #eee;
    color: gray;
    text-align: center;
    font-family: Serif;
}
</style>