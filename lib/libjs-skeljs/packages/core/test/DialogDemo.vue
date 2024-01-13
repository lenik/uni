<script setup lang="ts">

import { ref } from 'vue';
import Dialog from '../src/ui/Dialog.vue';

const modal = ref();
const nonModal = ref();

</script>

<template>
    <div>
        Various dialogs:
        <ul>
            <li @click="modal.open()">modal</li>
            <li @click="nonModal.open()">centered</li>
        </ul>
    </div>

    <Dialog ref="modal" modal title="Model Dialog" @closed="console.log('closed')">
        sample content.
    </Dialog>

    <Dialog ref="nonModal" title=" Non-modal Dialog">
        sample content.
    </Dialog>
</template>

<style lang="scss" scoped></style>
