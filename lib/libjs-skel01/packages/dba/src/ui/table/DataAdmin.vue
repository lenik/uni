<script lang="ts">
import { computed, onMounted, ref } from "vue";
import moment from "moment-timezone";

import type { Command, Status } from "skel01-core/src/ui/types";
import type { LogEntry } from "skel01-core/src/logging/api";
import { logsExample, parseException } from "skel01-core/src/logging/api";

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
import CmdButtons from 'skel01-core/src/ui/CmdButtons.vue';
import Dialog from 'skel01-core/src/ui/Dialog.vue';
import Detachable from 'skel01-core/src/ui/layout/Detachable.vue';
import StatusPanels from 'skel01-core/src/ui/StatusPanels.vue';
import LogMonitor from 'skel01-core/src/ui/LogMonitor.vue';
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
                    <LogMonitor :src="logs as any" />
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
@use 'DataAdmin.scss';
</style>