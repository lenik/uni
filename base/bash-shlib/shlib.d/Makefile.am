include ../libauto/common.am

dist_shlib_DATA = \
    ai \
    ai.img \
    bashunit \
    char \
    cliboot \
    compat \
    date \
    de_alias \
    echo-fill \
    echo-raw \
    export-p \
    findabc \
    float \
    get_perm \
    htmlcolor \
    import \
    initdir \
    lam \
    load \
    log \
    overrides \
    path \
    rcs \
    term \
    time

ai.img: ai.i
	AI_KEYFILE= ${AI} -ct bridge $< >$@
