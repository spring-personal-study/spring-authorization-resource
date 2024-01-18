-- create table into h2 for test
-- emm.tb_device definition

CREATE TABLE `TB_DEVICE`
(
    `DEVICE_ID`    bigint AUTO_INCREMENT PRIMARY KEY,
    `DEVICE_CD`    varchar(30) NULL UNIQUE,
    `SN`           varchar(30) NULL UNIQUE,
    `DEVICE_NM`    varchar(50) NULL,
    `PLATFORM_ID`  int         NULL,
    `DEVICE_DESC`  varchar(50) NULL,
    `STATUS`       varchar(15) NULL,
    `ENROLL_DT`    datetime    NULL,
    `USER_ID`      int         NULL,
    `IN_WARRANTY`  char(1)     NULL,
    `EXPIRED_DATE` varchar(10) NULL,
    `USE_YN`       char(1)     NULL DEFAULT 'Y',
    `CREATE_DT`    datetime(6) NULL,
    `CREATE_ID`    varchar(50) NULL,
    `UPDATE_DT`    datetime(6) NULL,
    `UPDATE_ID`    varchar(50) NULL
);

-- emm.tb_device_detail definition

CREATE TABLE `tb_device_detail`
(
    `ID`                bigint        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `DEVICE_ID`         bigint        NULL,
    `SN`                varchar(30)   NULL,
    `IMEI`              varchar(45)   NULL,
    `IMSI`              varchar(45)   NULL,
    `MAC_ADDRESS`       varchar(45)   NULL,
    `MODEL_NM`          varchar(20)   NULL,
    `VENDOR`            varchar(20)   NULL,
    `OS_VER`            varchar(20)   NULL,
    `OS_BUILD_DATE`     varchar(20)   NULL,
    `OS_BUILD_NUMBER`   varchar(20)   NULL,
    `OS_BUILD_DATE_STR` varchar(30)   NULL,
    `FCM_TOKEN`         varchar(1000) NULL,
    `UPDATE_DT`         datetime(6)   NULL
);

-- emm.tb_user definition

CREATE TABLE `tb_user`
(
    `USER_ID`    int          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `ID`         varchar(20)  NOT NULL UNIQUE,
    `PASSWORD`   varchar(500) NULL,
    `EMAIL`      varchar(50)  NULL,
    `COMPANY_ID` int          NULL,
    `GROUP_ID`   smallint     NULL,
    `USER_NM`    varchar(50)  NULL,
    `USE_YN`     char(1)      NULL,
    `CREATE_DT`  datetime(6)  NULL,
    `CREATE_ID`  varchar(50)  NULL,
    `UPDATE_DT`  datetime(6)  NULL,
    `UPDATE_ID`  varchar(50)  NULL
);