import AttachmentManifestTypeInfo from "./AttachmentManifestTypeInfo";

export class AttachmentManifestValidators {

    _type: AttachmentManifestTypeInfo;

    constructor(type: AttachmentManifestTypeInfo) {
        this._type = type;
    }

    get type(): AttachmentManifestTypeInfo {
        return this._type;
    }

}

export default AttachmentManifestValidators;
