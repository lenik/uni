--\import lily.account

    create sequence <?= $Name ?>_seq start with 1000;
    create table <?= $Name ?>(
        id          int primary key default nextval('<?= $Name ?>_seq'),
--\mixin lily.mixin.LabelExVer
--\mixin lily.mixin.Acl_rw-r--r--
        extra       varchar(30) not null
    );

    create index <?= $Name ?>_label              on <?= $Name ?>(label);
    create index <?= $Name ?>_description        on <?= $Name ?>(description);
    create index <?= $Name ?>_lastmod            on <?= $Name ?>(lastmod desc);
    create index <?= $Name ?>_creation           on <?= $Name ?>(creation);
    create index <?= $Name ?>_uid                on <?= $Name ?>(uid);
    create index <?= $Name ?>_gid                on <?= $Name ?>(gid);
    create index <?= $Name ?>_acl                on <?= $Name ?>(acl);

    comment on table <?= $Name ?>                is '<?= $words ?>';
    comment on column <?= $Name ?>.label         is 'Label';
    comment on column <?= $Name ?>.description   is 'Description';
    comment on column <?= $Name ?>.icon          is 'Icon';
    comment on column <?= $Name ?>.priority      is 'Priority';
    comment on column <?= $Name ?>.flags         is 'Flags';
    comment on column <?= $Name ?>.state         is 'State';
    comment on column <?= $Name ?>.creation      is 'Creation Time';
    comment on column <?= $Name ?>.lastmod       is 'Last Modified Time';
    comment on column <?= $Name ?>.version       is 'Version';
    comment on column <?= $Name ?>.uid           is 'Owner User';
    comment on column <?= $Name ?>.gid           is 'Owner Group';
    comment on column <?= $Name ?>.mode          is 'Access Mode';
    comment on column <?= $Name ?>.acl           is 'Access Control List';
    comment on column <?= $Name ?>.extra         is 'Extra';
    
