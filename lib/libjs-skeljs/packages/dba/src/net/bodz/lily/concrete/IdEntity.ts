import { integer } from '@skeljs/core/src/lang/type';
import { CoObject } from './CoObject';

export abstract class IdEntity<Id> extends CoObject {
    id?: Id

    constructor(o: any) {
        super(o);
    }
}

export default IdEntity;