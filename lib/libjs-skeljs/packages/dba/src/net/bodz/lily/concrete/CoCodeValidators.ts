
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoNodeValidators } from './CoNodeValidators';

export abstract class CoCodeValidators extends CoNodeValidators {

    static declaredProperty: EntityPropertyMap = {
        code: property({ type: 'string', precision: 30 })
    };

    constructor() {
        super();
        this.declare(CoCodeValidators.declaredProperty);
    }
}

export default CoCodeValidators;