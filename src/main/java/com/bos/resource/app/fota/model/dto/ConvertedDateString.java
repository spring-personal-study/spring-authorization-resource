package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignRule.InstallRule;
import com.bos.resource.exception.common.GeneralErrorMessage;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
public class ConvertedDateString {
    private final String startDateString;
    private final String endDateString;
    private final String startTimeString;
    private final String endTimeString;

    public ConvertedDateString(String formattedStartDateString, String formattedEndDateString, String formattedStartTimeString, String formattedEndTimeString) {
        this.startDateString = formattedStartDateString;
        this.endDateString = formattedEndDateString;
        this.startTimeString = formattedStartTimeString;
        this.endTimeString = formattedEndTimeString;
    }

    public static ConvertedDateString setStartEndDateTime(InstallRule install) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(install.startDate(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = startDate.plusDays(30);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedStartDateString = startDate.format(dateFormatter);
            String formattedEndDateString = endDate.format(dateFormatter);

            LocalTime startTime = LocalTime.parse(install.timeWindowStart(), DateTimeFormatter.ISO_TIME);
            LocalTime endTime = LocalTime.parse(install.timeWindowEnd(), DateTimeFormatter.ISO_TIME);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedStartTimeString = startTime.format(timeFormatter);
            String formattedEndTimeString = endTime.format(timeFormatter);

            return new ConvertedDateString(formattedStartDateString, formattedEndDateString, formattedStartTimeString, formattedEndTimeString);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(GeneralErrorMessage.DATE_TIME_PARSE_ERROR, e.getParsedString(), e.getErrorIndex());
        }
    }

}
