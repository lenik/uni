import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults, ConfigEnv } from 'vitest/config'
import viteConfig from './vite.config'

const command = 'serve';
const mode = 'unknown';
const env: ConfigEnv = { command, mode };

export default mergeConfig(
    viteConfig(env),
    defineConfig({
        test: {
            environment: 'jsdom',
            exclude: [...configDefaults.exclude, 'e2e/*'],
            root: fileURLToPath(new URL('./', import.meta.url))
        }
    })
)
