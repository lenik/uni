import TypeInfo from "./TypeInfo";

export class JsonVariantType extends TypeInfo<any> {

    get name() { return "Object"; }
    get icon() { return "far-js"; }

    format(val: any): string {
        return JSON.stringify(val);
    }

    parse(s: string): any {
        return JSON.parse(s);
    }

    override fromJson(jv: any) {
        return jv;
    }

    override toJson(val: any) {
        return val;
    }

}

export const JSON_VARIANT = new JsonVariantType();

export const typeMap = {
    'JSON_VARIANT': JSON_VARIANT,
};
