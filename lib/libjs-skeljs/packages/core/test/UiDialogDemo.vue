<script setup lang="ts">

import 'jquery-ui-themes/themes/le-frog/jquery-ui.css';

import { ref } from 'vue';
import UiDialog from '../src/ui/UiDialog.vue';

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

    <UiDialog ref="modal" modal title="Model UiDialog" @closed="console.log('closed')">
        sample content.
    </UiDialog>

    <UiDialog ref="nonModal" title=" Non-modal UiDialog">
        sample content.
    </UiDialog>
</template>

<style lang="scss" scoped></style>
