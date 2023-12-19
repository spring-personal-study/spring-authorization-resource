package com.bos.resource.app.fota.model.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static ConvertedDateString setStartEndDateTime() {
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = startDate.plusMinutes(30);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedStartDateString = startDate.format(dateFormatter);
        String formattedEndDateString = endDate.format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedStartTimeString = startDate.format(timeFormatter);
        String formattedEndTimeString = endDate.format(timeFormatter);

        return new ConvertedDateString(formattedStartDateString, formattedEndDateString, formattedStartTimeString, formattedEndTimeString);
    }

}
