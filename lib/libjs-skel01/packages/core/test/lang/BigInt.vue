<script lang="ts">
import { computed, onMounted, ref } from "vue";
import { BigInteger } from "../../src/lang/basetype";
import { BIG_INTEGER } from "../../src/lang/baseinfo";

export interface Props {
}
</script>

<script setup lang="ts">
const model = defineModel();

const props = withDefaults(defineProps<Props>(), {
});

const emit = defineEmits<{
    error: [message: string]
}>();

// property shortcuts

const rootElement = ref<HTMLElement>();

const a = ref<BigInteger>(BigInt(1000));
const b = ref<BigInteger>(BigInt(47));

const aStr = computed({
    get() {
        return String(a.value);
    },
    set(val: string) {
        a.value = BigInt(val);
    }
});
const bStr = computed({
    get() {
        return String(b.value);
    },
    set(val: string) {
        b.value = BigInt(val);
    }
});

// methods

defineExpose({ update });

function update() {
}

function format(n: BigInteger) {
    return BIG_INTEGER.format(n);
}

onMounted(() => {
});
</script>

<template>
    <div class="component-root" ref="rootElement">
        <h3>BigInt Divide</h3>
        <div> A: <input type="text" v-model="aStr"> {{ a }} </div>
        <div> B: <input type="text" v-model="bStr"> {{ b }} </div>
        <div v-if="b != 0"> A/B: {{ format(a / b) }} </div>
    </div>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}
</style>
