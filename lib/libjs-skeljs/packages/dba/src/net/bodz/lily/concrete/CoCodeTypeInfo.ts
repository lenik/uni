
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeValidators from './CoCodeValidators';
import CoNodeTypeInfo from './CoNodeTypeInfo';

export class CoCodeTypeInfo extends CoNodeTypeInfo {

    static validators = new CoCodeValidators();

    static declaredProperty: EntityPropertyMap = {
        code: property({ type: 'string', precision: 30 })
    };

    constructor() {
        super();
        this.declare(CoCodeTypeInfo.declaredProperty);
    }

}

export default CoCodeTypeInfo;