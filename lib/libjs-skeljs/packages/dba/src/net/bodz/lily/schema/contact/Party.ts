import { Moment } from 'moment-timezone';
import { int } from '@skeljs/core/src/lang/basetype';
import { IdEntity } from '../../concrete/IdEntity';
import { PartyType } from './PartyType';

export abstract class Party extends IdEntity<int> {

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
