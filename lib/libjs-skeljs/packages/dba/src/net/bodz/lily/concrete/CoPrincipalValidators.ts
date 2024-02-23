import { EntityPropertyMap, primaryKey, property } from '../entity';
import { CoObjectValidators } from './CoObjectValidators';

export abstract class CoPrincipalValidators extends CoObjectValidators {

    validateName(val: string) {

    }

    validateProperties(val: string) {

    }

}

export default CoPrincipalValidators;