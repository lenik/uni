
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeTypeInfo from './CoCodeTypeInfo';
import CoParameterValidators from './CoParameterValidators';

export class CoParameterTypeInfo extends CoCodeTypeInfo {

    static validators = new CoParameterValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoParameterTypeInfo.declaredProperty);
    }

}

export default CoParameterTypeInfo;