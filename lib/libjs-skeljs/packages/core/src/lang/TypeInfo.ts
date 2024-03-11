import ITypeInfo from './ITypeInfo';

// export { ITypeInfo };

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

    validate(val: T) {
    }

    renderHtml(val: any, context: any): HTMLElement | string | undefined {
        return undefined;
    }

    fromJson(jv: any): T {
        return jv as T;
    }

    toJson(val: T): any {
        return val;
    }

}

export default TypeInfo;
