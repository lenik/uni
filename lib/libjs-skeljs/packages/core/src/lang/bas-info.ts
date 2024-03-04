import TypeInfo from "./TypeInfo";

export class JSONVariantType extends TypeInfo<any> {

    get name() { return "Object"; }
    get icon() { return "far-js"; }

    format(val: any): string {
        return JSON.stringify(val);
    }

    parse(s: string): Date {
        return JSON.parse(s);
    }

}

export const JSON_VARIANT = new JSONVariantType();
