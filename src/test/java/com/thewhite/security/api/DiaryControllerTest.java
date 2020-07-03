package com.thewhite.security.api;

import com.thewhite.security.api.controller.DiaryController;
import com.thewhite.security.dto.CreateDiaryDto;
import com.thewhite.security.dto.DiaryDto;
import com.thewhite.security.dto.UpdateDiaryDto;
import com.thewhite.security.mapper.DiaryMapper;
import com.thewhite.security.model.Diary;
import com.thewhite.security.service.DiaryService;
import com.thewhite.security.service.argument.CreateDiaryArgument;
import com.thewhite.security.service.argument.UpdateDiaryArgument;
import com.whitesoft.api.dto.CollectionDTO;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * @author Konstantin Khudin
 */
@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class DiaryControllerTest {

    @InjectMocks
    DiaryController controller;

    @Mock
    DiaryMapper mapper;

    @Mock
    DiaryService service;

    private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Test
    void create() {
        //Arrange
        CreateDiaryDto createDto = mock(CreateDiaryDto.class);

        Diary savedRecord = mock(Diary.class);
        when(service.create(any(CreateDiaryArgument.class))).thenReturn(savedRecord);

        DiaryDto dto = mock(DiaryDto.class);
        when(mapper.toDto(any(Diary.class))).thenReturn(dto);

        //Act
        DiaryDto result = controller.create(createDto);

        //Assert
        Assertions.assertThat(result).isEqualTo(dto);
    }

    @Test
    void getAll() {
        //Arrange
        int pageNo = 1;
        int pageSize = 1;
        String sortField = "test sorting field";
        Sort.Direction sortDirection = DESC;
        Diary diary = mock(Diary.class);
        when(service.getAll(any())).thenReturn(new PageImpl<>(Lists.newArrayList(diary)));

        DiaryDto dto = mock(DiaryDto.class);
        when(mapper.toDto(diary)).thenReturn(dto);

        //Act
        CollectionDTO<DiaryDto> result = controller.getAll(pageNo, pageSize, sortField, sortDirection);

        //Assert
        verify(service).getAll((PageRequest.of(pageNo, pageSize, sortDirection, sortField)));
        Assertions.assertThat(result.getItems()).containsOnly(dto);
    }

    @Test
    void get() {
        //Arrange
        Diary author = mock(Diary.class);
        when(service.getExisting(id)).thenReturn(author);

        DiaryDto dto = mock(DiaryDto.class);
        when(mapper.toDto(author)).thenReturn(dto);

        //Act
        DiaryDto result = controller.get(id);

        //Assert
        assertEquals(dto, result);
    }

    @Test
    void update() {
        //Arrange
        UpdateDiaryDto updateDto = mock(UpdateDiaryDto.class);

        Diary updated = mock(Diary.class);
        when(service.update(any(UUID.class), any(UpdateDiaryArgument.class))).thenReturn(updated);

        DiaryDto dto = mock(DiaryDto.class);
        when(mapper.toDto(any(Diary.class))).thenReturn(dto);

        //Act
        DiaryDto result = controller.update(id, updateDto);

        //Assert
        Assertions.assertThat(result).isEqualTo(dto);
    }

    @Test
    void delete() {
        //Act
        controller.delete(id);

        //Assert
        verify(service).delete(id);
    }
}