import { Moment } from 'moment';

import { EntityPropertyMap, integer, property } from '../../../../../../src/lily/entity';
import { IdEntity, IdEntityType } from '../../../../../lily/concrete';

export abstract class PartyType extends IdEntityType<number> {

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
        this.declare(PartyType.declaredProperty);
    }
}

export abstract class Party extends IdEntity<integer> {

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
