import { ipcRenderer } from "electron";

export default {

    showOpenDialog: async (opts: any) => ipcRenderer.invoke('dialog:showOpenDialog', opts),

    openFile: async () => ipcRenderer.invoke('dialog:openFile'),

};
