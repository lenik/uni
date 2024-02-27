import { Moment } from 'moment';
import { EntityPropertyMap } from '../entity';
import IdEntityValidators from './IdEntityValidators';

export class CoMomentIntervalValidators extends IdEntityValidators {

    validateBeginTime(val: Moment) { }

    validateEndTime(val: Moment) { }

}

export default CoMomentIntervalValidators;
