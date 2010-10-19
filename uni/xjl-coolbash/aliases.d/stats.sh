alias hist="HISTTIMEFORMAT= history \
    | awk '{ \$1=\"\"; print \$0 }' \
    | sort | uniq -c | sort -n | grep -v '^ *[1-9] '"
alias hist1="HISTTIMEFORMAT= history \
    | awk '{ print \$2 }' \
    | sort | uniq -c | sort -n | grep -v '^ *[1-9] '"
alias hist2="HISTTIMEFORMAT= history \
    | awk '{ print \$2, \$3 }' \
    | sort | uniq -c | sort -n | grep -v '^ *[1-9] '"
