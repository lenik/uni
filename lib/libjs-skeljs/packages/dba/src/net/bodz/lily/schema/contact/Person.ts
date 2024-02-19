import type { integer } from "@skeljs/core/src/lang/type";
import { Party } from './Party';
import PersonType from './PersonType';

export class Person extends Party {
    static TYPE = new PersonType();

    gender?: string

    father?: Person
    fatherId?: integer
    mother?: Person
    motherId?: integer

    roleCount?: integer
    // roles: string[]

    employee: boolean
    bankCount?: integer
    homeland?: string
    passport?: string
    ssn?: string
    dln?: string

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}
