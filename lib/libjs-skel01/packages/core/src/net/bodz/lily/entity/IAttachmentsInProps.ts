import IAttachmentMap from "./IAttachmentMap";

export interface IId {
    id: any
}

export interface IAttachmentsInProps extends IId {

    properties: IAttachmentMap;

}

export default IAttachmentsInProps;
