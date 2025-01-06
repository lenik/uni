import fs from 'node:fs'
import { fileURLToPath, URL } from 'node:url'
import { resolve } from 'path'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import electron from 'vite-plugin-electron/simple'
import pkg from './package.json'

interface Args {
    command: string
}

const base = defineConfig(({ command }) => {
    fs.rmSync('dist-electron', { recursive: true, force: true });

    const isServe = command === 'serve';
    const isBuild = command === 'build';
    const sourcemap = isServe || !!process.env.VSCODE_DEBUG;

    let config: any = {
        build: {
            rollupOptions: {
                input: {
                    root: resolve(__dirname, 'index.html'),
                    src: resolve(__dirname, 'src/index.html'),
                }
            }
        },
        plugins: [
            vue(),
            vueJsx(),
            electron({
                main: {
                    // Shortcut of `build.lib.entry`
                    entry: 'electron/main/index.ts',
                    onstart({ startup }) {
                        if (process.env.VSCODE_DEBUG) {
                            console.log(/* For `.vscode/.debug.script.mjs` */'[startup] Electron App')
                        } else {
                            startup()
                        }
                    },
                    vite: {
                        build: {
                            sourcemap,
                            minify: isBuild,
                            outDir: 'dist-electron/main',
                            rollupOptions: {
                                // Some third-party Node.js libraries may not be built correctly by Vite, especially `C/C++` addons, 
                                // we can use `external` to exclude them to ensure they work correctly.
                                // Others need to put them in `dependencies` to ensure they are collected into `app.asar` after the app is built.
                                // Of course, this is not absolute, just this way is relatively simple. :)
                                external: Object.keys('dependencies' in pkg ? pkg.dependencies : {}),
                            },
                        },
                    },
                },
                preload: {
                    // Shortcut of `build.rollupOptions.input`.
                    // Preload scripts may contain Web assets, so use the `build.rollupOptions.input` instead `build.lib.entry`.
                    input: 'electron/preload/index.ts',
                    vite: {
                        build: {
                            sourcemap: sourcemap ? 'inline' : undefined, // #332
                            minify: isBuild,
                            outDir: 'dist-electron/preload',
                            rollupOptions: {
                                external: Object.keys('dependencies' in pkg ? pkg.dependencies : {}),
                            },
                        },
                    },
                },
                // Ployfill the Electron and Node.js API for Renderer process.
                // If you want use Node.js in Renderer process, the `nodeIntegration` needs to be enabled in the Main process.
                // See 👉 https://github.com/electron-vite/vite-plugin-electron-renderer
                renderer: {},
            }),
        ],
        resolve: {
            alias: {
            }
        },
        clearScreen: false,
    };

    config.server = {
        fs: {
            allow: ['.'],
            strict: false,
        },
    };

    if (process.env.VSCODE_DEBUG) {
        let url = new URL(pkg.debug.env.VITE_DEV_SERVER_URL);
        console.log('debug.env._URL', url);
        config.server = {
            host: url.hostname,
            port: +url.port,
        };
    } else {
        console.log('not VSCODE_DEBUG');
    }
    return config;
})

const build = defineConfig(({ command }) => {
    return {
        root: './',
        base: './',
        build: {
            target: command === 'serve' ? 'esnext' : 'es2015',
            // minify: command === 'serve' ? false : 'terser',
            outDir: 'dist',
            emptyOutDir: true,
        }
    }
})

// https://vitejs.dev/config/
export default defineConfig(env => ({
    ...base(env),
    //...build(env),
}))
