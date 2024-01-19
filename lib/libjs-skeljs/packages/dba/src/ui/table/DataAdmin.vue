<script setup lang="ts">

import $, { data } from "jquery";

import { ref, onMounted, computed } from "vue";
import { getCurrentInstance } from 'vue';

import { showError } from "@skeljs/core/src/logging/api";

import type { DataTab, SymbolCompileFunc } from "./types";
import formats from "./formats";
import { useAjaxDataTable, useDataTable } from "./apply";
import { objv2Tab } from "./objconv";

import DataTable from './DataTable.vue';
import CmdButtons from '@skeljs/core/src/ui/CmdButtons.vue';
import StatusPanels from '@skeljs/core/src/ui/StatusPanels.vue';
import { Command, Status } from "@skeljs/core/src/ui/types";

interface Props {
    tools: Command[]
    statuses: Status[]
}

const props = withDefaults(defineProps<Props>(), {
});


defineOptions({
    inheritAttrs: false
})

onMounted(() => {
});

</script>

<template>
    <div class="data-admin with-border">
        <div class="table-tooling">
            <div class="workpane">
                <ul class="toolbar top">
                    <CmdButtons :src="tools" class="left" vpos="top?" pos="left?" />
                    <li class="filler"></li>
                    <CmdButtons :src="tools" class="right" vpos="top?" pos="right" />
                </ul>

                <div class="table">
                    <slot name="table">
                        <DataTable v-bind="$attrs">
                            <slot name="columns">
                            </slot>
                        </DataTable>
                    </slot>
                </div>

                <slot name="table-below">
                    <div class="table-below">
                    </div>
                </slot>

                <ul class="toolbar bottom">
                    <CmdButtons :src="tools" class="left" vpos="bottom" pos="left?" />
                    <li class="filler"></li>
                    <CmdButtons :src="tools" class="right" vpos="bottom" pos="right" />
                </ul>
            </div>
            <div class="helppane">
                <div class="side-tools">
                    <slot name="side-tools">
                    </slot>
                </div>
                <div class="preview">
                    <slot name="preview">
                        preview pane, detachable to floating window.
                    </slot>
                </div>
            </div>
        </div>

        <ul class="statusbar">
            <slot name="statusbar">
                <StatusPanels :src="statuses" vpos="bottom?" pos="left" />
                <li class="filler"></li>
                <StatusPanels :src="statuses" vpos="bottom?" pos="right?" />
            </slot>
        </ul>

        <div class="editor">
            <slot name="editor"></slot>
            <slot></slot>
        </div>
    </div>
</template>

<style lang="scss"></style>

<style lang="scss" scoped>
.data-admin {
    display: flex;
    flex-direction: column;
    // background: #f8f8f8;

    // background-color: #e5e5f7;
    // background: repeating-linear-gradient(-45deg, #444cf7, #444cf7 5px, #e5e5f7 5px, #e5e5f7 25px);

    background-color: #f8f8f8;
}

.table-tooling {
    flex: 1;
    display: flex;
    flex-direction: row;
}

.workpane {
    flex: 1;
    display: flex;
    flex-direction: column;

    .table {
        flex: 1;
        margin: .5em 0;
    }
}

.helppane {
    >* {
        padding: .5em;
    }

}

.toolbar {
    display: flex;
    flex-direction: row;
    list-style: none;
    margin: 0;
    padding: 0;

    .filler {
        flex: 1;
    }
}

.statusbar {
    display: flex;
    flex-direction: row;
    list-style: none;
    margin: 0;
    padding: 0;

    .filler {
        flex: 1;
    }
}

.data-admin.with-border {
    --border-style: solid;
    --border-width: 1px;
    --border-color: gray;

    border-style: var(--border-style);
    border-width: var(--border-width);
    border-color: var(--border-color);

    // border-top-style: var(--border-style);
    // border-top-width: var(--border-width);
    // border-top-color: var(--border-color);

    .helppane {
        border-left-style: var(--border-style);
        border-left-width: var(--border-width);
        border-left-color: var(--border-color);

        >*:not(:first-child) {
            border-top-style: var(--border-style);
            border-top-width: var(--border-width);
            border-top-color: var(--border-color);
        }
    }

    // .toolbar.top {
    //     border-bottom-style: var(--border-style);
    //     border-bottom-width: var(--border-width);
    //     border-bottom-color: var(--border-color);
    // }

    // .toolbar.bottom {
    //     border-top-style: var(--border-style);
    //     border-top-width: var(--border-width);
    //     border-top-color: var(--border-color);
    // }

    .statusbar {
        border-top-style: var(--border-style);
        border-top-width: var(--border-width);
        border-top-color: var(--border-color);
    }
}
</style>