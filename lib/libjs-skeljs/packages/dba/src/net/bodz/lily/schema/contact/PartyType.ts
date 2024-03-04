import { EntityPropertyMap, property } from '../../entity/EntityType';
import { IdEntityType } from '../../concrete/IdEntityType';

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
