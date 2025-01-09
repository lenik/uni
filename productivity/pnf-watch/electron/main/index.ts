import { app, BrowserWindow, dialog, ipcMain, Menu } from 'electron';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

import { setupDefaultWindow } from './defaults';
import { buildMenu } from './menu';
import dialogApi from './api/dialog';
import ioApi from './api/io';
import navApi from './api/nav';
import windowApi from './api/window';

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

    let menu = buildMenu(mainWindow);
    Menu.setApplicationMenu(menu);


    dialogApi();
    ioApi();
    navApi(mainWindow);
    windowApi();

    let view = mainWindow.webContents;
    view.on('did-navigate', () => { view.send('nav:updated') });
    view.on('did-navigate-in-page', () => { view.send('nav:updated') });

    if (VITE_DEV_SERVER_URL) { // #298
        let url = VITE_DEV_SERVER_URL + homeView;
        // console.log('serverUrl', url);
        mainWindow.loadURL(VITE_DEV_SERVER_URL + homeView);
        // win.webContents.openDevTools();
    } else {
        mainWindow.loadFile(indexHtml)
    }

    return mainWindow;
}

setupDefaultWindow(createWindow);
