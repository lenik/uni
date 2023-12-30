<script setup lang="ts">

import * as C from '../misc/Counters';
import Counters from '../misc/Counters.vue';

interface Props {
    label?: string
    description?: string
    counterTypes?: C.CounterTypes
    counters?: C.CounterValues
}

const props = withDefaults(defineProps<Props>(), {
    label: 'Project',
    description: 'A small description of the project.',
    counterTypes: {
        views: {
            priority: 1,
            label: 'Views',
            iconfa: 'envelope-open-o',
        },
        stars: {
            priority: 2,
            label: 'Stars',
            iconfa: 'star',
        },
        votes: {
            priority: 3,
            label: 'Votes',
            iconfa: 'thumbs-o-up',
            cformat: (vals: C.CounterValues) => {
                if (vals.upVotes == undefined || vals.downVotes == undefined)
                    return undefined;
                return vals.upVotes + '/' + vals.downVotes;
            }
        },
    },
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
            <slot>
                <div class="editable subtitle">{{ description }}</div>
            </slot>
        </div>
        <div k="stats">
            <Counters :types="counterTypes" :values="counters"></Counters>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.project-info {
    background: #fafaff;
    position: relative;
    padding: 1em 2em .5em;

    h2 {
        margin: 0;
    }

    .subtitle {
        font-style: italic;
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