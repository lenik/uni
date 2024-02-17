
import { integer } from '../entity';
import { CoObject } from './CoObject';

export abstract class IdEntity<Id> extends CoObject {
    id?: Id

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}
