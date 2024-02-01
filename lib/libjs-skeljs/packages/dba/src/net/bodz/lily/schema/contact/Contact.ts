import { EntityPropertyMap, integer, property } from "../../../../../lily/entity"
import { IdEntity, IdEntityType } from "../../../../../lily/concrete"

export class ContactType extends IdEntityType<number> {

    name = "net.bodz.lily.schema.contact.Contact"
    icon = "fa-address-card"
    label = "Contact Information"
    description = "A contact record."

    static declaredProperty: EntityPropertyMap = {
        org: property({ type: 'any', icon: "fa-build" }),
        orgUnit: property({ type: 'any', icon: "fa-build" }),
        person: property({ type: 'any', icon: "fa-male" }),

        rename: property({ type: 'string', icon: "fa-tag" }),
        usage: property({ type: 'string', icon: "fa-tag" }),

        zone: property({ type: 'string', icon: "fa-tag" }),
        country: property({ type: 'string', icon: "fa-tag" }),
        r1: property({ type: 'string', icon: "fa-tag" }),
        r2: property({ type: 'string', icon: "fa-tag" }),
        r3: property({ type: 'string', icon: "fa-tag" }),
        r4: property({ type: 'string', icon: "fa-tag" }),

        address1: property({ type: 'string', icon: "fa-tag" }),
        address2: property({ type: 'string', icon: "fa-tag" }),
        postalCode: property({ type: 'string', icon: "fa-tag" }),

        tel: property({ type: 'string', icon: "fa-tel" }),
        mobile: property({ type: 'string', icon: "fa-mobile" }),
        fax: property({ type: 'string', icon: "fa-fax" }),
        email: property({ type: 'string', icon: "fa-email" }),
        web: property({ type: 'string', icon: "fa-web" }),
        qq: property({ type: 'string', icon: "fa-qq" }),
        wechat: property({ type: 'string', icon: "fa-wechat" }),

    }

    constructor() {
        super();
        this.declare(ContactType.declaredProperty);
    }
}

export class Contact extends IdEntity<integer> {
    static TYPE = new ContactType();

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
