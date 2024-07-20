<script lang="ts">

import { computed } from "vue";

import "../../skel/skel.scss";
import "@fontsource/noto-sans-sc/chinese-simplified.css";

export interface Props {
    scrollable?: boolean
}

</script>

<script setup lang="ts">

const props = withDefaults(defineProps<Props>(), {
    scrollable: true,
});

const emit = defineEmits<{
    error: [message: string]
}>();

const mainOverflowType = computed(() => props.scrollable ? 'auto' : 'hidden');

</script>

<template>
    <div class="model-q">
        <div class="header clear-each">
            <slot name="header">
                <slot name="site-bar"></slot>
                <slot name="project-info"></slot>
            </slot>
        </div>
        <div class="nav-main">
            <div class="navleft">
                <slot name="navleft"></slot>
            </div>
            <slot name="main">
                <div class="main">
                    <slot>
                    </slot>
                    <slot name="body">
                    </slot>
                </div>
            </slot>
            <div class="navright">
                <slot name="navright"></slot>
            </div>
        </div>
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
.model-q {
    display: flex;
    flex-direction: column;

    width: 100%;
    height: 100%;
    box-sizing: border-box;

    font-family: "Noto Sans SC";
    font-weight: 300;
}

.header {
    &.hide {
        background: #89a;
        height: 5px;

        * {
            display: none;
        }
    }
}

.nav-main {
    overflow: hidden;
    display: flex;
    flex-direction: row;
    flex: 1;

    > ::v-deep(.main) {
        position: relative;
        flex: 1;
        overflow: v-bind(mainOverflowType);
    }
}
</style>