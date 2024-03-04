import ITypeInfo from './ITypeInfo';

export abstract class TypeInfo<T> implements ITypeInfo<T> {

    abstract get name(): string;

    get label(): string | undefined {
        return undefined;
    }

    get display(): string {
        return this.label || this.name;
    }

    get description(): string | undefined {
        return undefined;
    }

    get icon(): string | undefined {
        return "fas-question";
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
