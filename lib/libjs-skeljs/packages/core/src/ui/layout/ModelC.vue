<script setup lang="ts">

import "../../skel/skel.scss";
import "../../font/NotoSansSC.scss";

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

                <slot name="main">
                    <div class="main">
                        <slot>
                        </slot>
                    </div>
                </slot>
            </div>
        </slot>


        <slot name="footer">
        </slot>
    </div>
</template>

<style lang="scss">
.main>* {
    margin: 1em;
}
</style>

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

::v-deep() {
    .body {
        overflow: hidden;

        >.main {
            flex: 1;
            overflow: hidden;
        }
    }
}
</style>