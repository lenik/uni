import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

interface Args {
    command: string
}

const base = defineConfig({
    plugins: [
        vue(),
        vueJsx(),
    ],
    resolve: {
        alias: {
        }
    },
    server: {
        fs: {
            // Allow serving files from one level up to the project root
            allow: ['.',
                '/'],
        },
    }
})

const build = ({ command }: Args) => defineConfig({
    root: './src',
    base: './',
    build: {
        target: command === 'serve' ? 'esnext' : 'es2015',
        // minify: command === 'serve' ? false : 'terser',
        outDir: '../dist',
        emptyOutDir: true,
    }
})

// https://vitejs.dev/config/
export default ({ command }: Args) => defineConfig({
    ...base,
    ...build({ command }),
})
