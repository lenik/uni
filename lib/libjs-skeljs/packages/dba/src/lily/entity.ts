import { simpleName } from "@skeljs/core/src/logging/api";
import type { Validator } from "@skeljs/core/src/ui/types";

export type integer = number;
export type long = number;

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

    constructor() {
    }

    declare(properties: EntityPropertyMap) {
        for (let k in properties) {
            let prop = properties[k];
            prop.name = k;
            // prop = new EntityProperty(prop);
            // properties[k] = prop;
        }
        Object.assign(this.property, properties);
    }

    get simpleName() {
        return simpleName(this.name);
    }
}

export interface EntityPropertyMap {
    [propertyName: string]: EntityProperty
}

export interface IEntityProperty {

    name?: string
    type: string // ts type, not java type
    // javaType: string
    precision?: number
    scale?: number
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    validator?: Validator
}

export class EntityProperty implements IEntityProperty {

    name?: string
    type: string
    precision?: number
    scale?: number
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    validator?: Validator

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

export function property(a: IEntityProperty): EntityProperty {
    let prop = new EntityProperty(a);
    return prop;
}
