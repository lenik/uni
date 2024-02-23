import { EntityPropertyMap, primaryKey, property } from '../entity';
import CoCodeType from './CoCodeType';
import CoTagValidators from './CoTagValidators';

export class CoTagType extends CoCodeType {

    static validators = new CoTagValidators();

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoTagType.declaredProperty);
    }

}

export default CoTagType;