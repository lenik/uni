import TypeInfo from '../../../../lang/TypeInfo';
import { INT, STRING, UNDEFINED } from '../../../../lang/baseinfo';
import { JSON_VARIANT } from '../../../../lang/bas-info';
import AttachmentManifest, { $AttachmentManifest } from './AttachmentManifest';
import AttachmentManifestValidators from './AttachmentManifestValidators';

export class AttachmentManifestTypeInfo extends TypeInfo<AttachmentManifest> {

    readonly validators = new AttachmentManifestValidators(this);

    constructor() {
        super();
    }

    get name() { return "net.bodz.lily.entity.AttachmentManifest"; }
    get icon() { return "fas-paperclip"; }
    get label() { return "Attachment Manifest"; }
    get description() { return "Describe attachments contained and their thumbnails, previews, screenshots, etc."; }

    override create() {
        return new AttachmentManifest();
    }

    override parse(s: string) {
        let jv = JSON.parse(s);
        return this.fromJson(jv);
    }

    override fromJson(jv: any) {
        let a = new AttachmentManifest();
        a.jsonIn(jv);
        return a;
    }

    override toJson(val: AttachmentManifest): any {
        let a = {} as $AttachmentManifest;
        return val.jsonOut(a);
    }

    // override preamble() {
    //     super.preamble();
    //     this.declare({
    //         data: property({ type: JSON_VARIANT, icon: 'far-copy' }),
    //         thumb: property({ type: JSON_VARIANT, icon: 'far-image' }),
    //         preview: property({ type: JSON_VARIANT, icon: 'fab-youtube' }),
    //         screen: property({ type: JSON_VARIANT, icon: 'far-film' }),
    //     });
    // }

    static readonly INSTANCE = new AttachmentManifestTypeInfo();

}

export default AttachmentManifestTypeInfo;
export const AttachmentManifest_TYPE = AttachmentManifestTypeInfo.INSTANCE;
