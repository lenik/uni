
BASH=$(shell which bash)

all: bash.exe

bash.exe: $(BASH)
	cp "$(BASH)" $@
	ldd "/usr/bin/bash" | cut -d\  -f3 | while read REQLIB; do \
		cp "$$REQLIB" .; \
	done

clean:
	rm bash
	ldd "/usr/bin/bash" | cut -d\  -f3 | while read REQLIB; do \
		rm "./$${REQLIB##*/}"; \
	done
