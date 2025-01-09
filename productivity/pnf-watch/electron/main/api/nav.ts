import { BrowserWindow, ipcMain } from "electron";

export default function (window: BrowserWindow) {
    let view = window.webContents;
    let history = view.navigationHistory;

    ipcMain.handle('nav:canGoBack', () => history.canGoBack());
    ipcMain.handle('nav:canGoForward', () => history.canGoForward());
    ipcMain.handle('nav:back', () => history.goBack());
    ipcMain.handle('nav:forward', () => { history.goForward() });

    ipcMain.handle('nav:getCurrentURL', () => view.getURL());
    ipcMain.handle('nav:getHistory', () => { return history.getAllEntries() });

}
