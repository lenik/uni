include libauto/project.am

dist_bin_SCRIPTS = \
    ollama-list \
    ollama.tmux

dist_man_MANS = \
    ollama-list.1

dist_completion_DATA = \
    completion/ollama

dist_alias_DATA = \
    aliases/ollama.sh

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-debug:
	sudo ln -snf $$PWD/completion/ollama ${completiondir}/ollama
	sudo ln -snf $$PWD/ollama.tmux ${bindir}/ollama.tmux
	sudo ln -snf $$PWD/ollama-list.in ${bindir}/ollama-list

