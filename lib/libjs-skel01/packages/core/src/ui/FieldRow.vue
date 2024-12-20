<script lang="ts">
import { computed, inject, onMounted, ref, watch } from "vue";
import type { ValidateResult, Validator } from "./types";
import Icon from "./Icon.vue";

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

export const FIELD_ROW_PROPS = Symbol('FieldRowProps');
</script>

<script setup lang="ts">
const model = defineModel();
const expanded = defineModel<boolean>('expanded');
const valid = defineModel<ValidateResult>('valid');

const _props = withDefaults(defineProps<Props>(), {
    label: 'unnamed',
});

const emit = defineEmits<{
    'update:vaild': [val: ValidateResult]
    error: [result: ValidateResult, event?: Event]
    validated: [result: ValidateResult, event?: Event]
}>();

// property shortcuts

const fieldRowProps = inject<Props>(FIELD_ROW_PROPS, {});

function coalesce<T>(...args: T[]): T | undefined {
    for (let i = 0; i < args.length; i++)
        if (args[i] !== undefined)
            return args[i];
    return undefined;
}

const mProps = computed<Props>(() => ({
    tagName: coalesce(_props.tagName, fieldRowProps?.tagName, 'div'),
    icon: coalesce(_props.icon, fieldRowProps?.icon),
    iconWidth: coalesce(_props.iconWidth, fieldRowProps?.iconWidth, '1rem'),
    labelWidth: coalesce(_props.labelWidth, fieldRowProps?.labelWidth),
    labelMaxLength: coalesce(_props.labelMaxLength, fieldRowProps?.labelMaxLength),
    description: coalesce(_props.description, fieldRowProps?.description),
    after: coalesce(_props.after, fieldRowProps?.after, 'end'),
    expandIcon: coalesce(_props.expandIcon, fieldRowProps?.expandIcon, 'far-question-circle'),
    collapseIcon: coalesce(_props.collapseIcon, fieldRowProps?.collapseIcon, 'fas-question-circle'),
    tooltip: coalesce(_props.tooltip, fieldRowProps?.tooltip),
    align: coalesce(_props.align, fieldRowProps?.align, 'middle'),
    alignLabel: coalesce(_props.alignLabel, fieldRowProps?.alignLabel, 'left'),
    required: coalesce(_props.required, fieldRowProps?.required),
    showState: coalesce(_props.showState, fieldRowProps?.showState, false),
    watch: coalesce(_props.watch, fieldRowProps?.watch, true),
}));

const mIconWidth = computed(() => mProps.value.iconWidth);
const mAlignLabel = computed(() => mProps.value.alignLabel);

const childTagName = computed(() => {
    switch (mProps.value.tagName) {
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
    if (_props.property == null)
        return _props.label;
    else
        return _props.property.display;
});

const _icon = computed(() => _props.property?.icon || mProps.value.icon);
const _description = computed(() => _props.property?.description || mProps.value.description);
const hasDescription = computed(() => _description.value != null && _description.value.length);

const _tooltip = computed(() => _props.property?.tooltip || mProps.value.tooltip);
const _validator = computed(() => _props.property?.validator || _props.validator);
const _required = computed(() => _props.property?.required || mProps.value.required);
const hasValidateInfo = computed(() => _props.validator != null || _props.required || valid.value?.error)

const autoLabelWidth = computed(() => {
    const defaultLabelWidth = '10em';
    const labelLengthLimit = 32;
    if (mProps.value.labelWidth != null)
        return mProps.value.labelWidth;

    if (mProps.value.labelMaxLength != null) {
        let len = Math.min(mProps.value.labelMaxLength, labelLengthLimit);
        return len + "em";
    }

    return defaultLabelWidth;
});

const labelSize = computed(() => {
    if (mProps.value.labelMaxLength != null)
        return mProps.value.labelMaxLength;
    else if (mProps.value.labelWidth != null)
        return parseInt(mProps.value.labelWidth.replace("rem", "").replace("em", ""));
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
    switch (mProps.value.align) {
        case 'top':
            return 'start';
        case 'bottom':
            return 'end';
        case 'middle':
        case 'center':
        default:
            return mProps.value.align;
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
            } catch (e) {
                result = {
                    error: true,
                    exception: e,
                    message: String(e), // 'Error: message'
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

if (mProps.value.watch) {
    watch(model, (newVal, oldVal) => {
        validate(newVal);
    }, {
        immediate: true
    });
}

</script>

<template>
    <component :is="mProps.tagName" class="component-root field-row"
        :class="{ required: _required, optional, ok, error }" ref="rootElement">
        <component :is="childTagName" class="icon-label">
            <Icon :name="_icon" v-if="_icon != null" />
            <span class="label" :class="{ morebit, morehalf, more2 }"> {{ _label }} </span>
            <Icon v-if="mProps.after == 'label'" :name="expanded ? mProps.collapseIcon! : mProps.expandIcon!"
                class="toggler" @click="expanded = !expanded" />
        </component>
        <component :is="childTagName" class="content" v-if="!hasDescription && !hasValidateInfo && _required != true">
            <div class="content">
                <slot>
                </slot>
            </div>
        </component>
        <component :is="childTagName" class="with-description" :class="{ expanded }" v-else>
            <div class="content">
                <slot>
                </slot>
                <Icon class="validate" name="far-check-circle" @click="validate()" v-if="!mProps.watch" />
                <Icon class="validate-state" :name="valid.error ? 'fa-times' : 'fa-check'"
                    v-if="valid != null && (mProps.showState || valid.error)" />
                <Icon v-if="mProps.after == 'end'" :name="expanded ? mProps.collapseIcon! : mProps.expandIcon!"
                    class="toggler" @click="expanded = !expanded" />
            </div>
            <div class="error-info" v-if="valid != null && valid.error">
                <slot name="error" v-bind="valid">
                    <span class="type" v-if="valid.type != null"> {{ valid.type }} </span>
                    <span class="code" v-if="valid.errorCode != null"> {{ valid.errorCode }} </span>
                    <span class="message" v-if="valid.message != null"> {{ formatError(valid.message) }} </span>
                </slot>
            </div>
            <div class="description" v-if="hasDescription">
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

    >.content {
        flex: 1;
    }
}

.icon-label {
    display: flex;
    flex-direction: row;
    align-items: center;

    margin-right: 1em;

    >.icon {
        width: v-bind(mIconWidth);
        margin-right: .6rem;
        text-align: center;
        font-size: 80%;
    }

    >.label {
        width: v-bind(autoLabelWidth);
        text-align: v-bind(mAlignLabel);

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
