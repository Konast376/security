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
@ApiModel(description = "Данные создания записи дневника")
public class CreateDiaryDto {
    @ApiModelProperty("Идентификатор записи дневника")
    private UUID id;

    @ApiModelProperty("Идентификатор автора записи дневника")
    private String owner;

    @ApiModelProperty("Заголовок записи дневника")
    private String title;

    @ApiModelProperty("Текст записи дневника")
    private String record;

    @ApiModelProperty("Дата записи дневника")
    private Date recordDate;
}