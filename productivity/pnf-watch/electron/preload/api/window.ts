import { ipcRenderer } from "electron"

export default {

    getTitle: async () => await ipcRenderer.invoke('getTitle'),

    setTitle: (title: string) => ipcRenderer.send('set-title', title),

};
