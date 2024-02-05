CREATE TABLE `TB_COMPANY`
(
    `COMPANY_ID`   int PRIMARY KEY AUTO_INCREMENT,
    `COMPANY_NM`   varchar(50)  NOT NULL,
    `COMPANY_DESC` varchar(300) NOT NULL,
    `ADMIN_YN`     char(1)      NOT NULL,
    `LICENSE_ID`   int          NULL,
    `USE_YN`       char(1)      NOT NULL DEFAULT 'Y',
    `CREATE_DT`    datetime(6)  NOT NULL,
    `CREATE_ID`    varchar(50)  NOT NULL,
    `UPDATE_DT`    datetime(6)  NOT NULL,
    `UPDATE_ID`    varchar(50)  NOT NULL
);

CREATE TABLE `TB_PLATFORM`
(
    `PLATFORM_ID` int PRIMARY KEY AUTO_INCREMENT,
    `PLATFORM_NM` varchar(50) NULL,
    `USE_YN`      char(1)     NOT NULL DEFAULT 'Y',
    `CREATE_DT`   datetime(6) NOT NULL,
    `CREATE_ID`   varchar(50) NOT NULL,
    `UPDATE_DT`   datetime(6) NOT NULL,
    `UPDATE_ID`   varchar(50) NOT NULL
);

