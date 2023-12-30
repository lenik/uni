
declare interface CounterType {
    priority?: number
    label: string
    iconfa: string
    format?: (val: number) => string
    cformat?: (vals: CounterValues) => string
}

declare interface CounterTypes {
    [key: string]: CounterType
}

declare interface CounterValues {
    [key: string]: number
}

declare interface CounterTypedValue extends CounterType {
    value: number
}
