import { integer } from '@skeljs/core/src/lang/type';
import CoObject from './CoObject';
import CoEntityTypeInfo from './CoEntityTypeInfo';

export abstract class CoEntity<Id> extends CoObject {
    static TYPE = new CoEntityTypeInfo();

    constructor(o: any) {
        super(o);
    }
}

export default CoEntity;