CREATE TABLE `TB_USER`
(
    `USER_ID`    int PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE `TB_DEVICE`
(
    `DEVICE_ID`    bigint PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE `TB_FOTA_CAMPAIGN`
(
    `CAMPAIGN_ID`   int PRIMARY KEY AUTO_INCREMENT,
    `CAMPAIGN_NM`   varchar(100)     NOT NULL,
    `CAMPAIGN_DESC` varchar(250)     NOT NULL,
    `PLATFORM_ID`   int              NOT NULL,
    `START_DT`      varchar(8)       NULL,
    `START_TM`      varchar(5)       NOT NULL,
    `END_DT`        varchar(8)       NULL,
    `END_TM`        varchar(5)       NOT NULL,
    `P2P_MODE_YN`   char(1)          NOT NULL DEFAULT 'N',
    `ASK_USER_YN`   char(1)          NOT NULL DEFAULT 'N',
    `LTE_USE_YN`    char(1)          NOT NULL DEFAULT 'N',
    `STATUS`        varchar(10)      NOT NULL,
    `COMPANY_ID`    tinyint unsigned NULL,
    `USE_YN`        char(1)          NOT NULL DEFAULT 'Y',
    `CREATE_DT`     datetime(6)      NOT NULL,
    `CREATE_ID`     varchar(50)      NOT NULL,
    `UPDATE_DT`     datetime(6)      NOT NULL,
    `UPDATE_ID`     varchar(50)      NOT NULL
);

CREATE TABLE `TB_SUPPORT_MODEL`
(
    `MODEL_ID`    int PRIMARY KEY AUTO_INCREMENT,
    `PLATFORM_ID` tinyint     NULL,
    `MODEL_NM`    varchar(50) NULL,
    `USE_YN`      char(1)     NOT NULL DEFAULT 'Y',
    `CREATE_DT`   datetime(6) NOT NULL,
    `CREATE_ID`   varchar(50) NOT NULL,
    `UPDATE_DT`   datetime(6) NOT NULL,
    `UPDATE_ID`   varchar(50) NOT NULL
);

CREATE TABLE `TB_DEVICE_DETAIL`
(
    `ID`                bigint        PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_ID`         bigint        NOT NULL,
    `SN`                varchar(30)   NOT NULL,
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
    `UPDATE_DT`         datetime(6)   NOT NULL,
    KEY `FK_TB_DEVICE_TO_TB_DEVICE_DETAIL` (`DEVICE_ID`),
    CONSTRAINT `FK_TB_DEVICE_TO_TB_DEVICE_DETAIL` FOREIGN KEY (`DEVICE_ID`) REFERENCES `TB_DEVICE` (`DEVICE_ID`)
);

CREATE TABLE `TB_DEVICE_GROUP`
(
    `DEVICE_GRP_ID`   int          PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_GRP_CD`   varchar(20)  NOT NULL,
    `DEVICE_GRP_NM`   varchar(60)  NULL,
    `DEVICE_GRP_DESC` varchar(300) NULL,
    `COMPANY_ID`      int          NOT NULL,
    `USE_YN`          char(1)      NOT NULL DEFAULT 'Y',
    `CREATE_DT`       datetime(6)  NOT NULL,
    `CREATE_ID`       varchar(50)  NOT NULL,
    `UPDATE_DT`       datetime(6)  NOT NULL,
    `UPDATE_ID`       varchar(50)  NOT NULL,
    KEY `FK_TB_COMPANY_TO_TB_DEVICE_GROUP` (`COMPANY_ID`),
    CONSTRAINT `FK_TB_COMPANY_TO_TB_DEVICE_GROUP` FOREIGN KEY (`COMPANY_ID`) REFERENCES `TB_COMPANY` (`COMPANY_ID`)
);

CREATE TABLE `TB_DEVICE_GROUP_MAP`
(
    `DEVICE_GROUP_MAP_ID` bigint PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_ID`           bigint NOT NULL,
    `DEVICE_GRP_ID`       int    NOT NULL,
    KEY `DEVICE_ID` (`DEVICE_ID`),
    KEY `DEVICE_GRP_ID` (`DEVICE_GRP_ID`),
    CONSTRAINT `tb_device_group_map_ibfk_1` FOREIGN KEY (`DEVICE_ID`) REFERENCES `TB_DEVICE` (`DEVICE_ID`),
    CONSTRAINT `tb_device_group_map_ibfk_2` FOREIGN KEY (`DEVICE_GRP_ID`) REFERENCES `TB_DEVICE_GROUP` (`DEVICE_GRP_ID`)
);

CREATE TABLE `TB_DEVICE_TAG`
(
    `DEVICE_TAG_ID`   int          PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_TAG_NM`   varchar(50)  NOT NULL,
    `DEVICE_TAG_DESC` varchar(300) NULL,
    `COMPANY_ID`      int          NOT NULL,
    `USE_YN`          char(1)      NOT NULL DEFAULT 'Y',
    `CREATE_DT`       datetime(6)  NOT NULL,
    `CREATE_ID`       varchar(50)  NOT NULL,
    `UPDATE_DT`       datetime(6)  NOT NULL,
    `UPDATE_ID`       varchar(50)  NULL,
    KEY `FK_TB_COMPANY_TO_TB_DEVICE_TAG` (`COMPANY_ID`),
    CONSTRAINT `FK_TB_COMPANY_TO_TB_DEVICE_TAG` FOREIGN KEY (`COMPANY_ID`) REFERENCES `TB_COMPANY` (`COMPANY_ID`)
);

CREATE TABLE `TB_DEVICE_TAG_MAP`
(
    `DEVICE_TAG_MAP_ID` int    PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_ID`         bigint NOT NULL,
    `DEVICE_TAG_ID`     int    NOT NULL,
    KEY `FK_TB_DEVICE_TAG_TO_TB_DEVICE_TAG_MAP` (`DEVICE_TAG_ID`),
    KEY `FK_TB_DEVICE_TO_TB_DEVICE_TAG_MAP` (`DEVICE_ID`),
    CONSTRAINT `FK_TB_DEVICE_TAG_TO_TB_DEVICE_TAG_MAP` FOREIGN KEY (`DEVICE_TAG_ID`) REFERENCES `TB_DEVICE_TAG` (`DEVICE_TAG_ID`),
    CONSTRAINT `FK_TB_DEVICE_TO_TB_DEVICE_TAG_MAP` FOREIGN KEY (`DEVICE_ID`) REFERENCES `TB_DEVICE` (`DEVICE_ID`)
);

CREATE TABLE `TB_PV_FIRMWARE_MAIN`
(
    `FIRMWARE_MAIN_ID`   int          PRIMARY KEY AUTO_INCREMENT,
    `PLATFORM_ID`        int          NULL,
    `FIRMWARE_NM`        varchar(255) NULL,
    `FIRMWARE_VER`       varchar(50)  NULL,
    `MODEL`              varchar(50)  NULL,
    `FIRMWARE_URL`       varchar(255) NULL,
    `FIRMWARE_DESC`      varchar(255) NULL,
    `COMPANY_ID`         int          NOT NULL,
    `FIRMWARE_UUID_NM`   varchar(100) NULL,
    `FIRMWARE_ORIGIN_NM` varchar(100) NULL,
    `UPLOAD_SERVER_TYPE` varchar(20)  NULL,
    `USE_YN`             char(1)      NOT NULL DEFAULT 'Y',
    `CREATE_DT`          datetime(6)  NOT NULL,
    `CREATE_ID`          varchar(50)  NOT NULL,
    `UPDATE_DT`          datetime(6)  NOT NULL,
    `UPDATE_ID`          varchar(50)  NOT NULL,
    `PACKAGE_TYPE`       varchar(15)  NULL,
    `ANDROID_VERSION_ID` int          NULL,
    `TYPE`               varchar(100) NULL,
    KEY `tb_pv_firmware_main_FK_1` (`PLATFORM_ID`),
    KEY `tb_pv_firmware_main_FK` (`COMPANY_ID`),
    CONSTRAINT `tb_pv_firmware_main_FK` FOREIGN KEY (`COMPANY_ID`) REFERENCES `TB_COMPANY` (`COMPANY_ID`),
    CONSTRAINT `tb_pv_firmware_main_FK_1` FOREIGN KEY (`PLATFORM_ID`) REFERENCES `TB_PLATFORM` (`PLATFORM_ID`)
);

CREATE TABLE `TB_FOTA_CAMPAIGN_DEVICE_MAP`
(
    `CAM_DEVICE_MAP_ID`  bigint       PRIMARY KEY AUTO_INCREMENT,
    `CAMPAIGN_ID`        int          NOT NULL,
    `DEVICE_ID`          bigint       NOT NULL,
    `STATUS`             varchar(15)  NOT NULL,
    `DETAIL_STATUS`      varchar(20)  NULL,
    `DETAIL_STATUS_DESC` varchar(100) NULL,
    `SEQ`                bigint       NOT NULL DEFAULT '0',
    `UPDATE_DT`          datetime(6)  NOT NULL,
    `UPLOAD_SERVER_TYPE` varchar(20)  NOT NULL DEFAULT '',
    `TICKET_STATUS`      tinyint(1)   NOT NULL DEFAULT '0',
    KEY `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_CAMPAIGN_DEVICE_MAP` (`CAMPAIGN_ID`),
    KEY `FK_TB_DEVICE_TO_TB_FOTA_CAMPAIGN_DEVICE_MAP` (`DEVICE_ID`),
    CONSTRAINT `FK_TB_DEVICE_TO_TB_FOTA_CAMPAIGN_DEVICE_MAP` FOREIGN KEY (`DEVICE_ID`) REFERENCES `TB_DEVICE` (`DEVICE_ID`),
    CONSTRAINT `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_CAMPAIGN_DEVICE_MAP` FOREIGN KEY (`CAMPAIGN_ID`) REFERENCES `TB_FOTA_CAMPAIGN` (`CAMPAIGN_ID`)
);

CREATE TABLE `TB_FOTA_PACKAGE`
(
    `PACKAGE_ID`       int          PRIMARY KEY AUTO_INCREMENT,
    `PACKAGE_NM`       varchar(100) NOT NULL,
    `PACKAGE_DESC`     varchar(250) NULL,
    `PLATFORM_ID`      int          NOT NULL,
    `MODEL_CD`         int          NOT NULL,
    `CURRENT_VER`      varchar(20)  NOT NULL,
    `TARGET_VER`       varchar(20)  NOT NULL,
    `FIRMWARE_MAIN_ID` int          NOT NULL,
    `COMPANY_ID`       tinyint      NOT NULL,
    `USE_YN`           char(1)      NOT NULL DEFAULT 'Y',
    `CREATE_DT`        datetime(6)  NOT NULL,
    `CREATE_ID`        varchar(50)  NOT NULL,
    `UPDATE_DT`        datetime(6)  NOT NULL,
    `UPDATE_ID`        varchar(50)  NOT NULL,
    KEY `FK_TB_PV_FIRMWARE_MAIN_TO_TB_FOTA_PACKAGE` (`FIRMWARE_MAIN_ID`),
    CONSTRAINT `FK_TB_PV_FIRMWARE_MAIN_TO_TB_FOTA_PACKAGE` FOREIGN KEY (`FIRMWARE_MAIN_ID`) REFERENCES `TB_PV_FIRMWARE_MAIN` (`FIRMWARE_MAIN_ID`)
);

CREATE TABLE `TB_FOTA_CAMPAIGN_PACKAGE_MAP`
(
    `CAM_PACK_MAP_ID` int PRIMARY KEY AUTO_INCREMENT,
    `PACKAGE_ID`      int NOT NULL,
    `CAMPAIGN_ID`     int NOT NULL,
    KEY `FK_TB_FOTA_PACKAGE_TO_TB_FOTA_CAMPAIGN_PACKAGE_MAP` (`PACKAGE_ID`),
    KEY `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_CAMPAIGN_PACKAGE_MAP` (`CAMPAIGN_ID`),
    CONSTRAINT `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_CAMPAIGN_PACKAGE_MAP` FOREIGN KEY (`CAMPAIGN_ID`) REFERENCES `TB_FOTA_CAMPAIGN` (`CAMPAIGN_ID`),
    CONSTRAINT `FK_TB_FOTA_PACKAGE_TO_TB_FOTA_CAMPAIGN_PACKAGE_MAP` FOREIGN KEY (`PACKAGE_ID`) REFERENCES `TB_FOTA_PACKAGE` (`PACKAGE_ID`)
);

CREATE TABLE `TB_FOTA_DEVICE_GROUP_MAP`
(
    `FOTA_DEVICE_GROUP_MAP_ID` int PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_GRP_ID`            int NOT NULL,
    `CAMPAIGN_ID`              int NOT NULL,
    KEY `FK_TB_DEVICE_GROUP_TO_TB_FOTA_DEVICE_GROUP_MAP` (`DEVICE_GRP_ID`),
    KEY `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_DEVICE_GROUP_MAP` (`CAMPAIGN_ID`),
    CONSTRAINT `FK_TB_DEVICE_GROUP_TO_TB_FOTA_DEVICE_GROUP_MAP` FOREIGN KEY (`DEVICE_GRP_ID`) REFERENCES `TB_DEVICE_GROUP` (`DEVICE_GRP_ID`),
    CONSTRAINT `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_DEVICE_GROUP_MAP` FOREIGN KEY (`CAMPAIGN_ID`) REFERENCES `TB_FOTA_CAMPAIGN` (`CAMPAIGN_ID`)
);

CREATE TABLE `TB_FOTA_DEVICE_TAG_MAP`
(
    `FOTA_DEVICE_TAG_MAP_ID` int PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_TAG_ID`          int NOT NULL,
    `CAMPAIGN_ID`            int NOT NULL,
    KEY `FK_TB_DEVICE_TAG_TO_TB_FOTA_DEVICE_TAG_MAP` (`DEVICE_TAG_ID`),
    KEY `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_DEVICE_TAG_MAP` (`CAMPAIGN_ID`),
    CONSTRAINT `FK_TB_DEVICE_TAG_TO_TB_FOTA_DEVICE_TAG_MAP` FOREIGN KEY (`DEVICE_TAG_ID`) REFERENCES `TB_DEVICE_TAG` (`DEVICE_TAG_ID`),
    CONSTRAINT `FK_TB_FOTA_CAMPAIGN_TO_TB_FOTA_DEVICE_TAG_MAP` FOREIGN KEY (`CAMPAIGN_ID`) REFERENCES `TB_FOTA_CAMPAIGN` (`CAMPAIGN_ID`)
);

CREATE TABLE `TB_OPERATION_QUEUE`
(
    `ID`        bigint      PRIMARY KEY AUTO_INCREMENT,
    `DEVICE_ID` bigint      NOT NULL,
    `PAYLOAD`   json        NULL,
    `UPDATE_DT` datetime(6) NOT NULL,
    `OP_CODE`   varchar(30) NULL,
    KEY `FK_TB_DEVICE_TO_TB_OPERATION_QUEUE` (`DEVICE_ID`),
    CONSTRAINT `FK_TB_DEVICE_TO_TB_OPERATION_QUEUE` FOREIGN KEY (`DEVICE_ID`) REFERENCES `TB_DEVICE` (`DEVICE_ID`)
);