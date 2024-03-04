
export interface ITypeInfo<T> {

    get name(): string
    get label(): string | undefined
    get display(): string  // default: label or name
    get description(): string | undefined
    get icon(): string | undefined

    createElement(parentElement: HTMLElement, val: any): HTMLElement

    validate(T: any): void

    parse(s: string): T

    format(val: T): string

    get nullText(): string

}

export default ITypeInfo;
