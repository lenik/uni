<script lang="ts">
import type { CounterTypes, CounterValues } from '../misc/Counters';
import Counters from '../misc/Counters.vue';

export const title = 'Project Info';

export interface Props {
    label?: string
    description?: string
    counterTypes?: CounterTypes
    counters?: CounterValues
}

const defaultCounterTypes: CounterTypes = {
    views: {
        priority: 1,
        label: 'Views',
        icon: 'far-eye',
    },
    stars: {
        priority: 2,
        label: 'Stars',
        icon: 'fa-star',
    },
    votes: {
        priority: 3,
        label: 'Votes',
        icon: 'far-thumbs-o-up',
        cformat: (vals: CounterValues) => {
            if (vals.upVotes == undefined || vals.downVotes == undefined)
                return undefined;
            else
                return vals.upVotes + '/' + vals.downVotes;
        }
    }
};
</script>

<script setup lang="ts">
const props = withDefaults(defineProps<Props>(), {
    label: 'Project',
    description: 'A small description of the project.',
    counterTypes: defaultCounterTypes,
    counters: {
        views: 0,
        stars: 0,
    }
});

</script>

<template>
    <div class="project-info overflow-auto">
        <div class="float-left">
            <h2 class="editable">{{ label }}</h2>
            <slot name="description">
                <div class="editable subtitle">{{ description }}</div>
            </slot>
        </div>
        <div k="stats">
            <Counters :types="counterTypes" :values="counters"></Counters>
        </div>
        <slot></slot>
    </div>
</template>

<style lang="scss" scoped>
.project-info {
    overflow: hidden;
    background-color: hsl(180, 30%, 80%, 10%);
    position: relative;
    padding: 1em 2em .5em;

    h2 {
        margin: 0;
        font-weight: 400;
    }

    .subtitle {
        // font-style: italic;
        margin-right: 12em;
        font-weight: 300;
    }

    [k=stats] {
        position: absolute;
        top: 1em;
        right: 2em;

        ul {
            display: flex;
            align-items: flex-start;
            margin: 0;
        }
    }
}
</style>