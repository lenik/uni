import AttachmentTypeInfo from "./AttachmentTypeInfo";

const TYPE = Symbol('type');

export class AttachmentValidators {

    constructor(type: AttachmentTypeInfo) {
        this[TYPE] = type;
    }

    get type(): AttachmentTypeInfo {
        return this[TYPE];
    }

}

export default AttachmentValidators;
