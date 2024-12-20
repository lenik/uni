<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { IdEntity } from "../../net/bodz/lily/concrete/IdEntity";

import Icon from "@skel01/core/src/ui/Icon.vue";
// import Dialog from "@skel01/core/src/ui/Dialog.vue";

const model = defineModel<IdEntity<any>>();
const id = defineModel("id");

interface Props {
    dialog?: any
    chooseButton?: boolean
    removeButton?: boolean
    rightRemove?: boolean
}

const props = withDefaults(defineProps<Props>(), {
    chooseButton: false,
    removeButton: true,
    rightRemove: true,
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();
const unspecified = computed(() => justNulls(model.value));
const specified = computed(() => !unspecified.value);

const hasId = computed(() => model.value?.id != null || id.value != null);
const hasLabel = computed(() => model.value?.label != null);

function justNulls(o) {
    if (o == null)
        return true;
    if (typeof o != 'object')
        return false;
    return o.id == null && o.label == null;
}

// methods

defineExpose({ update });

function update() {
}

function remove() {
    model.value = undefined;
    id.value = undefined;
}

function openDialog() {
    props.dialog?.open(onDialogSelect);
}

function onDialogSelect(val: any) {
    model.value = val;
    if (val == undefined)
        id.value = undefined;
    else
        id.value = val.id;
    return true;
}

onMounted(() => {
});
</script>

<template>
    <span class="ref-editor" ref="rootElement" :class="{ specified, unspecified, hasId, hasLabel }">
        <input type="hidden" :value="model?.id || id">
        <div class="info" @click="openDialog" @contextmenu.prevent="rightRemove && remove()">
            <template v-if="specified">
                <slot>
                    <span class="id" v-if="hasId"> {{ model?.id || id }} </span>
                    <span class="label" v-if="hasLabel">{{ model?.label }}</span>
                </slot>
            </template>
            <template v-else>
                <slot name="null">
                    <Icon name="far-ban" />
                    <span> Unspecified</span>
                </slot>
            </template>
        </div>
        <div class="btn" @click="remove" v-if="specified && removeButton">
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

        .id {
            display: inline-block;
            font-style: italic;
            transform: scaleX(70%);
            margin: 0 -10%;
            color: hsl(190, 40%, 50%);
        }

        .id+.label {
            &::before {
                content: " / ";
                color: #bbb;
            }
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
