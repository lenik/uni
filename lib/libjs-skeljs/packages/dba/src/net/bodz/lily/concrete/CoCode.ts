import { integer } from '@skeljs/core/src/lang/type';
import { CoNode } from './CoNode';

export abstract class CoCode<This> extends CoNode<This, integer> {

    code?: string

    get uniqueName() { return this.code; }
    set uniqueName(val: string | undefined) { this.code = val; }

    constructor(o: any) {
        super(o);
    }
}

export default CoCode;