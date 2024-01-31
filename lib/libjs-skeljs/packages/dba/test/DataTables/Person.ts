
import { Moment } from "moment";

import { EntityProperty, EntityPropertyMap, EntityType, IEntityProperty, IdEntity, integer } from '../../src/ui/table/types';


// Type Info

export function property(a: IEntityProperty): EntityProperty {
    let prop = new EntityProperty(a);
    return prop;
}

export class IdEntityType<Id> extends EntityType {

    static declaredProperty: EntityPropertyMap = {
        id: property({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        Object.assign(this.property, IdEntityType.declaredProperty);
    }
}

export class CoPartyType extends IdEntityType<number> {

    static declaredProperty: EntityPropertyMap = {
        category: property({ type: 'any' }),
        birthday: property({ type: 'Moment', icon: "fab-pagelines" }),

        locale: property({ type: 'string' }),
        timeZoneId: property({ type: 'string' }),

        peer: property({ type: 'boolean', nullable: false }),
        customer: property({ type: 'boolean', nullable: false }),
        supplier: property({ type: 'boolean', nullable: false }),

        tags: property({ type: 'string[]' }),

        subject: property({ type: 'string' }),
        contacts: property({ type: 'Contact[]' }),

        bank: property({ type: 'string' }),
        account: property({ type: 'string' }),
    }

    constructor() {
        super();
        Object.assign(this.property, CoPartyType.declaredProperty);
    }
}

export class PersonType extends CoPartyType {

    name = "net.bodz.lily.schema.contact.Person"
    icon = "fa-user"
    label = "Contact Person"
    description = "A human being regarded as an individual."

    static declaredProperty: EntityPropertyMap = {
        label: property({ type: "string", icon: "fa-user" }),

        father: property({ type: "Person", icon: "fa-male" }),
        mother: property({ type: "Person", icon: "fa-female" }),
        roleCount: property({ type: "number" }),
        employee: property({ type: "boolean" }),
        bankCount: property({ type: "string" }),
        gender: property({ type: "string", icon: "fa-venus-mars" }),
        homeland: property({ type: "string" }),
        passport: property({ type: "string" }),
        ssn: property({ type: "string" }),
        dln: property({ type: "string" }),
    }

    constructor() {
        super();
        Object.assign(this.property, PersonType.declaredProperty);
    }

}


// 

class Contact extends IdEntity<integer> {

    org: any
    orgUnit: any
    person: any

    rename?: string
    usage?: string

    zone: any
    zoneId?: integer

    country?: string
    r1?: string
    r2?: string
    r3?: string
    r4?: string
    address1?: string
    address2?: string
    postalCode?: string

    tel?: string
    mobile?: string
    fax?: string
    email?: string
    web?: string
    qq?: string
    wechat?: string

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}

class CoParty extends IdEntity<integer> {

    category?: any
    birthday?: Moment

    locale: string
    timeZoneId: string

    peer: boolean = false
    custoemr: boolean = false
    supplier: boolean = false

    tags: string[]

    subject?: string
    contacts: Contact[]

    bank?: string
    account?: string

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}

export class Person extends CoParty {

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

    static TYPE = new PersonType();
}
