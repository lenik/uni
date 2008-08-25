
# fucking...
#
# The SHELLFLAGS isn't supported, yet. may be added in future.
# There's no other way to override $(SHELL) -c

SHELL=cmd
SHELLFLAGS=/c

default:
	$(SHELLFLAGS) jsub -Xx.bat -Ddelim="|" -Dfile-field=name -Scsv -Mgsp=.autofind.bat.gsp .autofind.csv
