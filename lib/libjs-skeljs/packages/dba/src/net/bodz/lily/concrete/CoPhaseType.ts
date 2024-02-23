
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoCodeType } from './CoCodeType';

export class CoPhaseType extends CoCodeType {

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoPhaseType.declaredProperty);
    }
}

export default CoPhaseType;