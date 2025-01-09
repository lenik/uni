import { ipcMain, IpcMainInvokeEvent } from "electron";
import { Abortable } from "node:events";
import fs0, { Mode, ObjectEncodingOptions, OpenMode, PathLike, StatOptions, Stats } from "node:original-fs";
import fs, { CreateReadStreamOptions, FileHandle } from "node:fs/promises";

class FileUtils {

    async access(path: PathLike, mode?: number) {
        try {
            await fs.access(path, mode);
            return true;
        } catch (err) {
            return false;
        }
    }

    open(path: PathLike, flags?: string | number, mode?: Mode) {
        return fs.open(path, flags, mode);
    }

    async readLines(path: PathLike, options?: CreateReadStreamOptions) {
        let fd = await fs.open(path);
        let lines: string[] = [];
        for await (let line of fd.readLines(options))
            lines.push(line);
        await fd.close();
        return lines;
    }

    readDir(dir: PathLike) {
        return fs.readdir(dir);
    }

    readFile(
        path: PathLike | FileHandle,
        options?:
            | ({
                encoding?: null | undefined;
                flag?: OpenMode | undefined;
            } & Abortable)
            | null): Promise<Buffer> {
        return fs.readFile(path, options);
    }

    readFileString(
        path: PathLike | FileHandle,
        options:
            | ({
                encoding: BufferEncoding;
                flag?: OpenMode | undefined;
            } & Abortable)
            | BufferEncoding): Promise<string> {
        return fs.readFile(path, options);
    }

    readFile2(
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
            | null): Promise<string | Buffer> {
        return fs.readFile(path, options);
    }

    stat(path: PathLike, options: StatOptions) {
        return fs.stat(path, options);
    }

    async exists(path: PathLike) {
        try {
            await fs.stat(path);
            return true;
        } catch (err) {
            return false;
        }
    }

    async canRead(path: PathLike) {
        try {
            await fs.access(path, fs.constants.R_OK);
            return true;
        } catch (err) {
            return false;
        }
    }

    async canWrite(path: PathLike): Promise<boolean> {
        try {
            await fs.access(path, fs.constants.W_OK);
            return true;
        } catch (err) {
            return false;
        }
    }

    async isFile(path: PathLike): Promise<boolean> {
        try {
            let st = await fs.stat(path);
            return st.isFile();
        } catch (err) {
            return false;
        }
    }

    async isDirectory(path: PathLike): Promise<boolean> {
        try {
            let st = await fs.stat(path);
            return st.isDirectory();
        } catch (err) {
            return false;
        }
    }

}

export default function () {

    let a: any = new FileUtils();
    let pds = Object.getOwnPropertyDescriptors(FileUtils.prototype);
    for (let name in pds) {
        switch (name) {
            case 'constructor':
                break;
            default:
                let fn = a[name];
                ipcMain.handle('io:' + name,
                    (event: IpcMainInvokeEvent, ...args: any[]) => {
                        return fn.apply(a, args);
                    }
                );
        }
    }

}
