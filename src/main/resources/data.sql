INSERT INTO `TB_COMPANY` (`COMPANY_ID`, `COMPANY_NM`, `COMPANY_DESC`, `ADMIN_YN`, `LICENSE_ID`, `USE_YN`, `CREATE_DT`,
                          `CREATE_ID`, `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, 'Bluebird', 'string', 'Y', 1, 'Y', '2023-05-02 02:58:36', 'admin@bluebirdcorp.com',
        '2023-06-27 03:27:03.915442', 'admin@bluebirdcorp.com'),
       (2, 'zara', 'clothes company', 'N', 2, 'Y', '2023-05-09 06:13:47', 'admin@bluebirdcorp.com',
        '2023-09-12 03:11:18.801791', 'admin@bluebirdcorp.com'),
       (3, 'Inditexas', 'SPA', 'N', 2, 'Y', '2023-05-10 00:30:38', 'admin@bluebirdcorp.com',
        '2023-05-31 08:21:00.540394', 'admin@bluebirdcorp.com');

INSERT INTO `TB_USER` (`USER_ID`, `ID`, `PASSWORD`, `EMAIL`, `COMPANY_ID`, `GROUP_ID`, `USER_NM`, `USE_YN`, `CREATE_DT`,
                       `CREATE_ID`, `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, 'fake_user1', '$2a$12$JTX1hGYLGuhX3rOiHL5Gne0iOwh0AfA3PAmUmZuEtZk8arbB5BrGa', 'admin@bluebirdcorp.com', 1, 1,
        'dev_admin', 'Y', '2023-05-02 03:50:14', 'admin@bluebirdcorp.com', '2023-05-02 03:50:14',
        'admin@bluebirdcorp.com'),
       (2, 'fake_user2', '$2a$12$JTX1hGYLGuhX3rOiHL5Gne0iOwh0AfA3PAmUmZuEtZk8arbB5BrGa', 'admin@bluebirdcorp.com', 1, 1,
        'admin', 'Y', '2023-05-02 03:50:14', 'admin@bluebirdcorp.com', '2023-05-02 03:50:14', 'admin@bluebirdcorp.com'),
       (3, 'fake_user3', '$2a$12$hA3miYIH5EgzpXV0JT7wmOpFlJu1APeMIfYPNywNfKt2pXcgkYWx6', 'EmmAgent@bluebirdcorp.com', 1,
        1, 'Agent', 'Y', '2023-05-04 01:40:29', 'admin@bluebirdcorp.com', '2023-05-04 01:40:29',
        'admin@bluebirdcorp.com');

INSERT INTO `TB_PLATFORM` (`PLATFORM_ID`, `PLATFORM_NM`, `USE_YN`, `CREATE_DT`, `CREATE_ID`, `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, 'android', 'Y', '2023-05-02 02:14:39', 'admin@bluebirdcorp.com', '2023-05-02 02:14:39',
        'admin@bluebirdcorp.com');

INSERT INTO `TB_DEVICE` (`DEVICE_ID`, `DEVICE_CD`, `SN`, `DEVICE_NM`, `PLATFORM_ID`, `DEVICE_DESC`, `STATUS`,
                         `ENROLL_DT`, `USER_ID`, `IN_WARRANTY`, `EXPIRED_DATE`, `USE_YN`, `CREATE_DT`, `CREATE_ID`,
                         `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, '54f6bb1eae4c811', 'HF550XANLCBA001', 'android_EF500_HF550XANLCBA001', 1, 'EF55112321', 'INACTIVE',
        '2023-05-19 00:22:36', 1, 'Y', NULL, 'Y', '2023-05-19 00:22:36', 'admin@bluebirdcorp.com',
        '2023-11-21 03:25:12.515215', 'admin@bluebirdcorp.com'),
       (2, '54f6bb1eae4c812', 'HF550XANLCBA002', 'android_EF501_EF501XANLCBA002', 1, 'EF500', 'INACTIVE',
        '2023-05-19 00:23:24', 1, 'Y', NULL, 'Y', '2023-05-19 00:23:24', 'admin@bluebirdcorp.com',
        '2024-01-11 09:07:18.134951', 'admin@bluebirdcorp.com'),
       (3, '54f6bb1eae4c813', 'HF551XANLCBA003', 'android_EF501_HF551XANLCBA003', 1, 'EF501', 'INACTIVE',
        '2023-05-19 00:23:31', 2, 'Y', NULL, 'Y', '2023-05-19 00:23:31', 'admin@bluebirdcorp.com',
        '2023-09-05 09:26:09', 'admin@bluebirdcorp.com');

