import ITypeInfo from "@skeljs/core/src/lang/ITypeInfo";
import EntityProperty from "./EntityProperty";
import EntityPropertyMap from "./EntityPropertyMap";

export interface IEntityType extends ITypeInfo<any> {
    get name(): string        // Java class name

    get property(): EntityPropertyMap

    /**
     * sorted properties.
     */
    get properties(): EntityProperty[];

}

export default IEntityType;
