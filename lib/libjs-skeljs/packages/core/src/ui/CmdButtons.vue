<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { Command, CommandGroup, group } from "./types";

import CmdButton from './CmdButton.vue';

const model = defineModel();

type CmdRunnerFunc = (cmd: Command, event: Event, target?: HTMLElement) => void;

interface Props {
    src: Command[]

    includeGroups?: string[]  // include only these groups
    excludeGroups?: string[] // exclude these groups

    includes?: string[] // include only these commands by name
    excludes?: string[] // include only these commands by name

    pos?: 'left' | 'right'  //  include if the position matches
    vpos?: 'top' | 'bottom' // include if the vertical position matches

    runner?: CmdRunnerFunc
    target?: HTMLElement
}

const props = withDefaults(defineProps<Props>(), {
});

type NameSet = { [k: string]: boolean };
type OptNameSet = NameSet | undefined;

function toSet(array: string[] | undefined): OptNameSet {
    if (array == null) return array;
    let set: NameSet = {};
    array.map(item => set[item] = true);
    return set;
}

const includeGroupSet = computed(() => toSet(props.includeGroups));
const excludeGroupSet = computed(() => toSet(props.excludeGroups));
const includeSet = computed(() => toSet(props.includes));
const excludeSet = computed(() => toSet(props.excludes));

const filtered = computed(() => {
    let list = props.src.filter(cmd => {
        if (includeSet.value != null)
            if (!includeSet.value[cmd.name])
                return false;
        if (excludeSet.value != null)
            if (excludeSet.value[cmd.name])
                return false;

        let g = cmd.group || 'default';
        if (includeGroupSet.value != null)
            if (!includeGroupSet.value[g])
                return false;
        if (excludeGroupSet.value != null)
            if (excludeGroupSet.value[g])
                return false;

        if (props.pos != null)
            if (cmd.pos != props.pos)
                return false;
        if (props.vpos != null)
            if (cmd.vPos != props.vpos)
                return false;

        return true;
    });

    return list;
});

const groupMap = computed(() => {
    let filteredList = filtered.value;
    return group(filteredList);
});

const groupNames = computed(() => {
    let groups = Object.values(groupMap.value);
    groups.sort((a: CommandGroup, b: CommandGroup) => {
        if (a.ordinalMin != b.ordinalMin) {
            if (a.ordinalMin == null)
                return 1;
            if (b.ordinalMin == null)
                return -1;
            return a.ordinalMin - b.ordinalMin;
        }
        if (a.name < b.name)
            return -1;
        if (a.name > b.name)
            return 1;
        return 0;
    });
    let names = groups.map(g => g.name);
    return names;
});

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
    } else {
        if (cmd.run != null)
            cmd.run(event);

        if (cmd.action != null)
            throw "don't know how to handle action " + cmd.action + ".";

        if (cmd.href != null)
            location.href = cmd.href;
    }
}

defineExpose();

</script>

<template>
    <ul class="cmd-buttons" ref="rootElement">
        <template v-for="(groupName) in groupNames" :key="groupName">
            <CmdButton tag-name="li" v-for="cmd in groupMap[groupName].commands" :key="cmd.name" :cmd="cmd" :target="target"
                @click="(event) => runCmd(cmd, event)" v-bind="$attrs">
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