INSERT INTO `TB_DEVICE_DETAIL` (`ID`, `DEVICE_ID`, `SN`, `IMEI`, `IMSI`, `MAC_ADDRESS`, `MODEL_NM`, `VENDOR`, `OS_VER`,
                                `OS_BUILD_DATE`, `OS_BUILD_NUMBER`, `OS_BUILD_DATE_STR`, `FCM_TOKEN`, `UPDATE_DT`)
VALUES (1, 1, 'HF550XANLCBA001', NULL, NULL, NULL, 'HF550', NULL, NULL, '1659665129', '20230706_R1.00',
        'Fri Aug  5 11:05:29 KST 2022', NULL, '2023-05-16 17:25:06.142078'),
       (2, 2, 'HF550XANLCBA002', NULL, NULL, NULL, 'HF550', NULL, NULL, '1659665129', '20230706_R1.00',
        'Fri Aug  5 11:05:29 KST 2022', NULL, '2023-05-16 17:25:06.142078'),
       (3, 3, 'HF551XANLCBA003', NULL, NULL, NULL, 'AG50', NULL, NULL, '1659665129', '20230706_AG1.00',
        'Fri Aug  5 11:05:29 KST 2022', NULL, '2023-05-16 17:25:06.142078');

INSERT INTO `TB_FOTA_CAMPAIGN` (`CAMPAIGN_ID`, `CAMPAIGN_NM`, `CAMPAIGN_DESC`, `PLATFORM_ID`, `START_DT`, `START_TM`,
                                `END_DT`, `END_TM`, `P2P_MODE_YN`, `ASK_USER_YN`, `LTE_USE_YN`, `STATUS`, `COMPANY_ID`,
                                `USE_YN`, `CREATE_DT`, `CREATE_ID`, `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, 'FOTA-1', 'FOTA-1 campaign created by admin for new deployment.', 1, '20231219', '15:56', '20231219',
        '16:26', 'N', 'Y', 'Y', 'ACTIVE', 1, 'Y', '2023-12-19 15:55:58.391583', 'admin@bluebirdcorp.com',
        '2023-12-19 15:55:58.391583', 'admin@bluebirdcorp.com'),
       (2, 'FOTA-2', 'FOTA-11 campaign created by admin for new deployment.', 1, '20231219', '15:58', '20231219',
        '16:28', 'N', 'Y', 'Y', 'ACTIVE', 1, 'Y', '2023-12-19 15:57:39.779833', 'admin@bluebirdcorp.com',
        '2023-12-19 15:57:39.779833', 'admin@bluebirdcorp.com'),
       (3, 'FOTA-3', 'FOTA-111 campaign created by admin for new deployment.', 1, '20231219', '16:02', '20231219',
        '16:32', 'N', 'Y', 'Y', 'ACTIVE', 1, 'Y', '2023-12-19 16:01:25.481656', 'admin@bluebirdcorp.com',
        '2023-12-19 16:01:25.481656', 'admin@bluebirdcorp.com');

INSERT INTO `TB_SUPPORT_MODEL` (`MODEL_ID`, `PLATFORM_ID`, `MODEL_NM`, `USE_YN`, `CREATE_DT`, `CREATE_ID`, `UPDATE_DT`,
                                `UPDATE_ID`)
VALUES (1, 1, 'EF501', 'Y', '2023-07-11 03:34:27', 'admin@bluebirdcorp.com', '2023-10-25 02:50:43.561597',
        'admin@bluebirdcorp.com'),
       (2, 1, 'HF550', 'Y', '2023-07-11 03:34:29', 'admin@bluebirdcorp.com', '2023-07-11 03:34:29',
        'admin@bluebirdcorp.com'),
       (3, 1, 'EF500', 'Y', '2023-07-11 03:34:31', 'admin@bluebirdcorp.com', '2023-07-11 03:34:31',
        'admin@bluebirdcorp.com'),
       (4, 1, 'AG50', 'Y', '2023-07-31 08:20:13', 'admin@bluebirdcorp.com', '2023-07-31 08:20:13',
        'admin@bluebirdcorp.com');

INSERT INTO `TB_DEVICE_DETAIL` (`ID`, `DEVICE_ID`, `SN`, `IMEI`, `IMSI`, `MAC_ADDRESS`, `MODEL_NM`, `VENDOR`, `OS_VER`,
                                `OS_BUILD_DATE`, `OS_BUILD_NUMBER`, `OS_BUILD_DATE_STR`, `FCM_TOKEN`, `UPDATE_DT`)
VALUES (1, 1, 'HF550XANLCBA001', NULL, NULL, NULL, 'HF550', NULL, NULL, '1659665129', '20230706_R1.00',
        'Fri Aug  5 11:05:29 KST 2022', NULL, '2023-05-16 17:25:06.142078'),
       (2, 2, 'EF501XANLCBA002', NULL, NULL, NULL, 'EF501', NULL, NULL, '1659665129', '20230706_R1.00',
        'Fri Aug  5 11:05:29 KST 2022', NULL, '2023-05-16 17:25:06.142078'),
       (3, 3, 'HF550XANLCBA003', NULL, NULL, NULL, 'HF550', NULL, NULL, '1659665129', '20230706_AG1.00',
        'Fri Aug  5 11:05:29 KST 2022', NULL, '2023-05-16 17:25:06.142078');

INSERT INTO `TB_DEVICE_GROUP` (`DEVICE_GRP_ID`, `DEVICE_GRP_CD`, `DEVICE_GRP_NM`, `DEVICE_GRP_DESC`, `COMPANY_ID`,
                               `USE_YN`, `CREATE_DT`, `CREATE_ID`, `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, '0', 'default', 'default device group', 1, 'Y', '2023-05-12 07:07:18', 'admin@bluebirdcorp.com',
        '2023-09-26 02:50:55.486142', 'admin@bluebirdcorp.com'),
       (2, '1', 'test Group 2', 'Group 1', 1, 'Y', '2023-05-30 14:31:31.053685', 'EmmAgent@bluebirdcorp.com',
        '2023-10-16 17:17:43.096948', 'admin@bluebirdcorp.com'),
       (3, '7', 'Group 11', 'testGroupDescasdasd', 1, 'Y', '2023-05-30 15:35:23.629711', 'jhdl0157@naver.com',
        '2023-07-03 12:49:43.238049', 'admin@bluebirdcorp.com');

