
all: .gitignore

.gitignore: .gitignore.in
	rm -f $@
	sed -e 's/ *#.*$$//' <$< >$@
	chmod -w $@

ChangeLog:
	gitcl | gitcl2 -t uni -r -m 20 -n 2 >ChangeLog

