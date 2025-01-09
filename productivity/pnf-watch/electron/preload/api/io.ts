import type { Mode, ObjectEncodingOptions, OpenMode, PathLike, StatOptions } from "node:fs";
import { ipcRenderer } from "electron";
import { CreateReadStreamOptions, FileHandle } from "node:fs/promises";
import { Abortable } from "node:events";
import { Stats } from "node:original-fs";

export default {

    access: (path: PathLike, mode?: number): Promise<boolean> =>
        ipcRenderer.invoke('io:access', path, mode),

    open: (path: PathLike, flags?: string | number, mode?: Mode): Promise<FileHandle> =>
        ipcRenderer.invoke('io:open', path, flags, mode),

    readLines: (path: PathLike, options?: CreateReadStreamOptions): Promise<string[]> =>
        ipcRenderer.invoke('io:readLines', path, options),

    readDir: (dir: PathLike): Promise<string[]> =>
        ipcRenderer.invoke('io:readDir', dir),

    readFile: (
        path: PathLike | FileHandle,
        options?:
            | ({
                encoding?: null | undefined;
                flag?: OpenMode | undefined;
            } & Abortable)
            | null): Promise<Buffer> =>
        ipcRenderer.invoke('io:readFile', path, options),

    readFileString: (
        path: PathLike | FileHandle,
        options:
            | ({
                encoding: BufferEncoding;
                flag?: OpenMode | undefined;
            } & Abortable)
            | BufferEncoding): Promise<string> =>
        ipcRenderer.invoke('io:readFileString', path, options),

    readFile2: (
        path: PathLike | FileHandle,
        options?:
            | (
                & ObjectEncodingOptions
                & Abortable
                & {
                    flag?: OpenMode | undefined;
                }
            )
            | BufferEncoding
            | null) =>
        ipcRenderer.invoke('io:readFile2', path, options),

    stat: (path: PathLike, options?: StatOptions): Promise<Stats> =>
        ipcRenderer.invoke('io:stat', path, options),

    exists: (path: PathLike): Promise<boolean> =>
        ipcRenderer.invoke('io:exists', path),

    canRead: (path: PathLike): Promise<boolean> =>
        ipcRenderer.invoke('io:canRead', path),

    canWrite: (path: PathLike): Promise<boolean> =>
        ipcRenderer.invoke('io:canWrite', path),

    isFile: (path: PathLike): Promise<boolean> =>
        ipcRenderer.invoke('io:isFile', path),

    isDirectory: (path: PathLike): Promise<boolean> =>
        ipcRenderer.invoke('io:isDirectory', path),

};