INSERT INTO `TB_DEVICE_GROUP_MAP` (`DEVICE_GROUP_MAP_ID`, `DEVICE_ID`, `DEVICE_GRP_ID`)
VALUES (1, 1, 1),
       (2, 2, 2),
       (3, 3, 3);

INSERT INTO `TB_DEVICE_TAG` (`DEVICE_TAG_ID`, `DEVICE_TAG_NM`, `DEVICE_TAG_DESC`, `COMPANY_ID`, `USE_YN`, `CREATE_DT`,
                             `CREATE_ID`, `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, 'tag_1', 'tag_164465456', 1, 'Y', '2023-05-16 17:25:06.142078', 'admin', '2023-11-10 06:14:15.012166',
        'admin@bluebirdcorp.com'),
       (2, 'zara_tag_1', 'zara_tag_1_description', 2, 'Y', '2023-09-14 15:40:20.216880', 'test@gmail.com',
        '2023-09-14 15:40:20.216880', 'test@gmail.com'),
       (3, '_aaaa44', 'aaaaa', 1, 'Y', '2023-09-20 02:50:22.519623', 'admin@bluebirdcorp.com',
        '2023-10-25 04:51:52.088022', 'admin@bluebirdcorp.com');

INSERT INTO `TB_DEVICE_TAG_MAP` (`DEVICE_TAG_MAP_ID`, `DEVICE_ID`, `DEVICE_TAG_ID`)
VALUES (1, 1, 1),
       (2, 2, 2),
       (3, 3, 3);


INSERT INTO `TB_FOTA_CAMPAIGN_DEVICE_MAP` (`CAM_DEVICE_MAP_ID`, `CAMPAIGN_ID`, `DEVICE_ID`, `STATUS`, `DETAIL_STATUS`,
                                           `DETAIL_STATUS_DESC`, `SEQ`, `UPDATE_DT`, `UPLOAD_SERVER_TYPE`,
                                           `TICKET_STATUS`)
VALUES (1, 1, 1, 'FAIL', 'FAIL', 'Firmware Update has been failed.', 7400, '2023-10-13 18:41:37.312407', 'CLOUD', 0),
       (2, 1, 2, 'PENDING', 'PENDING', 'Firmware will be updated.', 7801, '2023-10-24 07:46:46.484819', 'CLOUD', 0);

INSERT INTO `TB_FOTA_CAMPAIGN_PACKAGE_MAP` (`CAM_PACK_MAP_ID`, `PACKAGE_ID`, `CAMPAIGN_ID`)
VALUES (1, 1, 1),
       (2, 2, 2);

INSERT INTO `TB_FOTA_DEVICE_GROUP_MAP` (`FOTA_DEVICE_GROUP_MAP_ID`, `DEVICE_GRP_ID`, `CAMPAIGN_ID`)
VALUES (1, 1, 1),
       (2, 2, 1);

INSERT INTO `TB_FOTA_DEVICE_TAG_MAP` (`FOTA_DEVICE_TAG_MAP_ID`, `DEVICE_TAG_ID`, `CAMPAIGN_ID`)
VALUES (1, 1, 1),
       (2, 1, 1),
       (3, 2, 1);

INSERT INTO `TB_FOTA_PACKAGE` (`PACKAGE_ID`, `PACKAGE_NM`, `PACKAGE_DESC`, `PLATFORM_ID`, `MODEL_CD`, `CURRENT_VER`,
                               `TARGET_VER`, `FIRMWARE_MAIN_ID`, `COMPANY_ID`, `USE_YN`, `CREATE_DT`, `CREATE_ID`,
                               `UPDATE_DT`, `UPDATE_ID`)
VALUES (1, 'mclee_campaign_test', '1234', 1, 1, '19:42', '20:12', 1, 1, 'Y', '2023-10-13 18:41:37.279410',
        'admin@bluebirdcorp.com', '2023-10-13 18:41:37.279410', 'admin@bluebirdcorp.com'),
       (2, 'test_UPLOAD_SERVER_TYPE', 'test_UPLOAD_SERVER_TYPE', 1, 1, '12:28', '12:28', 2, 1, 'Y',
        '2023-10-18 12:29:18.690457', 'admin@bluebirdcorp.com', '2023-10-22 07:30:31.003401', 'admin@bluebirdcorp.com'),
       (3, 'test_create', 'test_create', 1, 1, '14:05', '14:05', 3, 1, 'Y', '2023-10-18 05:06:06.557677',
        'admin@bluebirdcorp.com', '2023-10-24 07:46:46.482471', 'admin@bluebirdcorp.com');

INSERT INTO `TB_OPERATION_QUEUE` (`ID`, `DEVICE_ID`, `PAYLOAD`, `UPDATE_DT`, `OP_CODE`)
VALUES (1, 2,
        '{"id": 19001, "code": "UPGRADE_RFID_FIRMWARE", "type": "PROFILE", "status": "PENDING", "payLoad": "{\"firmware_id\":552,\"firmware_type\":0,\"start_date\":20240118,\"start_time\":\"07:03\",\"end_date\":20240126,\"end_time\":\"23:07\",\"force_update\":false,\"server_url\":\"https://new-emm.s3.ap-northeast-2.amazonaws.com/image/RFID_900/20231207/MCU\",\"allow_mobile_data_update\":true,\"useP2P\":false,\"useDefaultURL\":false,\"target_version\":20231207}", "isEnabled": true, "createdTimeStamp": "2024-01-1809:06:41.662"}',
        '2024-01-18 09:06:41.673326', NULL),
       (2, 2,
        '{"id": 20001, "code": "UPGRADE_RFID_FIRMWARE", "type": "PROFILE", "status": "PENDING", "payLoad": "{\"firmware_id\":557,\"firmware_type\":0,\"start_date\":20240122,\"start_time\":\"09:46\",\"end_date\":20240122,\"end_time\":\"10:16\",\"force_update\":false,\"server_url\":\"https://new-emm.s3.ap-northeast-2.amazonaws.com/image/RFID_900/20230113/MCU\",\"allow_mobile_data_update\":true,\"useP2P\":false,\"useDefaultURL\":false,\"target_version\":20230113}", "isEnabled": true, "createdTimeStamp": "2024-01-2200:46:57.990"}',
        '2024-01-22 00:46:57.993639', NULL),
       (3, 2,
        '{"id": 20101, "code": "UPGRADE_RFID_FIRMWARE", "type": "PROFILE", "status": "PENDING", "payLoad": "{\"firmware_id\":557,\"firmware_type\":0,\"start_date\":20240122,\"start_time\":\"10:06\",\"end_date\":20240122,\"end_time\":\"10:36\",\"force_update\":false,\"server_url\":\"https://new-emm.s3.ap-northeast-2.amazonaws.com/image/RFID_900/20230113/MCU\",\"allow_mobile_data_update\":false,\"useP2P\":false,\"useDefaultURL\":false,\"target_version\":20230113}", "isEnabled": true, "createdTimeStamp": "2024-01-2210:07:05.789"}',
        '2024-01-22 10:07:05.793956', NULL);

INSERT INTO `TB_PV_FIRMWARE_MAIN` (`FIRMWARE_MAIN_ID`, `PLATFORM_ID`, `FIRMWARE_NM`, `FIRMWARE_VER`, `MODEL`,
                                   `FIRMWARE_URL`, `FIRMWARE_DESC`, `COMPANY_ID`, `FIRMWARE_UUID_NM`,
                                   `FIRMWARE_ORIGIN_NM`, `UPLOAD_SERVER_TYPE`, `USE_YN`, `CREATE_DT`, `CREATE_ID`,
                                   `UPDATE_DT`, `UPDATE_ID`, `PACKAGE_TYPE`, `ANDROID_VERSION_ID`, `TYPE`)
VALUES (1, 1, 'HF550_OTA_6', '1', 'HF550', 'https://new-emm.s3.ap-northeast-2.amazonaws.com/image/HF550/20230727_R1.00',
        'HF550 Firmware6', 2, NULL, NULL, 'CLOUD', 'Y', '2023-07-11 03:28:10', 'admin@bluebirdcorp.com',
        '2023-07-11 03:28:10', 'admin@bluebirdcorp.com', 'INCREMENTAL', 19, NULL),
       (2, 1, 'HF550_OTA_7', '1', 'HF550', 'https://new-emm.s3.ap-northeast-2.amazonaws.com/image/HF550/20230727_R1.00',
        'HF550 Firmware7', 2, NULL, NULL, 'CLOUD', 'Y', '2023-07-11 03:28:10', 'admin@bluebirdcorp.com',
        '2023-07-11 03:28:10', 'admin@bluebirdcorp.com', 'INCREMENTAL', 19, NULL),
       (3, 1, 'HF550_OTA_8', '1', 'HF550', 'https://new-emm.s3.ap-northeast-2.amazonaws.com/image/HF550/20230727_R1.00',
        'HF550 Firmware8', 2, NULL, NULL, 'CLOUD', 'Y', '2023-07-11 03:28:10', 'admin@bluebirdcorp.com',
        '2023-07-11 03:28:10', 'admin@bluebirdcorp.com', 'INCREMENTAL', 19, NULL);
