import { int } from "@skeljs/core/src/lang/basetype"
import { Validator } from "@skeljs/core/src/ui/types"
import IEntityProperty from "./IEntityProperty"
import ITypeInfo from "@skeljs/core/src/lang/ITypeInfo"

export class EntityProperty implements IEntityProperty {

    /**
     * name is optional. Default to the key name if the property is declared in a object-map.
     */
    name?: string

    position?: int
    type: ITypeInfo<any>
    primaryKey?: boolean

    // javaType: string
    precision?: int
    scale?: int
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    validator?: Validator

    _g_index?: int;

    constructor(o: IEntityProperty) {
        Object.assign(this, o);
    }

    get display() {
        if (this.label != null && this.label.length)
            return this.label;
        let name = this.name;
        if (name != null) {
            let ucfirst = name.substring(0, 1).toUpperCase();
            return ucfirst + name.substring(1);
        }
        return null;
    }
}

export default EntityProperty;
