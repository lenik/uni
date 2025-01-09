/// <reference types="vite/client" />

declare module '*.vue' {
    import type { DefineComponent } from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
}

interface Window {
    // expose in the `electron/preload/index.ts`
    // import('electron').IpcRenderer
    browserWindow: {
        async getTitle: () => string,
        async setTitle: (title: string) => void,
    },

    dialog: {
        async showOpenDialog: (opts: any) => any,
        async openFile: () => void,
    },

    electron: {
        async on: (...args: Parameters<typeof ipcRenderer.on>) => any,
        async off: (...args: Parameters<typeof ipcRenderer.off>) => any,
        async send: (...args: Parameters<typeof ipcRenderer.send>) => any,
        async invoke: (...args: Parameters<typeof ipcRenderer.invoke>) => any,
    },

    io: {

        async access: (path: PathLike, mode?: number) => Promise<boolean>,

        async open: (path: PathLike, flags?: string | number, mode?: Mode) => Promise<FileHandle>,

        async readLines: (path: PathLike, options?: CreateReadStreamOptions) => Promise<string[]>,

        async readDir: (dir: string) => Promise<string[]>,

        async readFile: (
            path: PathLike | FileHandle,
            options?:
                | ({
                    encoding?: null | undefined;
                    flag?: OpenMode | undefined;
                } & Abortable)
                | null) => Promise<Buffer>,

        async readFileString: (
            path: PathLike | FileHandle,
            options:
                | ({
                    encoding: BufferEncoding;
                    flag?: OpenMode | undefined;
                } & Abortable)
                | BufferEncoding) => string,

        async readFile2: (
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
                | null) => Promise<Buffer | string>,


        async stat: (path: PathLike, options?: StatOptions) => Promise<Stats | undefined>,

        async exists: (path: PathLike) => Promise<boolean>,
        async canRead: (path: PathLike) => Promise<boolean>,
        async canWrite: (path: PathLike) => Promise<boolean>,
        async isFile: (path: PathLike) => Promise<boolean>,
        async isDirectory: (path: PathLike) => Promise<boolean>,
    },

    nav: any,
}
