import IAttachment, { getItemImage } from "./IAttachment";

export interface IAttachmentMap {

    // getAttachmentGroup(groupKey: string): IAttachment[]

    // setAttachmentGroup(groupKey: string, attachments: IAttachment[]);

    [groupKey: string]: IAttachment[]

}

export function getFirstImage(id: any, attachments?: IAttachmentMap, fallback = ""): string | null {
    if (id == null || attachments == null) return fallback;
    var images = attachments.images;
    if (images == null || images.length == 0) return fallback;
    var first = images[0];
    var href = getItemImage(first, id);
    return href;
}

export default IAttachmentMap;
