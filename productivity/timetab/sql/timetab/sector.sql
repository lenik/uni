--\import lily.account.user

    create sequence sector_seq start with 1000;
    create table "sector"(
        id          bigint primary key default nextval('sector_seq'),
--\mixin lily.mixin.Acl_rw-r--r--
--\mixin lily.mixin.LabelExVer
--\mixin lily.mixin.PropFiles

        seq         int not null,
        weight      int not null,
        -- ratio       float not null,
        abbr        varchar(30) not null
    );

    create index sector_label         on "sector"(label);
    create index sector_lastmod       on "sector"(lastmod desc);
    create index sector_state         on "sector"(state);
  --create index sector_props         on "sector" using gin(props);

    comment on table "sector" is 'Task Sector';
    comment on column "sector".seq is 'Sequence number';
    comment on column "sector".weight is 'Sector weight. Will be normalized by calc weight/total_weight';
    comment on column "sector".abbr is 'Abbreviated code';

    insert into "sector"
        (id,    uid,mode,   seq,    weight, abbr,   description) values
        (1,     0,  666,    1,      10,     'TK',   '工具支持'),
        (2,     0,  666,    2,      20,     'LANG', '编程语言学习'),
        (3,     0,  666,    3,      5,      'DEMO', '产品原型'),
        (4,     0,  666,    4,      12,     'GAM',  '游戏'),
        (5,     0,  666,    5,      20,     'CAD',  '建筑'),
        (6,     0,  666,    6,      20,     'MIS',  '企业管理软件'),
        (7,     0,  666,    7,      11,     'PM',   '项目管理'),
        (8,     0,  666,    8,      100,    'TAX',  '公司事项'),
        (9,     0,  666,    9,      8,      'DAT',  '数据接口'),
        (10,    0,  666,    10,     5,      'VIZ',  '可视化'),
        (11,    0,  666,    11,     10,     'PUB',  '出版物'),
        (12,    0,  666,    12,     12,     'CE',   '电子和硬件产品'),
        (13,    0,  666,    13,     20,     'SCI',  '科学计算，分析，统计'),
        (14,    0,  666,    14,     12,     'CV',   '视觉合成'),
        (15,    0,  666,    15,     11,     'FEM',  '女性主义'),
        (16,    0,  666,    16,     11,     'PFA',  '表演艺术'),
        (17,    0,  666,    17,     10,     'EDU',  '教育&个人成长');
