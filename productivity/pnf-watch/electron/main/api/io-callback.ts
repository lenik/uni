import { ipcMain, IpcMainInvokeEvent } from "electron";
import fs, { BigIntStats, PathLike, StatOptions, Stats } from "node:original-fs";

class FileUtils {

    _stat(
        path: PathLike,
        options: StatOptions | undefined,
        callback: (err: NodeJS.ErrnoException | null, stats: Stats | BigIntStats) => void,
    ) {
        return fs.stat(path, options, callback);
    }

    _stat2(path: PathLike, callback: (err: NodeJS.ErrnoException | null, stats: Stats) => void,) {
        return fs.stat(path, callback);
    }

    _statI(path: PathLike,
        options:
            | (StatOptions & {
                bigint?: false | undefined;
            })
            | undefined,
        callback: (err: NodeJS.ErrnoException | null, stats: Stats) => void,
    ) {
        return fs.stat(path, options, callback);
    }

    _statL(
        path: PathLike,
        options: StatOptions & {
            bigint: true;
        },
        callback: (err: NodeJS.ErrnoException | null, stats: BigIntStats) => void,
    ) {
        return fs.stat(path, options, callback);
    }

}
