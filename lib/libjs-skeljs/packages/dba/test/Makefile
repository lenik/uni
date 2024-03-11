SHELL = /bin/bash

router.ts:
	shopt -s nullglob; \
    make-router \
        ./Index.vue \
        ../node_modules/@skeljs/core/src/ui/demo/Project.vue \
        DataTables/*.vue \
        input/*.vue \
        ../src/net/bodz/lily/schema/**/*Admin.vue \
        >$@

