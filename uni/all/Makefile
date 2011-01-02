all: .symlinks

__list__: ..
	cd ..; eachdir -rf debian/control pwd | sed -e 's/.*\///' >all/__list__

.symlinks: __list__
	cd ..; c="$$PWD"; eachdir -rf debian/control pwd | while read d; do \
		base="$${d##*/}"; \
		rel="$${d#$$c/}"; \
		ln -snf "../$$rel" "all/xjl-$${base%-xjl}"; \
	done
