#!/usr/bin/env -S tmux-run

new -s OllamaTest -n 'Ollama Test' 'watch nvidia-smi'
splitw -v -l 10 -t 0 'gputop'
splitw -h -l 45 -t 1 'watch ollama ps'
selectw -t 0

