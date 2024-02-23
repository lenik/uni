import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeTypeInfo from './CoCodeTypeInfo';
import CoPhaseValidators from './CoPhaseValidators';

export class CoPhaseTypeInfo extends CoCodeTypeInfo {

    static validators = new CoPhaseValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoPhaseTypeInfo.declaredProperty);
    }

}

export default CoPhaseTypeInfo;