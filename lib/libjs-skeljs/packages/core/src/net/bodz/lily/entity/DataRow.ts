import IAttachments, { getFirstImage } from './IAttachments';

interface IDataRow {
    [index: number]: any
}

export function getRowImage(row: IDataRow, indexOfIdColumn = 0, indexOfPropsColumn = 1, fallback = ""): string | null {
    let id = row[indexOfIdColumn];
    let props = row[indexOfPropsColumn] as IAttachments;
    return getFirstImage(id, props, fallback);
}
