import { contextBridge, ipcRenderer } from 'electron';
// import './loading';
import dialogApi from './api/dialog';
import electronApi from './api/electron';
import ioApi from './api/io';
import windowApi from './api/window';

contextBridge.exposeInMainWorld('versions', process.versions);

contextBridge.exposeInMainWorld('dialog', dialogApi);
contextBridge.exposeInMainWorld('io', ioApi);
contextBridge.exposeInMainWorld('electron', electronApi);
contextBridge.exposeInMainWorld('browserWindow', windowApi);
