create table if not exists company
(
    company_id      int auto_increment
        primary key,
    company_name    varchar(100) not null,
    company_address varchar(255) null
);

create table if not exists admin
(
    id         int auto_increment comment '主键ID'
        primary key,
    username   varchar(255) null comment '账号',
    password   varchar(255) null comment '密码',
    name       varchar(255) null comment '姓名',
    avatar     varchar(255) null comment '头像',
    role       varchar(255) null comment '角色',
    phone      varchar(255) null comment '电话',
    email      varchar(255) null comment '邮箱',
    company_id int          null,
    constraint fk_company
        foreign key (company_id) references company (company_id),
    constraint fk_company1
        foreign key (company_id) references company (company_id)
)
    comment '管理员表' collate = utf8mb4_unicode_ci
                       row_format = DYNAMIC;

create table if not exists department
(
    id         int auto_increment
        primary key,
    name       varchar(255) not null comment '部门名称',
    company_id int          null,
    constraint department__fk
        foreign key (company_id) references company (company_id)
);

create table if not exists doctor
(
    id          int auto_increment comment '主键ID'
        primary key,
    username    varchar(255) null comment '账号',
    password    varchar(255) null comment '密码',
    name        varchar(255) null comment '姓名',
    avatar      varchar(255) null comment '头像',
    role        varchar(255) null comment '角色',
    seniority   int          null comment '工龄',
    content     text         null comment '简介',
    phone       varchar(255) null comment '电话',
    email       varchar(255) null comment '邮箱',
    code        varchar(255) null comment '身份证',
    certificate varchar(255) null comment '资格证',
    status      varchar(255) null comment '审批状态',
    company_id  int          null,
    constraint doctor_company__fk
        foreign key (company_id) references company (company_id)
)
    comment '心理医生表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists employee
(
    id            int auto_increment comment '主键ID'
        primary key,
    username      varchar(255) null comment '账号',
    password      varchar(255) null comment '密码',
    name          varchar(255) null comment '姓名',
    role          varchar(255) null comment '角色',
    phone         varchar(255) null comment '电话',
    email         varchar(255) null comment '邮箱',
    avatar        varchar(255) null comment '头像',
    department_id int          null comment '用户部门',
    constraint employee_department__fk
        foreign key (department_id) references department (id)
)
    comment '用户信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists chat_log
(
    id             varchar(64) not null comment 'ID'
        primary key,
    user_id        int         null comment '用户ID',
    title          varchar(64) null comment '会话名称',
    conversations  text        null comment '对话明细',
    heart_analysis text        null,
    constraint chat_log_employee__fk
        foreign key (user_id) references employee (id)
);

create table if not exists diagnosis
(
    diagnosis_id      int auto_increment comment '诊断表id'
        primary key,
    doctor_id         int                                 not null comment '医生id',
    symptoms          varchar(255)                        null comment '症状描述',
    patient_id        int                                 not null comment '病人（员工）id',
    diagnosis_details varchar(255)                        null comment '诊断详情',
    health_score      int                                 null comment '健康打分',
    diagnosis_date    timestamp default CURRENT_TIMESTAMP null,
    constraint diagnosis_ibfk_1
        foreign key (doctor_id) references doctor (id),
    constraint diagnosis_ibfk_2
        foreign key (patient_id) references employee (id)
)
    comment '诊断表';

create index doctor_id
    on diagnosis (doctor_id);

create index patient_id
    on diagnosis (patient_id);

create table if not exists feedback
(
    id            int auto_increment comment '主键ID'
        primary key,
    user_id       int          null comment '用户ID',
    question      text         null comment '反馈问题',
    content       text         null comment '用户想法',
    time          varchar(255) null comment '反馈时间',
    reply_content text         null comment '回复内容',
    reply_id      int          null,
    reply_time    varchar(255) null comment '回复时间',
    status        varchar(255) null comment '反馈状态',
    type          varchar(20)  null comment '反馈类型',
    urgency       int          null comment '紧急程度',
    image_urls    text         null comment '图片URL列表',
    constraint feedback_admin__fk
        foreign key (reply_id) references admin (id),
    constraint feedback_employee__fk
        foreign key (user_id) references employee (id)
)
    comment '反馈建议表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists notice
(
    id       int auto_increment comment '主键ID'
        primary key,
    title    varchar(255) null comment '公告标题',
    content  text         null comment '公告内容',
    time     varchar(255) null comment '发布时间',
    admin_id int          null,
    constraint notice_admin__fk
        foreign key (admin_id) references admin (id)
)
    comment '系统公告表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists propagate_tag
