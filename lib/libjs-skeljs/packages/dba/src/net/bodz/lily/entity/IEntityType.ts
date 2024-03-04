import ITypeInfo from "@skeljs/core/src/lang/ITypeInfo";
import EntityProperty from "./EntityProperty";

export interface IEntityType extends ITypeInfo<any> {
    get name(): string        // Java class name

    property: EntityPropertyMap

    /**
     * sorted properties.
     */
    get properties(): EntityProperty[];

}

export interface EntityPropertyMap {
    [propertyName: string]: EntityProperty
}

export default IEntityType;
