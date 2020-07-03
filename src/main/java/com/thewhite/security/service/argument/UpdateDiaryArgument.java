package com.thewhite.security.service.argument;

import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
@Builder
public class UpdateDiaryArgument {
    /** Идентификатор записи дневника */
    private UUID id;

    /** Заголовок записи дневника */
    private String title;

    /** Запись дневника */
    private String record;

    /** Дата последнего обновления */
    private Date recordDate;
}