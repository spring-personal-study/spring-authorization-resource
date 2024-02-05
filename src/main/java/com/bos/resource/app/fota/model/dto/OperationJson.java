package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.constants.enums.CampaignDeviceStatus;
import com.bos.resource.app.fota.model.constants.enums.OpCode;
import com.bos.resource.app.fota.model.constants.enums.OperationType;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import com.bos.resource.app.fota.model.entity.Firmware;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public record OperationJson(Long id, OpCode code, OperationType type,
                            CampaignDeviceStatus status,
                            String payLoad, Boolean isEnabled,
                            String createdTimeStamp) {

    @Builder
    public OperationJson(Long id, OpCode code, OperationType type, CampaignDeviceStatus status, String payLoad,
                         Boolean isEnabled, String createdTimeStamp) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.status = status;
        this.payLoad = payLoad;
        this.isEnabled = isEnabled;
        this.createdTimeStamp = createdTimeStamp;
    }


    public static OperationJson getOperationJson(PayLoad payLoad, CampaignDeviceMap fotaCampaignDeviceMap,
                                                 DateTimeFormatter dateTimeFormatter) {
        return OperationJson.builder()
                .id(fotaCampaignDeviceMap.getSequence().longValue())
                .code(OpCode.UPGRADE_FIRMWARE)
                .type(OperationType.PROFILE)
                .status(fotaCampaignDeviceMap.getStatus())
                .payLoad(payLoad.toJsonFormat())
                .isEnabled(true)
                .createdTimeStamp(LocalDateTime.now().format(dateTimeFormatter))
                .build();
    }

    public record PayLoad(
            Long firmware_id,
            String start_time,
            String end_time,
            Boolean force_update,
            String server_url,
            Boolean allow_mobile_data_update,
            Boolean useP2P,
            Boolean useDefaultURL
    ) {
        public String toJsonFormat() {
            return
                    "{\"firmware_id\":" + firmware_id + ",\"start_time\":" +"\""+ start_time + "\"" + ",\"end_time\":" +"\""+ end_time + "\"" +
                            ",\"force_update\":" + force_update + ",\"server_url\":" +"\""+ server_url + "\"" + ",\"allow_mobile_data_update\":" + allow_mobile_data_update +
                            ",\"useP2P\":" + useP2P + ",\"useDefaultURL\":" + useDefaultURL +"}";
        }

        @Builder
        public PayLoad(Long firmware_id, String start_time, String end_time, Boolean force_update, String server_url,
                       Boolean allow_mobile_data_update, Boolean useP2P, Boolean useDefaultURL) {
            this.firmware_id = firmware_id;
            this.start_time = start_time;
            this.end_time = end_time;
            this.force_update = force_update;
            this.server_url = server_url;
            this.allow_mobile_data_update = allow_mobile_data_update;
            this.useP2P = useP2P;
            this.useDefaultURL = useDefaultURL;
        }

        public static PayLoad getPayLoad(Campaign updatedFotaCampaign, Firmware pvFirmware) {
            return PayLoad.builder()
                    .firmware_id(pvFirmware.getId())
                    .start_time(updatedFotaCampaign.getStartTime())
                    .end_time(updatedFotaCampaign.getEndTime())
                    .force_update(!updatedFotaCampaign.getAskUserYn().useTypeToBoolean())
                    .server_url(pvFirmware.getUrl())
                    .allow_mobile_data_update(updatedFotaCampaign.getLteUseYn().useTypeToBoolean())
                    .useP2P(updatedFotaCampaign.getP2pModeYn().useTypeToBoolean())
                    .useDefaultURL(false)
                    .build();
        }

    }

}