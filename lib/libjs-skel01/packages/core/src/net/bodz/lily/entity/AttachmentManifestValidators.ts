import AttachmentManifestTypeInfo from "./AttachmentManifestTypeInfo";

const TYPE = Symbol('type');

export class AttachmentManifestValidators {

    constructor(type: AttachmentManifestTypeInfo) {
        this[TYPE] = type;
    }

    get type() {
        return this[TYPE];
    }

}

export default AttachmentManifestValidators;
