<script setup lang="ts">

import $ from 'jquery';
import { computed, onMounted, ref } from "vue";
import { simpleName } from "@skeljs/core/src/logging/api";
import { DialogSelectCallback } from '@skeljs/core/src/ui/types';
import { Selection } from '../table/types';
import { EntityType } from '../../lily/entity';

import Dialog from '@skeljs/core/src/ui/Dialog.vue';
import DataTable from '../table/DataTable.vue';

const model = defineModel();

interface Props {
    modal?: boolean | string
    type: EntityType
    lilyUrl?: string
    serverUrl?: string
}

const props = withDefaults(defineProps<Props>(), {
    modal: true,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcts

const dialogComp = ref<undefined | InstanceType<typeof Dialog>>();
const dialogTitle = computed(() => "Choose " + props.type.label);
const dialogName = computed(() => simpleName(props.type.name) + "Chooser");

const lilyUrl = computed(() => {
    if (props.lilyUrl != null)
        return props.lilyUrl;
    else if (props.serverUrl != null)
        return props.serverUrl + "/" + props.type.simpleName;
    else
        throw "either lily-url or server-url have to be specified."
});

const dataTableComp = ref();
const dataTableApi = computed(() => dataTableComp.value?.api);

const selection = ref();

defineExpose({
    open
});

function open(callback?: DialogSelectCallback) {
    dialogComp.value?.open(callback);
}

function onselect(sel: Selection, e: Event) {
    selection.value = sel.toObject();
}

onMounted(() => {
    let root = dialogComp.value!.rootElement;
    root = $('.dialog', root)[0];

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
    <Dialog ref="dialogComp" class="choose-dialog" :name="dialogName" v-model="selection" :modal="modal"
        :title="dialogTitle" height="25em" :cmds="{ ok: true, cancel: true }">
        <DataTable ref="dataTableComp" :lilyUrl="lilyUrl" @select="onselect">
            <slot></slot>
        </DataTable>
    </Dialog>
</template>

<style scoped lang="scss">
.choose-dialog {
    padding: 0;
}

::v-deep(.dialog) {

    >.body>.content {
        display: flex;
        flex-direction: column;
        padding: 0;
    }

    .dt-container {
        flex: 1;

        // display: flex;
        // flex-direction: column;

        >.dataTables_wrapper {
            margin: 1em;
            // box-sizing: border-box;
            //     flex: 1;
        }
    }
}
</style>
