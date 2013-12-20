include libauto/project.am

bin_SCRIPTS = \
    b43-tools/assembler/b43-asm \
    b43-tools/assembler/b43-asm.bin \
    b43-tools/disassembler/b43-dasm \
    b43-tools/fwcutter/b43-fwcutter \
    b43-tools/ssb_sprom/ssb-sprom

dist_man_MANS = \
    b43-tools/fwcutter/b43-fwcutter.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    b43-clean \
    ${dist_setup_SCRIPTS}

b43-tools/assembler/b43-asm: assembler
b43-tools/assembler/b43-asm.bin: assembler
b43-tools/disassembler/b43-dasm: disassembler
b43-tools/fwcutter/b43-fwcutter: fwcutter
b43-tools/ssb_sprom/ssb-sprom: ssb_sprom

assembler:
	make -C b43-tools/assembler

disassembler:
	make -C b43-tools/disassembler

fwcutter:
	make -C b43-tools/fwcutter

ssb_sprom:
	make -C b43-tools/ssb_sprom

b43-clean:
	cd b43-tools; \
	for d in *; do \
		if [ -d "$$d" ]; then \
			cd "$$d"; \
			make clean; \
			cd ..; \
		fi; \
	done
