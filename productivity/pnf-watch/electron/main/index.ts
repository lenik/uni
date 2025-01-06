import { app, BrowserWindow, dialog, ipcMain, Menu } from 'electron';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

import { setupDefaultWindow } from './defaults';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

process.env.APP_ROOT = path.join(__dirname, '../..');

export const MAIN_DIST = path.join(process.env.APP_ROOT, 'dist-electron');
export const RENDERER_DIST = path.join(process.env.APP_ROOT, 'dist');
export const VITE_DEV_SERVER_URL = process.env.VITE_DEV_SERVER_URL;

process.env.VITE_PUBLIC = VITE_DEV_SERVER_URL
    ? path.join(process.env.APP_ROOT, 'public')
    : RENDERER_DIST;

const preload = path.join(__dirname, '../preload/index.mjs');

const homeView = 'src/index.html';
const indexHtml = path.join(RENDERER_DIST, './' + homeView);

async function createWindow() {

    let mainWindow = new BrowserWindow({
        title: 'Main window',
        icon: path.join(process.env.VITE_PUBLIC!, 'icon.png'),

        autoHideMenuBar: true,
        // frame: false,
        // transparent: true,  // No shadow, transparent background

        webPreferences: {
            preload,
            // Warning: Enable nodeIntegration and disable contextIsolation is not secure in production
            // nodeIntegration: true,

            // Consider using contextBridge.exposeInMainWorld
            // Read more on https://www.electronjs.org/docs/latest/tutorial/context-isolation
            // contextIsolation: false,
        },
    });


    const isMac = process.platform === 'darwin'

    let view = mainWindow.webContents;
    let history = view.navigationHistory;

    const template: any = [
        // { role: 'appMenu' }
        ...(isMac
            ? [{
                label: app.name,
                submenu: [
                    { role: 'about' },
                    { type: 'separator' },
                    { role: 'services' },
                    { type: 'separator' },
                    { role: 'hide' },
                    { role: 'hideOthers' },
                    { role: 'unhide' },
                    { type: 'separator' },
                    { role: 'quit' }
                ]
            }]
            : []),
        // { role: 'fileMenu' }
        {
            label: 'File',
            submenu: [
                isMac ? { role: 'close' } : { role: 'quit' }
            ]
        },
        // { role: 'editMenu' }
        {
            label: 'Edit',
            submenu: [
                { role: 'undo' },
                { role: 'redo' },
                { type: 'separator' },
                { role: 'cut' },
                { role: 'copy' },
                { role: 'paste' },
                ...(isMac
                    ? [
                        { role: 'pasteAndMatchStyle' },
                        { role: 'delete' },
                        { role: 'selectAll' },
                        { type: 'separator' },
                        {
                            label: 'Speech',
                            submenu: [
                                { role: 'startSpeaking' },
                                { role: 'stopSpeaking' }
                            ]
                        }
                    ]
                    : [
                        { role: 'delete' },
                        { type: 'separator' },
                        { role: 'selectAll' }
                    ])
            ]
        },
        // { role: 'viewMenu' }
        {
            label: 'View',
            submenu: [

                {
                    label: 'Back',
                    accelerator: 'Alt+Left', // Optional: keyboard shortcut
                    click() {
                        if (history.canGoBack()) {
                            history.goBack();
                        }
                    }
                },
                {
                    label: 'Forward',
                    accelerator: 'Alt+Right', // Optional: keyboard shortcut
                    click() {
                        if (history.canGoForward()) {
                            history.goForward();
                        }
                    }
                },

                { role: 'reload' },
                { role: 'forceReload' },
                { role: 'toggleDevTools' },
                { type: 'separator' },
                { role: 'resetZoom' },
                { role: 'zoomIn' },
                { role: 'zoomOut' },
                { type: 'separator' },
                { role: 'togglefullscreen' }
            ]
        },
        // { role: 'windowMenu' }
        {
            label: 'Window',
            submenu: [
                { role: 'minimize' },
                { role: 'zoom' },
                ...(isMac
                    ? [
                        { type: 'separator' },
                        { role: 'front' },
                        { type: 'separator' },
                        { role: 'window' }
                    ]
                    : [
                        { role: 'close' }
                    ])
            ]
        },

        {
            role: 'help',
            submenu: [
                {
                    label: 'Learn More',
                    click: async () => {
                        const { shell } = require('electron')
                        await shell.openExternal('https://electronjs.org')
                    }
                }
            ]
        }
    ];

    let menu = Menu.buildFromTemplate(template);
    Menu.setApplicationMenu(menu);

    ipcMain.handle('nav:canGoBack', () => history.canGoBack());
    ipcMain.handle('nav:canGoForward', () => history.canGoForward());
    ipcMain.handle('nav:back', () => history.goBack());
    ipcMain.handle('nav:forward', () => { history.goForward() });

    ipcMain.handle('nav:getCurrentURL', () => view.getURL());
    ipcMain.handle('nav:getHistory', () => { return history.getAllEntries() });

    view.on('did-navigate', () => { view.send('nav:updated') });
    view.on('did-navigate-in-page', () => { view.send('nav:updated') });

    if (VITE_DEV_SERVER_URL) { // #298
        let url = VITE_DEV_SERVER_URL + homeView;
        console.log('serverUrl', url);
        mainWindow.loadURL(VITE_DEV_SERVER_URL + homeView);
        // win.webContents.openDevTools();
    } else {
        mainWindow.loadFile(indexHtml)
    }

    return mainWindow;
}

setupDefaultWindow(createWindow);

ipcMain.on('set-title', (event, title) => {
    const win = BrowserWindow.fromWebContents(event.sender)!
    if (win == null) return;
    win.setTitle(title);
});

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

ipcMain.handle('dialog:openFile', async () => {
    let opts = {};
    let { canceled, filePaths } = await dialog.showOpenDialog(opts);
    if (canceled) return undefined;
    return filePaths
});
