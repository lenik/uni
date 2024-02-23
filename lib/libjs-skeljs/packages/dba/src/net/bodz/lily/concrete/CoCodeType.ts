
import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeValidators from './CoCodeValidators';
import CoNodeType from './CoNodeType';

export class CoCodeType extends CoNodeType {

    static validators = new CoCodeValidators();

    static declaredProperty: EntityPropertyMap = {
        code: property({ type: 'string', precision: 30 })
    };

    constructor() {
        super();
        this.declare(CoCodeType.declaredProperty);
    }

}

export default CoCodeType;