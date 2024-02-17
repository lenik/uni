
import { integer } from '../entity';
import { CoObject } from './CoObject';

export abstract class CoPrincipal extends CoObject {
    id?: integer

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}
