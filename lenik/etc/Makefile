
%: %.m4
	if m4 -P -I$(LAM_KALA)/lib/m4 $*.m4 >"$(TEMP)/$@"; then \
        mv "$(TEMP)/$@" .; \
    fi
