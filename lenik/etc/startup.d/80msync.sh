# Synchronize Mirror Sites...

for root in /mnt/*/; do
    if [ -d "${root}mirror" ]; then
        pushd "${root}mirror" >/dev/null
        for site in */; do
            echo msync -d "$PWD> $site"
            msync -d "$site" &
        done
        popd >/dev/null
    fi
done
