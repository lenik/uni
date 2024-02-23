import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoCodeType } from './CoCodeType';

export abstract class CoTagType extends CoCodeType {

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoTagType.declaredProperty);
    }
}

export default CoTagType;