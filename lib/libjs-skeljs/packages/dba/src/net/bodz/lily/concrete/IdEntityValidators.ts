
import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoObjectValidators } from './CoObjectValidators';

export abstract class IdEntityValidators extends CoObjectValidators {

    validateId(val: any) {
    }

}

export default IdEntityValidators;