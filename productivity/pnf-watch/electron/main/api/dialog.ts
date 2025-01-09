import { dialog, ipcMain } from "electron";

export default function () {

    ipcMain.handle('dialog:showOpenDialog', async (event: any, opts: any) => {
        let { canceled, filePaths } = await dialog.showOpenDialog(opts);
        if (canceled) return undefined;
        return filePaths
    });

    ipcMain.handle('dialog:openFile', async (event: any) => {
        let opts = {};
        let { canceled, filePaths } = await dialog.showOpenDialog(opts);
        if (canceled) return undefined;
        return filePaths
    });

}
