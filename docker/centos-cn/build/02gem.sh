#!/bin/bash

function wget() {
    url="$1"
    base="${url##*/}"
    curl -o "$base" "$url"
}

cd /etc/yum
    sed -i.backup 's/^enabled=1/enabled=0/' pluginconf.d/fastestmirror.conf

cd /etc/yum.repos.d
    mkdir bak
    for f in \
            CentOS-Base.repo \
            epel.repo \
            epel-testing.repo \
            epel-7.repo \
            ; do
        if [ -f "$f" ]; then
            mv "$f" bak/
        fi
    done

    wget http://mirrors.aliyun.com/repo/Centos-7.repo
    wget http://mirrors.aliyun.com/repo/epel-7.repo

cd /tmp
    wget http://dl.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-8.noarch.rpm
    wget http://rpms.famillecollet.com/enterprise/remi-release-7.rpm
    rpm -Uvh epel-release-7*.rpm
    rpm -Uvh remi-release-7*.rpm

    yum clean all
    yum makecache

cd /root
    mkdir -p .pip
    touch .pip/pip.conf

    sed -i.backup \
        -r 's/^index-url\s*=\s*.+$/index-url = http:\/\/mirrors.aliyun.com\/pypi\/simple\//' \
        .pip/pip.conf

    # If file not changed, write contents back to pip.conf
        diff "~/.pip/pip.conf" "~/.pip/pip.conf.backup" &> /dev/null

    if [ $? -eq 0 ]; then
        cat > ~/.pip/pip.conf <<EOF
[global]
index-url = http://mirrors.aliyun.com/pypi/simple/
EOF
    fi

