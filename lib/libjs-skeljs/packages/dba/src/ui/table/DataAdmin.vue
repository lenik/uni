<script setup lang="ts">

import $, { data } from "jquery";

import { ref, onMounted, computed } from "vue";
import { getCurrentInstance } from 'vue';

import { showError } from "@skeljs/core/src/logging/api";

import type { DataTab, SymbolCompileFunc } from "./types";
import formats from "./formats";
import { useAjaxDataTable, useDataTable } from "./apply";
import { objv2Tab } from "./objconv";

interface Props {
    tools?: Command[]
    statuses?: Status[]

}

const props = withDefaults(defineProps<Props>(), {
    watch: false
});


defineOptions({
    inheritAttrs: false
})

onMounted(() => {
});

</script>

<template>
    <div class="data-admin">
        <div class="table-tooling">
            <div class="left">
                <ul class="toolbar">
                    <slot name="toolbar">
                        <CmdButtons :src="tools" vpos="top" pos="left" />
                        <li class="filler"></li>
                        <CmdButtons :src="tools" vpos="top" pos="right" />
                    </slot>
                </ul>

                <slot name="table">
                    <DataTable v-bind="$attrs">
                        <slot name="columns">
                        </slot>
                    </DataTable>
                </slot>

                <slot name="table-below">
                    <div class="table-below">
                    </div>
                </slot>
            </div>
            <div class="right">
                <div class="side-tools">
                    <slot name="side-tools">

                    </slot>
                </div>
                <slot name="preview">
                    preview pane, detachable to floating window.
                </slot>
            </div>
            <slot name="statusbar">
                <ul class="statusbar">
                    <StatusPanels :src="statuses" vpos="bottom" pos="left" />
                    <li class="filler"></li>
                    <StatusPanels :src="statuses" vpos="bottom" pos="right" />
                </ul>
            </slot>
        </div>

        <div class="editor">
            <slot name="editor"></slot>
            <slot></slot>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.data-admin {}
</style>