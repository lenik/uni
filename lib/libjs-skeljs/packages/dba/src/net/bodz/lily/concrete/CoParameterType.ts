
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeType from './CoCodeType';
import CoParameterValidators from './CoParameterValidators';

export class CoParameterType extends CoCodeType {

    static validators = new CoParameterValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoParameterType.declaredProperty);
    }

}

export default CoParameterType;