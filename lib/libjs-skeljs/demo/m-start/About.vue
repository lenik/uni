<script setup lang="ts">

defineProps<{
    version: string
}>();

</script>

<template>
    <div>
        <h2>About ProjectTitle {{ version }}</h2>

        <h3>TODO</h3>
        <ol>
            <li></li>
            <li></li>
            <li></li>
        </ol>
    </div>
</template>