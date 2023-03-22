
all: .gitignore

.gitignore: .gitignore.in
	rm -f $@
	sed -e 's/ *#.*$$//' <$< >$@
	chmod -w $@

ChangeLog:
	gitcl | gitcl2 -t uni -r -m 20 -n 2 >ChangeLog

services.list:
	find -name services -exec find {} -type f \; | sed -e 's,.*/,,g' | sort -u >$@

features.list:
	find -name features -exec find {} -type f \; | sed -e 's,.*/,,g' | sort -u >$@

codesnipper-apkmerges: services.list features.list
	( \
	    sed -e "s|^|        merge 'META-INF/services/|" -e "s|\$$|'|" services.list; \
	    sed -e "s|^|        merge 'META-INF/features/|" -e "s|\$$|'|" features.list; \
	) | xsel -b

