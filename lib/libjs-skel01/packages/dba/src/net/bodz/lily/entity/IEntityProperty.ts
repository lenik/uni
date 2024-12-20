import { int } from "@skel01/core/src/lang/basetype"
import { Validator } from "@skel01/core/src/ui/types"
import ITypeInfo from "@skel01/core/src/lang/ITypeInfo"

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
