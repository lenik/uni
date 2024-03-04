import { int } from "@skeljs/core/src/lang/basetype"
import IEntityType from "./IEntityType"
import { Validator } from "@skeljs/core/src/ui/types"

export interface IEntityProperty {

    name?: string
    position?: int
    type: string | (new () => IEntityType) | IEntityType // ts type, not java type
    primaryKey?: boolean

    // javaType: string
    precision?: int
    scale?: int
    nullable?: boolean

    icon?: string
    label?: string
    description?: string

    validator?: Validator
}

export default IEntityProperty;
