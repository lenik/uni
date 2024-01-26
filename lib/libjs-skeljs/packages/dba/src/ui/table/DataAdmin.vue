<script setup lang="ts">

import $ from "jquery";

import { ref, onMounted, computed } from "vue";

import { showError } from "@skeljs/core/src/logging/api";

import type { DataTab, SymbolCompileFunc } from "./types";
import { EntityType } from "./types";

import formats from "./formats";
import { useAjaxDataTable, useDataTable } from "./apply";
import { objv2Tab } from "./objconv";

import DataTable from './DataTable.vue';
import CmdButtons from '@skeljs/core/src/ui/CmdButtons.vue';
import StatusPanels from '@skeljs/core/src/ui/StatusPanels.vue';
import Detachable from '@skeljs/core/src/ui/layout/Detachable.vue';
import Icon from '@skeljs/core/src/ui/Icon.vue';
import Dialog from '@skeljs/core/src/ui/Dialog.vue';

import { Command, dialogCmds, Status } from "@skeljs/core/src/ui/types";

const instanceModel = defineModel<any>('instance');

interface Props {
    type?: EntityType
    tabindex?: number

    tools: Command[]
    statuses: Status[]
    previewDetached?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    previewDetached: false,
    tabindex: 0,
});

defineOptions({
    inheritAttrs: false
})

const rootElement = ref<HTMLElement>();
const dataTable = ref<InstanceType<typeof DataTable>>();

const editor = ref<InstanceType<typeof Dialog>>();
const editorCommands = ref(Object.values(dialogCmds));
const editorTitle = computed(() => {
    return "Edit " + props.type.label;
});

function deleteSelection() {
    let dt = dataTable.value!;
    dt.deleteSelection();
}

defineExpose({ rootElement, editor, dataTable, deleteSelection });

</script>

<template>
    <div ref="rootElement" class="data-admin with-border" :tabindex="tabindex">
        <div class="table-tooling">
            <div class="workpane">
                <ul class="toolbar top">
                    <CmdButtons :src="tools" class="left" vpos="top?" pos="left?" />
                    <li class="filler"></li>
                    <CmdButtons :src="tools" class="right" vpos="top?" pos="right" />
                </ul>
                <div class="table">
                    <slot name="table">
                        <DataTable ref="dataTable" v-bind="$attrs">
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
            <div class="sidepane">
                <Detachable class="preview" title="Preview" :detached="previewDetached" width="20em">
                    <slot name="preview">
                    </slot>
                </Detachable>
                <div class="side-tools">
                    <slot name="side-tools">
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
        <Dialog ref="editor" v-model="instanceModel" modal="true" :title="editorTitle" :buttons="editorCommands">
            <slot name="editor"></slot>
        </Dialog>
        <div class="templates">
            <div class="processing">
                <div class='fa-stack fa-lg'>
                    <i class='fa fa-spinner fa-spin fa-stack-2x fa-fw'></i>
                </div> Processing ...
            </div>
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
    overflow: hidden;

    &:focus-visible {
        outline: none;
    }
}

.table-tooling {
    flex: 1;
    display: flex;
    flex-direction: row;
    overflow: hidden;
}

.workpane {
    flex: 1;
    display: flex;
    flex-direction: column;

    .table {
        flex: 1;
        margin: .5em 0;
        overflow: auto;
        display: flex;
        flex-direction: column;

        >.dt-container {
            flex: 1;
        }
    }
}

.sidepane {
    position: relative;
    display: flex;
    flex-direction: column;
}

.preview.attached {
    flex: 1;
}

.side-tools {
    padding: .5em;
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
    box-sizing: border-box;
    overflow: hidden;

    >.status-panels:first-child {
        overflow: hidden;
    }
}

.filler {
    flex: 1;
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

    .sidepane {
        border-left-style: var(--border-style);
        border-left-width: var(--border-width);
        border-left-color: var(--border-color);

        >:not(.detached):not(:first-child) {
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

.data-admin {
    ::v-deep(.dataTable) {
        tr.new {
            background: hsl(60, 80%, 80%);
        }

        tr.dirty {
            background: hsl(0, 80%, 80%);
        }
    }
}

.templates {
    display: none;
}

::v-deep(.dataTables_processing) {
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
    box-sizing: border-box;

    .processing {
        display: inline-block;
        border-style: solid;
        border-width: 2px;
        border-color: hsl(330, 30%, 70%, 90%);
        background: hsla(330, 30%, 92%, 90%);
        padding: 1em 2em;
        border-radius: 1em;
        box-shadow: 3px 3px 10px 0px gray;
        position: relative;
        top: 50%;
        transform: translateY(-50%);
        color: hsl(330, 30%, 30%, 100%);
        opacity: 85%;
        font-style: italic;
    }

    .processing+* {
        display: none;
    }
}
</style>