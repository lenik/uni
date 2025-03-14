import AttachmentTypeInfo from "./AttachmentTypeInfo";

export class AttachmentValidators {

    _type: AttachmentTypeInfo;

    constructor(type: AttachmentTypeInfo) {
        this._type = type;
    }

    get type(): AttachmentTypeInfo {
        return this._type;
    }

}

export default AttachmentValidators;
