<script lang="ts">
import { computed, onMounted, ref } from "vue";
import moment from "moment-timezone";

import { Command, Status } from "@skeljs/core/src/ui/types";
import { LogEntry, logsExample, parseException } from "@skeljs/core/src/logging/api";

import EntityType from "../../net/bodz/lily/entity/EntityType";
import { } from "./types";

export interface Props {
    type?: EntityType
    tabindex?: number

    tools: Command[]
    statuses: Status[]
    previewDetached?: boolean
    logsDetached?: boolean
}
</script>

<script setup lang="ts">
import CmdButtons from '@skeljs/core/src/ui/CmdButtons.vue';
import Dialog from '@skeljs/core/src/ui/Dialog.vue';
import Detachable from '@skeljs/core/src/ui/layout/Detachable.vue';
import StatusPanels from '@skeljs/core/src/ui/StatusPanels.vue';
import LogMonitor from '@skeljs/core/src/ui/LogMonitor.vue';
import DataTable from './DataTable.vue';

defineOptions({
    inheritAttrs: false
})

const instanceModel = defineModel<any>('instance');

const props = withDefaults(defineProps<Props>(), {
    previewDetached: false,
    logsDetached: false,
    tabindex: 0,
});

// computed properties

const editorTitle = computed(() => {
    return "Edit " + props.type?.label;
});
const editorDialogName = computed(() => {
    if (props.type != null) {
        let typeName = props.type.name;
        let lastDot = typeName.lastIndexOf('.');
        let simpleName = lastDot == -1 ? typeName : typeName.substring(lastDot + 1);
        return simpleName + "Editor";
    } else {
        return "ObjectEditor";
    }
});

// DOM reference

const rootElement = ref<HTMLElement>();
const dataTableComp = ref<undefined | InstanceType<typeof DataTable>>();
const dataTableApi = computed(() => dataTableComp.value?.api);
const editorDialog = ref<undefined | InstanceType<typeof Dialog>>();

const logs = ref<LogEntry[]>(logsExample);

defineExpose({
    rootElement, dataTableComp, editorDialog, //
    deleteSelection
});

function deleteSelection() {
    let api = dataTableApi.value as any;
    if (api == null) return;
    let before = api.rowNumInfo()!;

    let dtComp = dataTableComp.value!;
    dtComp.deleteSelection();

    let pos = before.pos || 0;
    let after = (api as any).rowNumInfo()!;
    dtComp.api?.row(after.nodes[pos]).select();
}

onMounted(() => {
    let root = rootElement.value!;

    root.addEventListener('keydown', (e: any) => {
        if (e.target != root) return false;
        let api = dataTableApi.value as any;
        switch (e.key) {
            case 'ArrowUp':
            case 'ArrowDown':
            case 'Home':
            case 'End':
                if (api != null) {
                    let info = api.rowNumInfo()!;
                    let current = info.pos;
                    let next = 0;
                    if (current != null) {
                        switch (e.key) {
                            case 'ArrowUp': next = current - 1; break;
                            case 'ArrowDown': next = current + 1; break;
                            case 'Home': next = 0; break;
                            case 'End': next = info.n - 1; break;
                        }
                        // next = (next + info.n) % info.n;
                        if (next < 0) next = 0;
                        if (next >= info.n) next = info.n - 1;

                        api.row(info.nodes[current]).deselect();
                    }
                    api.row(info.nodes[next]).select();
                }
                break;

            case 'PageUp':
            case 'PageDown':
                if (api != null) {
                    let info = api.rowNumInfo()!;
                    let current = info.pos;
                    api.page(e.key == 'PageUp' ? 'previous' : 'next').draw(false);
                    info = api.rowNumInfo()!;
                    let pos = Math.min(current || 0, info.nodes.length - 1);
                    api.row(info.nodes[pos]).select();
                }
                break;
        }
    }, true);

});

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
                        <DataTable ref="dataTableComp" v-bind="$attrs">
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
                <Detachable class="logs" title="Logs" :detached="logsDetached" width="20em">
                    <LogMonitor :src="logs" />
                    <div style="overflow-anchor: auto;" />
                </Detachable>
            </div>
        </div>
        <ul class="statusbar">
            <slot name="statusbar">
                <StatusPanels :src="statuses" vpos="bottom?" pos="left" />
                <li class="filler"></li>
                <StatusPanels :src="statuses" vpos="bottom?" pos="right?" />
            </slot>
        </ul>
        <Dialog ref="editorDialog" :name="editorDialogName" v-model="instanceModel" modal="true" :title="editorTitle"
            :cmds="{ ok: true, cancel: true }">
            <slot name="editor"></slot>
        </Dialog>
        <div class="templates">
            <div class="processing">
                <div class='fa-stack fa-lg'>
                    <i class='fa fa-spinner fa-spin fa-stack-2x fa-fw'></i>
                </div> Loading ...
            </div>
            <Dialog id="waitbox" modal="true" :buttons="[]">
                <div class="flex-row">
                    <div class="content">
                        <i class="fa fa-circle-o-notch fa-spin"></i>
                        <span class="text">Wait...</span>
                    </div>
                </div>
            </Dialog>
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
    box-sizing: border-box;
    width: 100%;
}

.workpane {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

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
    // overflow: hidden;
    max-width: 30%;
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
    >* {
        display: none;
    }
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

.workpane {
    padding: 1em;
    // background: #fef;

    --color1: #ddd3;
    --color2: #fef3;
    --width: 1px;
    --scale: 1;
    background: repeating-linear-gradient(-45deg,
            var(--color1),
            var(--color1) calc(var(--width) * var(--scale)),
            var(--color2) calc(var(--width) * var(--scale)),
            var(--color2) calc(10px * var(--scale)));
}

.logs {
    font-size: 70%;

    position: relative;
    overflow: hidden;

    &.attached {
        max-height: 8em;
    }

    ::v-deep() {
        >.header {
            background-color: hsl(160, 20%, 65%);
        }

        >.content {
            --vskip: .1em;

            padding: calc(var(--vskip)/2) .1em;
            overflow: auto;
            overscroll-behavior-y: contain;
            scroll-snap-type: y proximity;

            >ul>li {
                padding: calc(var(--vskip)/2) 0;

                &:last-child {
                    scroll-snap-align: end;
                }
            }
        }
    }
}

#waitbox {

    cursor: wait;
    --hue: 150;
    --sat: 50%;

    &::v-deep() {
        .disabler {
            // background-color: hsla(200, 30%, 20%, 30%);

            animation: bgloop 1s infinite alternate ease-in-out;

            @at-root {
                @keyframes bgloop {
                    0% {
                        background: hsla(200, 30%, 20%, 25%);
                    }

                    100% {
                        background: hsla(200, 30%, 20%, 18%);
                    }
                }
            }
        }

        .dialog {
            background: hsla(var(--hue), var(--sat), 90%, 85%);
            border-color: hsla(var(--hue), var(--sat), 30%, 85%);
            border-radius: 1em;
        }

        .content {
            padding: .5em .8em;
            text-align: center;
            font-size: 150%;
            font-family: Sans;
            font-weight: bold;
            color: hsla(var(--hue), var(--sat), 30%);

            .text {
                margin-left: .5em;
            }
        }
    }


}
</style>