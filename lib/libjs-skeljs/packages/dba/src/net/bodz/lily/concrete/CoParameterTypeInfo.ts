
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeTypeInfo from './CoCodeTypeInfo';
import CoParameterValidators from './CoParameterValidators';

export class CoParameterTypeInfo extends CoCodeTypeInfo {

    name = "net.bodz.lily.concrete.CoParameter"
    icon = "fa-adjust"
    label = "Concrete Parameter"
    description = "Definition of a parameter type."

    validators = new CoParameterValidators();

    declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(this.declaredProperty);
    }

}

export default CoParameterTypeInfo;