import type { integer } from '@skeljs/core/src/lang/type';
import { simpleName } from "@skeljs/core/src/logging/api";
import type { Validator } from "@skeljs/core/src/ui/types";

export interface IEntityType {
    name: string        // Java class name
    icon?: string

    label?: string
    description?: string

    property: EntityPropertyMap
}

export class EntityType implements IEntityType {
    name: string        // Java class name
    icon?: string

    label?: string
    description?: string

    property: EntityPropertyMap = {}
    ordinalPositionBase: number = 0

    constructor() {
    }

    declare(declaredProperties: EntityPropertyMap) {
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
            if (prev != null)
                prop.position = prev.position;
            prop.position ||= base + i;

            delete prop._g_index;
            this.property[name] = prop;
        }

        this.ordinalPositionBase += sorted.length;
    }

    get simpleName() {
        return simpleName(this.name);
    }

    get properties() {
        let v = Object.values(this.property);
        v.sort((a, b) => a.position! - b.position!);
        return v;
    }

}

export interface EntityPropertyMap {
    [propertyName: string]: EntityProperty
}

export interface IEntityProperty {

    name?: string
    position?: integer
    type: string // ts type, not java type
    primaryKey?: boolean

    // javaType: string
    precision?: integer
    scale?: integer
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    validator?: Validator
}

export class EntityProperty implements IEntityProperty {

    name?: string
    position?: integer
    type: string
    primaryKey?: boolean

    // javaType: string
    precision?: integer
    scale?: integer
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    validator?: Validator

    _g_index?: integer;

    constructor(o: IEntityProperty) {
        Object.assign(this, o);
    }

    get display() {
        if (this.label != null && this.label.length)
            return this.label;
        let name = this.name;
        if (name == null)
            return this.label;
        let ucfirst = name.substring(0, 1).toUpperCase();
        return ucfirst + name.substring(1);
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
