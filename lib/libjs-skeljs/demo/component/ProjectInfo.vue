<script setup lang="ts">

defineProps<{
    stat: any
}>();

</script>

<template>
    <div class="overflow-auto">
        <div class="float-left">
            <h2 class="editable">Project</h2>
            <div class="editable subtitle">A small description of the project.</div>
        </div>
        <div k="stats">
            <ul class="inline-block">
                <li class="counter-pair">
                    <span class="name"><i class="fa fa-envelope-open-o"></i>View</span>
                    <span class="val">{{ stat.views }}</span>
                </li>
                <li class="counter-pair">
                    <span class="name"><i class="fa fa-star"></i>Star</span>
                    <span class="val">{{ stat.stars }}</span>
                </li>
                <li class="counter-pair">
                    <span class="name"><i class="fa fa-thumbs-o-up"></i>Vote</span>
                    <span class="val">{{ stat.upvotes }}/{{ stat.downvotes }}</span>
                </li>
            </ul>
        </div>
    </div>
</template>