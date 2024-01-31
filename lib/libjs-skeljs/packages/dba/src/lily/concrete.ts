
import { Moment } from 'moment';
import { EntityPropertyMap, EntityType, integer, property } from '../lily/entity';

export class CoObjectType extends EntityType {

    static declaredProperty: EntityPropertyMap = {
        label: property({ type: 'string', icon: 'fa-tag' }),
        description: property({ type: 'string', icon: 'far-info-circle' }),
    };

    constructor() {
        super();
        this.declare(CoObjectType.declaredProperty);
    }
}

export abstract class CoObject {
    label?: string
    description?: string
    comment?: string
    image?: string
    imageAlt?: string
    flags: integer
    priority: integer
    state: integer
    ownerUser: any
    ownerUserId: integer
    ownerGroup: any
    owenrGroupId: integer
    acl: integer
    accessMode: integer

    constructor(o: any) {
        if (o != null) Object.assign(this, o);
    }
}

export abstract class IdEntityType<Id> extends CoObjectType {

    static declaredProperty: EntityPropertyMap = {
        id: property({ type: 'number', precision: 20, })
    };

    constructor() {
        super();
        this.declare(IdEntityType.declaredProperty);
    }
}

export abstract class IdEntity<Id> extends CoObject {
    id?: Id

    constructor(o: any) {
        super(o);
        if (o != null) Object.assign(this, o);
    }
}

export class Contact extends IdEntity<integer> {

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

export abstract class CoPartyType extends IdEntityType<number> {

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
        this.declare(CoPartyType.declaredProperty);
    }
}

export abstract class CoParty extends IdEntity<integer> {

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
