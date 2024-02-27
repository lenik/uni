import { Moment } from 'moment';
import { integer } from '@skeljs/core/src/lang/type';
import IdEntity from './IdEntity';
import CoMomentIntervalTypeInfo from './CoMomentIntervalTypeInfo';

export abstract class CoMomentInterval<Id> extends IdEntity<Id> {
    static TYPE = new CoMomentIntervalTypeInfo();
    
    beginTime?: Moment
    endTime?: Moment
    year: integer
    
    constructor(o: any) {
        super(o);
    }
}

export default CoMomentInterval;