(
    id         int auto_increment,
    tag        varchar(255) null,
    company_id int          null,
    constraint propagate_tag_pk
        unique (id),
    constraint propagate_tag_company__fk
        foreign key (company_id) references company (company_id)
);

create table if not exists propagate
(
    id        int auto_increment comment '主键ID'
        primary key,
    doctor_id int           null comment '医生ID',
    title     varchar(255)  null comment '宣传标题',
    img       varchar(255)  null comment '宣传封面',
    content   longtext      null comment '宣传内容',
    time      varchar(255)  null comment '发布时间',
    num       int default 0 null comment '浏览量',
    status    varchar(255)  null comment '文章审核状态',
    tag_id    int           null,
    constraint propagate_doctor__fk
        foreign key (doctor_id) references doctor (id),
    constraint propagate_propagate_tag__fk
        foreign key (tag_id) references propagate_tag (id)
)
    comment '健康宣传表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists reservation
(
    id          int auto_increment comment '主键ID'
        primary key,
    employee_id int          null comment '用户ID',
    doctor_id   int          null comment '医生ID',
    start       varchar(255) null comment '开始时间',
    end         varchar(255) null comment '结束时间',
    question    text         null comment '问题描述',
    time        varchar(255) null comment '申请时间',
    status      varchar(255) null comment '审批状态',
    reason      varchar(255) null comment '拒绝理由',
    constraint reservation_doctor__fk
        foreign key (doctor_id) references doctor (id),
    constraint reservation_employee__fk
        foreign key (employee_id) references employee (id)
)
    comment '预约信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists sideshow
(
    id           int auto_increment comment '主键ID'
        primary key,
    img          varchar(255) null comment '封面',
    propagate_id int          null comment '健康宣传ID',
    constraint sideshow_propagate__fk
        foreign key (propagate_id) references propagate (id)
)
    comment '轮播图信息表' collate = utf8mb4_unicode_ci
                           row_format = DYNAMIC;

create table if not exists type
(
    id         int auto_increment comment '主键ID'
        primary key,
    title      varchar(255) null comment '分类标题',
    company_id int          null,
    constraint type_company__fk
        foreign key (company_id) references company (company_id)
)
    comment '心理分类表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists test_paper
(
    id        int auto_increment comment '主键ID'
        primary key,
    title     varchar(255)  null comment '试卷名称',
    img       varchar(255)  null comment '试卷封面',
    content   text          null comment '试卷介绍',
    type_id   int           null comment '分类ID',
    doctor_id int           null comment '医生ID',
    num       int           null comment '题目数量',
    score     int           null comment '试卷总分',
    ids       varchar(255)  null comment '所有题目ID',
    status    varchar(255)  null comment '试卷状态',
    test_num  int default 0 null comment '测试人数',
    time      varchar(255)  null comment '创建时间',
    a_range   varchar(255)  null comment '底部区间',
    b_range   varchar(255)  null comment '中部区间',
    c_range   varchar(255)  null comment '顶部区间',
    a_answer  text          null comment '底部解答',
    b_answer  text          null comment '中部解答',
    c_answer  text          null comment '顶部解答',
    constraint test_paper_doctor__fk
        foreign key (doctor_id) references doctor (id),
    constraint test_paper_type__fk
        foreign key (type_id) references type (id)
)
    comment '试卷信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists test_record
(
    id            int auto_increment comment '主键ID'
        primary key,
    test_paper_id int          null comment '测试卷ID',
    user_id       int          null comment '用户ID',
    doctor_id     int          null comment '医生ID',
    score         int          null comment '分数',
    result        text         null comment '测试结果',
    time          varchar(255) null comment '测试时间',
    constraint test_record_doctor__fk
        foreign key (doctor_id) references doctor (id),
    constraint test_record_employee__fk
        foreign key (user_id) references employee (id),
    constraint test_record_test_paper__fk
        foreign key (test_paper_id) references test_paper (id)
)
    comment '测试记录表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create table if not exists topic
(
    id         int auto_increment comment '主键ID'
        primary key,
    title      varchar(255) null comment '题目名称',
    type_id    int          null comment '分类ID',
    a_name     varchar(255) null comment '选项A',
    a_score    int          null comment '选项A分数',
    b_name     varchar(255) null comment '选项B',
    b_score    int          null comment '选项B分数',
    c_name     varchar(255) null comment '选项C',
    c_score    int          null comment '选项C分数',
    d_name     varchar(255) null comment '选项D',
    d_score    int          null comment '选项D分数',
    score      int          null comment '最高分数',
    company_id int          null,
    constraint topic_company_fk
        foreign key (company_id) references company (company_id),
    constraint topic_type__fk
        foreign key (type_id) references type (id)
)
    comment '题目信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;


