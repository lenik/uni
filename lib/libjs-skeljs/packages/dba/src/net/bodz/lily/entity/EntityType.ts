import TypeInfo from "@skeljs/core/src/lang/TypeInfo";
import type { IEntityType } from "./IEntityType";
import type { IEntityProperty } from "./IEntityProperty";
import EntityProperty from "./EntityProperty";
import type { EntityPropertyMap } from "./EntityPropertyMap";
import type { ITypeInfo } from "@skeljs/core/src/lang/ITypeInfo";

export abstract class EntityType extends TypeInfo<any> implements IEntityType {

    private allProps: EntityPropertyMap = {}
    private applied = false;
    // private declStack: EntityPropertyMap[] | undefined = []
    private ordinalPositionBase: number = 0

    /**
     * Java class name
     */
    abstract override get name(): string

    get simpleName() {
        if (this.name == null) return this.name;
        let lastDot = this.name.lastIndexOf('.');
        return lastDot == -1 ? this.name : this.name.substring(lastDot + 1);
    }

    protected abstract preamble(): void;

    protected declare(props: EntityPropertyMap) {
        // save object key as property.name
        for (let k in props) {
            let prop = props[k];
            prop.name = k;
        }

        let base = this.ordinalPositionBase;
        let sorted = Object.values(props).sort(
            (a, b) => a._g_index! - b._g_index!);

        for (let i = 0; i < sorted.length; i++) {
            let prop = sorted[i];

            let name = prop.name!;
            let prev = this.allProps[name];
            if (prev != null) {
                prop.position = prev.position;

                prop.type ||= prev.type;
                prop.precision ||= prev.precision;
                prop.scale ||= prev.scale;

                prop.label ||= prev.label;
                prop.description ||= prev.description;
                prop.icon ||= prev.icon;

                prop.validator ||= prev.validator;

                if (prop.primaryKey == null) prop.primaryKey = prev.primaryKey;
                if (prop.nullable == null) prop.nullable = prev.nullable;
                // prop.unique
            }

            prop.position ||= base + i;

            delete prop._g_index;
            this.allProps[name] = prop;
        }

        this.ordinalPositionBase += sorted.length;
    }

    get property() {
        if (!this.applied) {
            this.preamble();
            this.applied = true;
        }
        return this.allProps;
    }

    get properties(): EntityProperty[] {
        if (!this.applied) {
            this.preamble();
            this.applied = true;
        }
        let v = Object.values(this.allProps);
        v.sort((a, b) => a.position! - b.position!);
        return v;
    }

    override format(val: any): string {
        let json = JSON.stringify(val);
        return json;
    }

    override parse(s: string) {
        let obj = JSON.parse(s);
        return obj;
    }

    override fromJson(jv: any): any {
        let o = this.create();
        for (let property of this.properties) {
            let name = property.name!;
            if (o.__lookupGetter__(name) != null)
                continue;
            let propJv = jv[name];
            if (propJv != null) {
                let propType: ITypeInfo<any> = property.type;
                let propVal;
                if (propType.fromJson != undefined) {
                    propVal = propType.fromJson(propJv);
                } else {
                    console.warn("fromJson() isn't defined on "
                        + propType.name + " property " + property.name);
                    propVal = propJv;
                }
                o[name] = propVal;
            }
        }
        return o;
    }

    override toJson(o: any): any {
        let jv: any = {};
        for (let property of this.properties) {
            let name = property.name!;
            if (o.__lookupGetter__(name) != null)
                continue;
            let propVal = o[name];
            if (propVal != null) {
                let propType: ITypeInfo<any> = property.type;
                let propJv = propType.toJson(propVal);
                jv[name] = propJv;
            }
        }

        return jv;
    }

}

let _next_g_index: number = 1;
export function property(a: IEntityProperty): EntityProperty {
    let prop = new EntityProperty(a);
    prop._g_index = _next_g_index++;
    return prop;
}

export function primaryKey(a: IEntityProperty): EntityProperty {
    let prop = property(a);
    prop.primaryKey = true;
    return prop;
}

export default EntityType;
