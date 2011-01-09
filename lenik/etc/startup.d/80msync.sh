# Synchronize Mirror Sites...

pushd /mirror >/dev/null
for site in *; do
    if [ ! -d "$site" ]; then continue; fi

    echo msync -d "$site"
    msync -d "$site" &

done
popd >/dev/null
