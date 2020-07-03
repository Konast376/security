package com.thewhite.security.service.argument;

import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
@Builder
public class CreateDiaryArgument {
    /** Идентификатор записи дневника */
    private UUID id;

    /** Идентификатор пользователя */
    private UUID writerId;

    /** Заголовок записи дневника */
    private String title;

    /** Запись дневника */
    private String record;

    /** Дата последнего обновления */
    private Date recordDate;
}