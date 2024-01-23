<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { group, select, Status } from "./types";

import StatusPanel from './StatusPanel.vue';

const model = defineModel();

interface Props {

    src: Status[]

    includeGroups?: string[]  // include only these groups
    excludeGroups?: string[] // exclude these groups

    includes?: string[] // include only these commands by name
    excludes?: string[] // include only these commands by name

    pos?: 'left' | 'right' | 'left?' | 'right?'  //  include if the position matches
    vpos?: 'top' | 'bottom' | 'top?' | 'bottom?' // include if the vertical position matches

}

const props = withDefaults(defineProps<Props>(), {
});

type NameSet = { [k: string]: boolean };

function toSet(array: string[] | undefined) {
    if (array == null) return undefined;
    let set: NameSet = {};
    array.map(item => set[item] = true);
    return set;
}

const includeGroupSet = computed(() => toSet(props.includeGroups));
const excludeGroupSet = computed(() => toSet(props.excludeGroups));
const includeSet = computed(() => toSet(props.includes));
const excludeSet = computed(() => toSet(props.excludes));

const filtered = computed(() => select({
    includeGroupSet: includeGroupSet.value,
    excludeGroupSet: excludeGroupSet.value,
    includeSet: includeSet.value,
    excludeSet: excludeSet.value,
    pos: props.pos,
    vpos: props.vpos,
}, props.src));

const groupMap = computed(() => {
    let filteredList = filtered.value;
    return group(filteredList);
});

const groupNames = computed(() => groupMap.value.sortedNames);

interface Emits {
    (e: 'error', message: string): void
}

const emit = defineEmits<Emits>();

const rootElement = ref<HTMLElement>();
onMounted(() => {
});

defineExpose();

</script>

<template>
    <ul class="status-panels" ref="rootElement">
        <template v-for="(groupName) in groupNames" :key="groupName">
            <template v-for="(status, i) in groupMap.get(groupName).items" :key="status.name">
                <StatusPanel tag-name="li" :status="status" v-bind="$attrs" />
                <li class="item-sep" v-if="i != groupMap.get(groupName).items.length - 1" />
            </template>
            <li class="group-sep" />
        </template>
    </ul>
</template>

<style scoped lang="scss">
.status-panels {
    display: flex;
    flex-direction: row;
    margin: 0;
    padding: 0;
    list-style: none;
    align-items: center;
}

.item-sep {
    width: .5em;
    text-align: center;
    vertical-align: middle;
    align-self: normal;
    position: relative;

    &:before {
        display: block;
        position: relative;
        top: 50%;
        transform: translateY(-50%);
        height: 100%;
        border-left: solid 1px #aaa;
        content: ' ';
    }

    &:last-child {
        display: none;
    }

}

.group-sep {
    width: 1em;
    text-align: center;
    vertical-align: middle;
    align-self: normal;
    position: relative;

    &:before {
        display: block;
        position: relative;
        top: 50%;
        transform: translateY(-50%);
        height: 90%;
        border-left: solid 1px #aaa;
        content: ' ';
    }

    &:last-child {
        display: none;
    }
}
</style>
