import { BrowserWindow, ipcMain } from "electron";

export default function () {

    ipcMain.handle('getTitle', (event) => {
        const win = BrowserWindow.fromWebContents(event.sender)!
        if (win == null) return;
        let title = win.getTitle();
        return title;
    });

    ipcMain.handle('setTitle', (event, title) => {
        const win = BrowserWindow.fromWebContents(event.sender)!
        if (win == null) return;
        win.setTitle(title);
    });

    ipcMain.on('set-title', (event, title) => {
        const win = BrowserWindow.fromWebContents(event.sender)!
        if (win == null) return;
        win.setTitle(title);
    });

}
