import { EntityPropertyMap, integer, property } from '../../../../../../src/lily/entity';
import { Party, PartyType, } from './Party';

import * as validators from './PersonValidators';

// Type Info

export class PersonType extends PartyType {

    name = "net.bodz.lily.schema.contact.Person"
    icon = "fa-user"
    label = "Contact Person"
    description = "A human being regarded as an individual."

    static declaredProperty: EntityPropertyMap = {
        label: property({ type: "string", icon: "fa-user", label: 'Name' }),

        gender: property({ type: "string", icon: "fa-venus-mars", label: 'Gender', validator: validators.validate_gender }),
        father: property({ type: "Person", icon: "fa-male", validator: validators.validate_father }),
        mother: property({ type: "Person", icon: "fa-female", validator: validators.validate_mother }),

        roleCount: property({ type: "number" }),
        employee: property({ type: "boolean" }),
        bankCount: property({ type: "string" }),
        homeland: property({ type: "string", validator: validators.validate_homeland }),
        passport: property({ type: "string", validator: validators.validate_passport }),
        ssn: property({ type: "string", icon: "far-address-card", label: 'Social Security Number', validator: validators.validate_ssn }),
        dln: property({ type: "string", icon: "far-car", label: "Driver License Number", validator: validators.validate_dln }),
    }

    constructor() {
        super();
        this.declare(PersonType.declaredProperty);
    }

}


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
