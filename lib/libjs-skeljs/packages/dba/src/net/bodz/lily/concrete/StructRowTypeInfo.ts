
import { EntityPropertyMap, EntityType, property } from '../entity';
import StructRowValidators from './StructRowValidators';

export class StructRowTypeInfo extends EntityType {

    name = "net.bodz.lily.concrete.StructRow"
    icon = "far-cube"
    label = "Content Version"
    description = "Content data with version."

    validators = new StructRowValidators();

    declaredProperty: EntityPropertyMap = {
        creationDate: property({ type: 'date', icon: 'far-calendar-plus' }),
        lastModifiedDate: property({ type: 'date', icon: 'far-edit' }),
        version: property({ type: 'number', icon: 'far-code-branch' }),
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default StructRowTypeInfo;