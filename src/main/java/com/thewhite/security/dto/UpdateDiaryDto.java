package com.thewhite.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Данные обновления записи дневника")
public class UpdateDiaryDto {
    @ApiModelProperty("Идентификатор записи дневника")
    private UUID id;

    @ApiModelProperty("Заголовок записи дневника")
    private String title;

    @ApiModelProperty("Текст записи дневника")
    private String record;

    @ApiModelProperty("Дата записи дневника")
    private Date recordDate;
}