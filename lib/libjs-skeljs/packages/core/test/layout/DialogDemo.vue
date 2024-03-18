<script lang="ts">
import { computed, ref } from 'vue';

export const title = 'dialog demo';
</script>

<script setup lang="ts">
import Dialog from '../src/ui/Dialog.vue';

const modal = ref();
const nonModal = ref();

const blink = ref(true);
</script>

<template>
    <div>
        Various dialogs:
        <ul>
            <li>
                <span @click="modal.open()">
                    modal dialog
                </span>
                <input type="checkbox" v-model="blink"> with blinking disabler
            </li>

            <li @click="nonModal.open()">
                non-modal centered
            </li>
        </ul>
    </div>

    <Dialog ref="modal" :modal="blink ? 'blink' : '1'" title="Model Dialog" @closed="console.log('closed')">
        sample content.
    </Dialog>

    <Dialog ref="nonModal" title=" Non-modal Dialog">
        A very long text typically refers to a piece of writing that is extensive in length. It could be a book, an article,
        a document, or any other form of written content that contains a substantial amount of information. The term "very
        long text" is subjective and can vary depending on the context.

        When dealing with a very long text, it's important to consider various factors such as the purpose, subject matter,
        and intended audience. Here are a few tips to handle and navigate through a very long text effectively:

        <ol>
            <li> Skim and scan: Start by quickly skimming through the text to get a general idea of its structure, headings,
                and subheadings. This will help you identify key sections or chapters that are most relevant to your needs.
            </li>
            <li> Use the table of contents/index: If available, refer to the table of contents or index to locate specific
                topics or sections within the text. This will save you time by directly jumping to the relevant parts. </li>
            <li> Take notes: While reading, jot down important points or key ideas that you come across. This will help you
                retain information and make it easier to refer back to later.
            </li>
            <li> Break it down: If the text is particularly lengthy, consider breaking it down into smaller, manageable
                sections. Set goals for yourself, such as reading a certain number of pages or chapters each day, to make
                the task more achievable. </li>
            <li> Use digital tools: If you're reading an electronic version of the text, take advantage of digital tools
                such as search functions, highlighting, and bookmarking to quickly find and reference information. </li>
            <li> Seek summaries or reviews: If available, look for summaries or reviews of the text online. These can
                provide you with an overview or analysis of the content, helping you grasp the main points without having to
                read the entire text.
            </li>
        </ol>
        Remember, reading a very long text can be time-consuming, so prioritize your reading based on your specific needs
        and interests.
    </Dialog>
</template>

<style lang="scss" scoped></style>
