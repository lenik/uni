import { int } from "@skeljs/core/src/lang/basetype"
import { Validator } from "@skeljs/core/src/ui/types"
import ITypeInfo from "@skeljs/core/src/lang/ITypeInfo"

export interface IEntityProperty {

    name?: string
    position?: int
    type: ITypeInfo<any> // ts type, not java type
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
