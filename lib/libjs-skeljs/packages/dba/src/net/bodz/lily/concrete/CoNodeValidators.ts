import { integer } from '@skeljs/core/src/lang/type';
import { EntityPropertyMap } from '../entity';
import { IdEntityValidators } from './IdEntityValidators';

export class CoNodeValidators extends IdEntityValidators {

        validateParent(val: integer) {

        }

        validateRefCount(val: integer) {

        }

        validateDepth(val: integer) {

        }

}

export default CoNodeValidators;