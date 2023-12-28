SHELL = /bin/bash

SCSS = sassc
SCSSFLAGS = -I /usr/share/javascript/zwk

templatedir = .template
modmodels = $(shell find -L -name 'index-model.js')
mod_DIRS = $(patsubst %/index-model.js, %, $(modmodels))

scss_SOURCES = $(shell find . -name '*.scss' \
    ! -name '.*' \
    ! -name '_*' \
    ! -path './.*/*' \
    ! -path './_*/*' \
    ! -path './lib*/*' \
    ! -path './node_modules/*' \
    )
scss_DIRS = $(dir $(scss_SOURCES))
css_OBJECTS = $(patsubst %.scss, %.css, $(scss_SOURCES))
OBJECTS = $(css_OBJECTS)

%.css: %.scss
	$(SCSS) $(SCSSFLAGS) "$<" "$@"

all: $(OBJECTS)

whattodo: 
	@echoli $(scss_SOURCES)

clean: 
	rm -f $(OBJECTS)

auto-build: all
	onchange $(patsubst %, -f %, $(scss_DIRS)) make all

work:
	setsid atom .
	mate-open index.html
	make auto-build

