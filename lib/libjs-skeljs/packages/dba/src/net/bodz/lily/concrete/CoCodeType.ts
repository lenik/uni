
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoNodeType } from './CoNodeType';

export abstract class CoCodeType extends CoNodeType {

    static declaredProperty: EntityPropertyMap = {
        code: property({ type: 'string', precision: 30 })
    };

    constructor() {
        super();
        this.declare(CoCodeType.declaredProperty);
    }
}

export default CoCodeType;