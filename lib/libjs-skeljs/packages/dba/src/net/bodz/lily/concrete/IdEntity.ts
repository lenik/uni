import { integer } from '@skeljs/core/src/lang/type';
import CoObject from './CoObject';
import IdEntityType from './IdEntityType';

export abstract class IdEntity<Id> extends CoObject {
    static TYPE: any = new IdEntityType();

    id?: Id

    constructor(o: any) {
        super(o);
    }
}

export default IdEntity;