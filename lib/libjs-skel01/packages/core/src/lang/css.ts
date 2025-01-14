import { rawInvokeStr } from "./scripting";

export const rgb = (red: any, green: any, blue: any) => rawInvokeStr('rgb', red, green, blue);
export const rgba = (red: any, green: any, blue: any, alpha: any) => rawInvokeStr('rgb', red, green, blue, alpha);
export const hsl = (hue: any, sat: any, lum: any) => rawInvokeStr('hsl', hue, sat, lum);
export const hsla = (hue: any, sat: any, lum: any, alpha: any) => rawInvokeStr('hsl', hue, sat, lum, alpha);
