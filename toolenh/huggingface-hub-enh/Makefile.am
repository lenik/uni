include libauto/project.am

dist_completion_DATA = \
    completion/huggingface-cli

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-debug:
	sudo ln -snf $$PWD/completion/huggingface-cli ${completiondir}/huggingface-cli

