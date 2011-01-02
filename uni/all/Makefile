all: .symlinks

__list__: ..
	cd ..; eachdir -lrf debian/control | sed -e 's/.*\///' >all/__list__

.symlinks: __list__
	cd ..; c="$$PWD"; eachdir -lrf debian/control | while read d; do \
		d="$${d%/}"; \
		base="$${d##*/}"; \
		rel="$${d#$$c/}"; \
		ln -snf "../$$rel" "all/xjl-$${base%-xjl}"; \
	done
