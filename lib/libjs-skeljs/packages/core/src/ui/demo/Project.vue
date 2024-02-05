<script lang="ts">
import { onMounted, ref } from "vue";

import Icon from '../Icon.vue';
import Card from './Card.vue';

export interface Props {
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

// property shortcuts

const rootElement = ref<HTMLElement>();

// methods

defineExpose({ update });

function update() {
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <h3>The project is a composite of these components:</h3>
        <ul>
            <li>
                <Card label="uni tools" icon="fab-linux" href="https://github.com/lenik/uni" version="62b6e37a6">
                    <p> Toolset for *nix-alike OS. </p>
                    <p> Debian is a popular Linux distribution known for its stability and large software repository. It
                        provides a vast selection of tools for various purposes, including system administration,
                        development, networking, security, and more. These tools are typically open-source and can be
                        installed using Debian's package management system. </p>
                </Card>
            </li>
            <li>
                <Card label="@skeljs/core" icon="fab-node-js"
                    href="https://github.com/lenik/uni/tree/master/lib/libjs-skeljs" version="62b6e37a6">
                    <p>Originally called the skel project, which is a quickstart web template.</p>
                    <p>It's entirely refactored as ES modules in TypeScript language. </p>
                    <p>A Node.js module is a reusable piece of code that encapsulates related functionality. It can be a
                        single file or a directory containing multiple files. Modules help in organizing code and making it
                        more maintainable and scalable. </p>
                </Card>
            </li>
            <li>
                <Card label="Jazz/Lily Framework" icon="fab-java" href="https://github.com/lenik/jazz" version="62b6e37a6">
                    <p>Jazz is a Java framework for applications of various types.</p>
                    <p>Lily is a Jazz-based database/web framework for enterprise applications. It mainly provides admin
                        service and a consistent API for heterogeneous backend components. </p>
                    <p>Application server administration consoles: Most Java EE application servers come with administration
                        consoles that provide a web-based interface for managing and configuring enterprise services. These
                        consoles typically offer features for deploying applications, managing resources, configuring
                        security settings, and monitoring performance. </p>
                </Card>
            </li>
        </ul>
    </div>
</template>

<style scoped lang="scss">
ul,
ol {
    list-style: none;
    margin: 0;
    padding: 0;
}

h3 {
    font-weight: 300;
}

ul {
    display: flex;
    flex-direction: row;
    margin: 0 1em;

    li {
        flex: 1;
        display: flex;
        flex-direction: column;

        >* {
            flex: 1;
        }

        &:nth-child(even) .card {
            background-color: hsl(0, 30%, 90%);
        }
    }
}
</style>
