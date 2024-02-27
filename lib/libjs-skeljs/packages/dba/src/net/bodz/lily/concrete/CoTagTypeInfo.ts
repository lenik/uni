import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeTypeInfo from './CoCodeTypeInfo';
import CoTagValidators from './CoTagValidators';

export class CoTagTypeInfo extends CoCodeTypeInfo {

    name = "net.bodz.lily.concrete.CoTagType"
    icon = "fa-tag"
    label = "Concrete Tag Type"
    description = "Definition of a tag type."

    validators = new CoTagValidators();

    declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoTagTypeInfo;