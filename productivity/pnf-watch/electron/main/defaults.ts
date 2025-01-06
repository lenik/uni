import { app, BrowserWindow, shell, ipcMain } from 'electron';

import os from 'node:os'

// Disable GPU Acceleration for Windows 7
if (os.release().startsWith('6.1')) app.disableHardwareAcceleration()

// Set application name for Windows 10+ notifications
if (process.platform === 'win32') app.setAppUserModelId(app.getName())

if (!app.requestSingleInstanceLock()) {
    app.quit()
    process.exit(0)
}

export var defaultWindow: BrowserWindow | null = null;

export async function setupDefaultWindow(createFn: () => Promise<BrowserWindow>) {
    app.whenReady().then(async () => {
        defaultWindow = await createFn();
    });

    app.on('window-all-closed', () => {
        defaultWindow = null;
        if (process.platform !== 'darwin')
            app.quit();
    });

    app.on('second-instance', () => {
        if (defaultWindow) {
            // Focus on the main window if the user tried to open another
            if (defaultWindow.isMinimized())
                defaultWindow.restore();
            defaultWindow.focus();
        }
    });

    app.on('activate', async () => {
        const allWindows = BrowserWindow.getAllWindows();
        if (allWindows.length) {
            allWindows[0].focus();
        } else {
            defaultWindow = await createFn();
        }
    });
}
