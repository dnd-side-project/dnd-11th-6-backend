drop table if exists simple_snap;
drop table if exists meeting_mission_snap;
drop table if exists random_mission_snap;
drop table if exists mission_participant;
drop table if exists snap;
drop table if exists participant;
drop table if exists mission;
drop table if exists random_mission;
drop table if exists meeting;

create table meeting (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    end_date datetime(6) not null,
    start_date datetime(6) not null,
    updated_at datetime(6) not null,
    description varchar(255),
    leader_auth_key varchar(255) not null,
    meeting_link varchar(255) not null,
    name varchar(255) not null,
    password varchar(255) not null,
    symbol_color varchar(255) not null,
    thumbnail_url varchar(255),
    primary key (id)
) engine=InnoDB;

create table participant (
    id bigint not null auto_increment,
    shoot_count integer not null,
    created_at datetime(6) not null,
    meeting_id bigint,
    nickname varchar(8) not null,
    updated_at datetime(6) not null,
    role varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table mission (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    meeting_id bigint not null,
    updated_at datetime(6) not null,
    content varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table random_mission (
    id integer not null auto_increment,
    content varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table mission_participant (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    mission_id bigint not null,
    participant_id bigint not null,
    updated_at datetime(6) not null,
    primary key (id)
) engine=InnoDB;

create table snap (
    created_at datetime(6) not null,
    id bigint not null auto_increment,
    meeting_id bigint not null,
    participant_id bigint not null,
    shoot_date datetime(6) not null,
    updated_at datetime(6) not null,
    dtype varchar(31) not null,
    snap_url varchar(255),
    primary key (id)
) engine=InnoDB;

create table simple_snap (
    id bigint not null,
    primary key (id)
) engine=InnoDB;

create table meeting_mission_snap (
    id bigint not null,
    mission_id bigint not null,
    primary key (id)
) engine=InnoDB;

create table random_mission_snap (
    random_mission_id integer,
    id bigint not null,
    primary key (id)
) engine=InnoDB;

-- 외래 키 제약 조건 추가
alter table participant
    add constraint fk_participant_meeting
        foreign key (meeting_id)
            references meeting(id);

alter table mission
    add constraint fk_mission_meeting
        foreign key (meeting_id)
            references meeting(id);

alter table mission_participant
    add constraint fk_mission_participant_mission
        foreign key (mission_id)
            references mission(id);

alter table mission_participant
    add constraint fk_mission_participant_participant
        foreign key (participant_id)
            references participant(id);

alter table snap
    add constraint fk_snap_meeting
        foreign key (meeting_id)
            references meeting(id);

alter table snap
    add constraint fk_snap_participant
        foreign key (participant_id)
            references participant(id);

alter table simple_snap
    add constraint fk_simple_snap_snap
        foreign key (id)
            references snap(id);

alter table meeting_mission_snap
    add constraint fk_meeting_mission_snap_mission
        foreign key (mission_id)
            references mission(id);

alter table meeting_mission_snap
    add constraint fk_meeting_mission_snap_snap
        foreign key (id)
            references snap(id);

alter table random_mission_snap
    add constraint fk_random_mission_snap_random_mission
        foreign key (random_mission_id)
            references random_mission(id);

alter table random_mission_snap
    add constraint fk_random_mission_snap_snap
        foreign key (id)
            references snap(id);