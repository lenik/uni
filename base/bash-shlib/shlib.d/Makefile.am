include ../libauto/coolauto.am

dist_shlib_DATA = \
    bashunit \
    cliboot \
    cmds \
    export-p \
    findabc \
    float \
    get_perm \
    import \
    initdir \
    lam \
    load \
    log \
    overrides \
    path \
    rcs \
    string \
    time

%: %.in Makefile
	$(do_subst) <$< >$@
