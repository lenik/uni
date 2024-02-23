import { integer } from '@skeljs/core/src/lang/type';
import { CoObject } from './CoObject';

export abstract class CoEntity<Id> extends CoObject {
    constructor(o: any) {
        super(o);
    }
}

export default CoEntity;