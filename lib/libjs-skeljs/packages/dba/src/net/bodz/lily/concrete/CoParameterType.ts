
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoCodeType } from './CoCodeType';

export abstract class CoParameterType extends CoCodeType {

    static declaredProperty: EntityPropertyMap = {
        // id: primaryKey({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(CoParameterType.declaredProperty);
    }
}

export default CoParameterType;