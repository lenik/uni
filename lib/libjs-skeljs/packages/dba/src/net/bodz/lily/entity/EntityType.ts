import TypeInfo from "@skeljs/core/src/lang/TypeInfo";
import IEntityType from "./IEntityType";
import IEntityProperty from "./IEntityProperty";
import EntityProperty from "./EntityProperty";
import EntityPropertyMap from "./EntityPropertyMap";
import { simpleName } from "@skeljs/core/src/logging/api";

export abstract class EntityType extends TypeInfo<any> implements IEntityType {
    abstract override get name(): string        // Java class name

    property: EntityPropertyMap = {}
    ordinalPositionBase: number = 0

    declare(declaredProperties: EntityPropertyMap) {
        // save object key as property.name
        for (let k in declaredProperties) {
            let prop = declaredProperties[k];
            prop.name = k;
        }

        let base = this.ordinalPositionBase;
        let sorted = Object.values(declaredProperties).sort(
            (a, b) => a._g_index! - b._g_index!);

        for (let i = 0; i < sorted.length; i++) {
            let prop = sorted[i];

            let name = prop.name!;
            let prev = this.property[name];
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
            this.property[name] = prop;
        }

        this.ordinalPositionBase += sorted.length;
    }

    get simpleName() {
        return simpleName(this.name);
    }

    get properties(): EntityProperty[] {
        let v = Object.values(this.property);
        v.sort((a, b) => a.position! - b.position!);
        return v;
    }

    format(val: any): string {
        let json = JSON.stringify(val);
        return json;
    }

    parse(s: string) {
        let obj = JSON.parse(s);
        return obj;
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
