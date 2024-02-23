import { integer } from '@skeljs/core/src/lang/type';
import IdEntity from './IdEntity';
import CoPrincipalTypeInfo from './CoPrincipalTypeInfo';

export abstract class CoPrincipal extends IdEntity<integer> {
    static TYPE = new CoPrincipalTypeInfo();

    name?: string
    properties: any

    get uniqueName() { return this.name; }
    set uniqueName(val: string | undefined) { this.name = val; }

    constructor(o: any) {
        super(o);
    }
}

export default CoPrincipal;