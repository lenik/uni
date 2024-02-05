
export interface CounterType {
    priority?: number
    label: string
    icon?: string
    format?: (val: number) => string
    cformat?: (vals: CounterValues) => string | undefined
}

export interface CounterTypes {
    [key: string]: CounterType
}

export interface CounterValues {
    [key: string]: number
}

export interface CounterTypedValue extends CounterType {
    value: number
}
