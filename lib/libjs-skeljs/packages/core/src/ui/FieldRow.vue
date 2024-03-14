<script lang="ts">
export interface Props {
    tagName?: string

    property?: any

    icon?: string
    iconWidth?: string

    label?: string
    labelWidth?: string
    labelMaxLength?: number

    description?: string
    after?: 'label' | 'end'
    expandIcon?: string
    collapseIcon?: string

    tooltip?: string

    align?: 'top' | 'bottom' | 'baseline' | 'center' | 'middle'
    alignLabel?: string

    required?: boolean
    validator?: Validator
    showState?: boolean
    watch?: boolean
}
</script>

<script setup lang="ts">

import { computed, onMounted, ref, watch } from "vue";

import Icon from "./Icon.vue";
import type { ValidateResult, Validator } from "./types";

const model = defineModel();
const expanded = defineModel<boolean>('expanded');
const valid = defineModel<ValidateResult>('valid');

const props = withDefaults(defineProps<Props>(), {
    tagName: 'div',
    label: 'unnamed',
    iconWidth: '1rem',
    after: 'end',
    expandIcon: 'far-question-circle',
    collapseIcon: 'fas-question-circle',
    align: 'middle',
    alignLabel: 'left',
    showState: false,
    watch: true,
});

const emit = defineEmits<{
    'update:vaild': [val: ValidateResult]
    error: [result: ValidateResult, event?: Event]
    validated: [result: ValidateResult, event?: Event]
}>();

// property shortcuts

const childTagName = computed(() => {
    switch (props.tagName) {
        case 'div':
            return 'span';
        case 'ul':
        case 'ol':
            return 'li';
        case 'tr':
            return 'td';
        default:
            return 'span';
    }
});

const _label = computed(() => {
    if (props.property == null)
        return props.label;
    else
        return props.property!.display;
});

const _icon = computed(() => props.property?.icon || props.icon);
const _description = computed(() => props.property?.description || props.description);
const _tooltip = computed(() => props.property?.tooltip || props.tooltip);
const _validator = computed(() => props.property?.validator || props.validator);
const _required = computed(() => props.property?.required || props.required);

const autoLabelWidth = computed(() => {
    const defaultLabelWidth = '10em';
    const labelLengthLimit = 32;
    if (props.labelWidth != null)
        return props.labelWidth;

    if (props.labelMaxLength != null) {
        let len = Math.min(props.labelMaxLength, labelLengthLimit);
        return len + "em";
    }

    return defaultLabelWidth;
});

const labelSize = computed(() => {
    if (props.labelMaxLength != null)
        return props.labelMaxLength;
    else if (props.labelWidth != null)
        return parseInt(props.labelWidth.replace("rem", "").replace("em", ""));
    else
        return undefined;
});
const morebit = computed(() => {
    let size = labelSize.value;
    if (size == null) return false;
    let len = _label.value?.length || 0;
    return len > size && len < size * 1.5;
});
const morehalf = computed(() => {
    let size = labelSize.value;
    if (size == null) return false;
    let len = _label.value?.length || 0;
    return len >= size * 1.5 && len < size * 2;
});
const more2 = computed(() => {
    let size = labelSize.value;
    if (size == null) return false;
    let len = _label.value?.length || 0;
    return len >= size * 2;
});

const optional = computed(() => _required.value == undefined ? undefined : !_required.value);

const alignItems = computed(() => {
    switch (props.align) {
        case 'top':
            return 'start';
        case 'bottom':
            return 'end';
        case 'middle':
        case 'center':
        default:
            return props.align;
    }
});

const ok = computed(() => valid.value == null ? false : !valid.value.error);
const error = computed(() => valid.value == null ? false : valid.value.error);

const rootElement = ref<HTMLElement>();

defineExpose({
    validate
});

function validate(val: any = model.value, event?: Event): ValidateResult {
    let result: ValidateResult = { error: false };

    if (val == null) {
        if (_required.value === true)
            result = {
                error: true,
                type: 'required',
                message: 'The field is required.',
            };
    } else {
        let validator = _validator.value;
        if (validator != null)
            try {
                result = validator(val);
                if (result == null)
                    result = { error: false };
            } catch (err) {
                result = {
                    error: true,
                    exception: err,
                    message: err.toString(), // 'Error: message'
                };
            }
    }

    if (event != null) result.target = event.target;

    valid.value = result;
    return result;
}

