import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeTypeInfo from './CoCodeTypeInfo';
import CoTagValidators from './CoTagValidators';

export class CoTagTypeInfo extends CoCodeTypeInfo {

    static validators = new CoTagValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoTagTypeInfo.declaredProperty);
    }

}

export default CoTagTypeInfo;