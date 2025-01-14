import { rawInvokeStr, wsRawInvokeStr, wsSepStr } from "./scripting";

export const viewBox = (...args: (any)[]) => wsSepStr('', ...args);
export const rotate = (...args: (any)[]) => wsRawInvokeStr('rotate', ...args);
export const translate = (...args: (any)[]) => wsRawInvokeStr('translate', ...args);
export const scale = (...args: (any)[]) => wsRawInvokeStr('scale', ...args); 
