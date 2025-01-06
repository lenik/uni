
export interface CounterType {
    priority?: number
    label: string
    icon?: string
    format?: (val: number) => string
    cformat?: (vals: CounterValues) => string | undefined
}

export type CounterTypes = {
    [key: string]: CounterType
}

export type CounterValues = {
    [key: string]: number
}

export interface CounterTypedValue extends CounterType {
    value: number | string
}
