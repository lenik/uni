<script lang="ts">
import 'jquery-ui-themes/themes/le-frog/jquery-ui.css';

import { computed, ref } from 'vue';

export const title = 'jquery-ui dialog demo';
</script>

<script setup lang="ts">
import UiDialog from '../src/ui/UiDialog.vue';

const modal = ref();
const nonModal = ref();

</script>

<template>
    <div>
        <p> Various dialogs: </p>
        <ul>
            <li @click="modal.open()">modal</li>
            <li @click="nonModal.open()">centered</li>
        </ul>
    </div>
    <UiDialog ref="modal" modal title="Model UiDialog" @closed="console.log('closed')">
        <p>sample content.</p>
    </UiDialog>
    <UiDialog ref="nonModal" title=" Non-modal UiDialog">
        <p>sample content.</p>
    </UiDialog>
</template>

<style lang="scss" scoped></style>