function formatError(err: string) {
    if (err == null) return "(no more information)";
    let first = err.substring(0, 1);
    err = first.toUpperCase() + err.substring(1);
    if (err.match(/\w$/))
        err += "!";
    return err;
}

onMounted(() => {
});

if (props.watch) {
    watch(model, (newVal, oldVal) => {
        validate(newVal);
    }, {
        immediate: true
    });
}

</script>

<template>
    <component :is="tagName" class="component-root field-row" :class="{ required, optional, ok, error }"
        ref="rootElement">
        <component :is="childTagName" class="icon-label">
            <Icon :name="_icon" v-if="_icon != null" />
            <span class="label" :class="{ morebit, morehalf, more2 }"> {{ _label }} </span>
            <Icon v-if="after == 'label'" :name="expanded ? collapseIcon : expandIcon" class="toggler"
                @click="expanded = !expanded" />
        </component>
        <component :is="childTagName" class="content"
            v-if="description == null && _validator == null && _required != true">
            <div class="content">
                <slot>
                </slot>
            </div>
        </component>
        <component :is="childTagName" class="with-description" :class="{ expanded }" v-else>
            <div class="content">
                <slot>
                </slot>
                <Icon class="validate" name="far-check-circle" @click="validate()" v-if="!watch" />
                <Icon class="validate-state" :name="valid.error ? 'fa-times' : 'fa-check'"
                    v-if="valid != null && (showState || valid.error)" />
                <Icon v-if="after == 'end'" :name="expanded ? collapseIcon : expandIcon" class="toggler"
                    @click="expanded = !expanded" />
            </div>
            <div class="error-info" v-if="valid != null && valid.error">
                <slot name="error" v-bind="valid">
                    <span class="type" v-if="valid.type != null"> {{ valid.type }} </span>
                    <span class="code" v-if="valid.errorCode != null"> {{ valid.errorCode }} </span>
                    <span class="message" v-if="valid.message != null"> {{ formatError(valid.message) }} </span>
                </slot>
            </div>
            <div class="description" v-if="_description != null">
                <slot name="description"> {{ _description }} </slot>
            </div>
        </component>
    </component>
</template>

<style scoped lang="scss">
.component-root {
    padding: 0;
}

.field-row {
    display: v-bind("tagName == 'tr' ? 'table-row' : 'flex'");
    flex-direction: row;
    align-items: v-bind(alignItems);
    break-inside: avoid;
}

.icon-label {
    display: flex;
    flex-direction: row;
    align-items: center;

    margin-right: 1em;

    .icon {
        width: v-bind(iconWidth);
        margin-right: .6rem;
        text-align: center;
        font-size: 80%;
    }
}

.label {
    width: v-bind(autoLabelWidth);
    text-align: v-bind(alignLabel);

    &.morebit {
        // font-size: 80%;
    }

    &.morehalf {
        // font-size: 80%;
    }

    &.more2 {
        font-size: 80%;
    }
}

.with-description {
    flex: 1;
    overflow: hidden;

    .content {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .icon {
        margin-left: .5em;
        font-size: 80%;
    }

    &:not(.expanded) {
        .toggler {
            opacity: 50%;
        }

        .description {
            display: none;
        }
    }

}

.error .validate-state {
    color: red;
}

.ok .validate-state {
    color: green;
}

.toggler {
    color: #999;
    margin-left: 1em;
}

.description {
    margin-top: .5em;
    border-top: dashed 1px gray;
    font-weight: 300;
    font-size: 85%;
    opacity: 80%;
}

.optional {

    .label,
    .description {
        font-style: italic;
    }
}

.error-info {
    display: flex;
    flex-direction: row;
    align-items: center;
    color: #f44;
    margin-top: .3em;

    .icon {
        margin-right: .2em;
        color: #f00;
    }

    .type {
        margin-right: .5em;
        padding: 0 .5em;
        background-color: #ff8;
        border: solid 1px #f88;
        border-radius: .2em;
        font-size: 70%;
        padding: .2em .8em;

        &:after {
            content: ": ";
        }
    }

    .code {
        margin-right: .5em;
        padding: 0 .5em;
        background-color: #ff8;
        border: solid 1px #f88;
        border-radius: .2em;
        font-size: 70%;
        padding: .2em .8em;

        &:after {
            content: ": ";
        }
    }

    .message {
        font-weight: 300;
    }

}
</style>
