
export interface ITypeInfo<T> {

    get name(): string
    get label(): string | undefined
    get description(): string | undefined
    get icon(): string | undefined
    get display(): string  // default: label or name

    createElement(parentElement: HTMLElement, val: any): HTMLElement

    validate(T: any): void

    parse(s: string): T

    format(val: T): string

    get nullText(): string

}

export abstract class TypeInfo<T> implements ITypeInfo<T> {

    abstract get name(): string

    get label(): string | undefined {
        return undefined;
    }

    get description(): string | undefined {
        return undefined;
    }

    get icon(): string | undefined {
        return "fas-question"
    }

    get display() {
        return this.label || this.name;
    }

    createElement(parentElement: HTMLElement, val: any): HTMLElement {
        throw "not implemented";
    }

    validate(val: T) {
    }

    abstract parse(s: string): T

    format(val: T): string {
        if (val == null)
            return this.nullText;
        else
            return "" + val;
    }

    get nullText(): string {
        return "null";
    }

}

export default TypeInfo;
