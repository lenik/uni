import type { IJsonConverter } from './IJsonConverter';

export interface ITypeInfo<T> extends IJsonConverter<T> {

    get name(): string
    get label(): string | undefined
    get display(): string  // default: label or name
    get description(): string | undefined
    get icon(): string | undefined

    parse(s: string): T
    format(val: T): string
    get nullText(): string

    validate(T: any): void

    renderHtml(val: any, context: any): HTMLElement | string | undefined
}

// export default ITypeInfo;
