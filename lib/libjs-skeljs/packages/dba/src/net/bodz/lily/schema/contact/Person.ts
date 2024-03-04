import type { int } from "@skeljs/core/src/lang/basetype";
import { Party } from './Party';
import PersonType from './PersonType';

export class Person extends Party {
    static TYPE = new PersonType();

    gender?: string

    father?: Person
    fatherId?: int
    mother?: Person
    motherId?: int

    roleCount?: int
    // roles: string[]

    employee: boolean
    bankCount?: int
    homeland?: string
    passport?: string
    ssn?: string
    dln?: string

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}
