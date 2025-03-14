export interface IJsonConverter<T> {

    fromJson(jv: any): T

    toJson(val: T): any

}

export interface IJsonForm {

    get wantObjectContext(): boolean

    /**
     * @return the context if wantObjectContext.     
     *         otherwise, context is null and return the converted JSON object/value.
    */
    jsonOut(context: any): any;

    jsonIn(jv: any): void;

}

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
