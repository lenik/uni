import { integer } from '@skeljs/core/src/lang/type';
import CoObject from './CoObject';
import CoEntityType from './CoEntityType';

export abstract class CoEntity<Id> extends CoObject {
    static TYPE = new CoEntityType();

    constructor(o: any) {
        super(o);
    }
}

export default CoEntity;