<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { IdEntity } from "../../ui/table/types";

import Icon from "@skeljs/core/src/ui/Icon.vue";
import Dialog from "@skeljs/core/src/ui/Dialog.vue";

const model = defineModel<IdEntity<any>>();
const id = defineModel("id");

type InfoFunc = (o: any) => string;

interface Props {
    info?: InfoFunc
    dialog?: InstanceType<typeof Dialog>
    chooseButton?: boolean
    removeButton?: boolean
    rightRemove?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    info: str_idLabel,
    chooseButton: false,
    removeButton: true,
    rightRemove: true,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const specified = computed(() => model.value != null);
const unspecified = computed(() => model.value == null);

// methods

defineExpose({ update });

function update() {
}

function remove() {
    model.value = undefined;
    id.value = undefined;
}
function remove2(event: Event) {
    if (props.rightRemove) {
        remove();
        event.preventDefault();
    }
}
function openDialog() {
    props.dialog?.open((val: any) => {
        model.value = val;
        if (val == undefined)
            id.value = undefined;
        else
            id.value = val;
        return true;
    });
}

onMounted(() => {
});
</script>

<script lang="ts">
export function str_idLabel(o) {
    if (o == null)
        return "null";
    else
        return o.id + " - " + o.label;
}
</script>

<template>
    <span class="ref-editor" ref="rootElement" :class="{ specified, unspecified }">
        <input type="hidden" :value="model?.id || id">
        <div class="info" @click="openDialog" @contextmenu="(e) => remove2(e)">
            <template v-if="model != null">
                <slot>
                    <span> {{ info(model) }} </span>
                </slot>
            </template>
            <template v-else>
                <slot name="null"><span> Unspecified</span> </slot>
            </template>
        </div>
        <div class="btn" @click="remove" v-if="model != null && removeButton">
            <Icon class="normal" name="far-times-circle" />
            <Icon class="highlight" name="fa-times-circle" />
        </div>
        <div class="btn" @click="openDialog" v-if="chooseButton">
            <Icon class="normal" name="far-arrow-alt-circle-right" />
            <Icon class="highlight" name="fas-arrow-alt-circle-right" />
        </div>
    </span>
</template>

<style scoped lang="scss">
.ref-editor {
    display: inline-flex;
    flex-direction: row;
    align-items: center;

    .info {
        border: solid 1px hsl(190, 30%, 45%);
        border-radius: .5rem;
        font-size: 80%;
        transform: scaleY(0.8);

        padding: .1rem .5rem;
        cursor: pointer;
        user-select: none;
        font-weight: 300;

        color: hsl(190, 30%, 30%);
        background-color: hsl(190, 30%, 92%);

        &:hover {
            background-color: hsl(140, 40%, 92%);
        }
    }

    &.unspecified .info {
        border-color: #888;
        color: #666;
        background-color: #eee;
        font-style: italic;
    }

    .btn {
        margin-left: 1em;
        font-size: 80%;
        color: hsl(220, 30%, 60%);
        user-select: none;
        cursor: pointer;

        &:hover .normal {
            display: none;
        }

        &:not(:hover) .highlight {
            display: none;
        }
    }

}
</style>
