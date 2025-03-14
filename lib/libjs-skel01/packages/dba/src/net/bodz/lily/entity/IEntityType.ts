import type { ITypeInfo } from "skel01-core/src/lang/ITypeInfo";
import type { EntityPropertyMap } from "./EntityPropertyMap";
import EntityProperty from "./EntityProperty";

export interface IEntityType extends ITypeInfo<any> {
    get name(): string        // Java class name

    get property(): EntityPropertyMap

    /**
     * sorted properties.
     */
    get properties(): EntityProperty[];

}

export default IEntityType;
