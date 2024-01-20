<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { Command, UiGroup, group, select } from "./types";

import CmdButton from './CmdButton.vue';

const model = defineModel();

type CmdRunnerFunc = (cmd: Command, event: Event, target?: HTMLElement) => void;

interface Props {
    src: Command[]

    includeGroups?: string[]  // include only these groups
    excludeGroups?: string[] // exclude these groups

    includes?: string[] // include only these commands by name
    excludes?: string[] // include only these commands by name

    pos?: 'left' | 'right' | 'left?' | 'right?'  //  include if the position matches
    vpos?: 'top' | 'bottom' | 'top?' | 'bottom?' // include if the vertical position matches

    runner?: CmdRunnerFunc
    target?: HTMLElement
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

function runCmd(cmd: Command, event: Event) {
    if (props.runner != null) {
        props.runner(cmd, event, props.target);
    }
}

defineExpose();

</script>

<template>
    <ul class="cmd-buttons" ref="rootElement">
        <template v-for="(groupName) in groupNames" :key="groupName">
            <CmdButton tag-name="li" v-for="cmd in groupMap.get(groupName).items" :key="cmd.name" :cmd="cmd"
                :target="target" @click="(event) => runCmd(cmd, event)" v-bind="$attrs">
            </CmdButton>
            <li class="sep" />
        </template>
    </ul>
</template>

<style scoped lang="scss">
.cmd-buttons {
    display: flex;
    flex-direction: row;
    margin: 0;
    padding: 0;
    list-style: none;
    align-items: center;
}

.sep {
    width: 1em;
    text-align: center;
    vertical-align: middle;
    align-self: normal;
    position: relative;

    &:before {
        display: inline-block;
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
