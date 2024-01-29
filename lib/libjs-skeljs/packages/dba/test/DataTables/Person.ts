
import { Moment } from "moment";

import { EntityProperty, EntityPropertyMap, EntityType, IEntityProperty, IdEntity, Integer, integer } from '../../src/ui/table/types';


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

        father: property({ type: "Person" }),
        mother: property({ type: "Person" }),
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

class Contact extends IdEntity<Integer> {

    org: any
    orgUnit: any
    person: any

    rename: String
    usage: String

    zone: any
    zoneId: Integer

    country: String
    r1: String
    r2: String
    r3: String
    r4: String
    address1: String
    address2: String
    postalCode: String

    tel: String
    mobile: String
    fax: String
    email: String
    web: String
    qq: String
    wechat: String

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}

class CoParty extends IdEntity<Integer> {

    category?: any
    birthday?: Moment

    locale: string
    timeZoneId: string

    peer: boolean = false
    custoemr: boolean = false
    supplier: boolean = false

    tags: string[]

    subject: String
    contacts: Contact[]

    bank: String
    account: String

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}

export class Person extends CoParty {

    father: Person
    mother: Person
    roleCount: integer
    employee: boolean
    bankCount: integer
    gender: String
    homeland: String
    passport: String
    ssn: String
    dln: String

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }

    static TYPE = new PersonType();
}
