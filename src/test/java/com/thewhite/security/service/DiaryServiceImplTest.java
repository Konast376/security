package com.thewhite.security.service;

import com.thewhite.security.errorInfo.DiaryErrorInfo;
import com.thewhite.security.model.Diary;
import com.thewhite.security.repository.DiaryRepository;
import com.thewhite.security.service.argument.CreateDiaryArgument;
import com.thewhite.security.service.argument.UpdateDiaryArgument;
import com.thewhite.util.test.check.GuardCheck;
import com.whitesoft.util.exceptions.WSArgumentException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Konstantin Khudin
 */
@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
 class DiaryServiceImplTest {
    @Captor
    private ArgumentCaptor<Diary> captor;

    @InjectMocks
    DiaryServiceImpl service;

    @Mock
    DiaryRepository repository;

    private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Test
    void create(BDDSoftAssertions softly) throws Exception {
        // Arrange
        CreateDiaryArgument argument = mock(CreateDiaryArgument.class);
        when(argument.getRecord()).thenReturn("firstRecord");
        when(argument.getTitle()).thenReturn("1");
        when(argument.getOwner()).thenReturn("user1");
        when(argument.getRecordDate()).thenReturn(Timestamp.valueOf("2019-05-20 21:15:30.0"));


        Diary savedRecord = mock(Diary.class);
        when(repository.save(any())).thenReturn(savedRecord);

        // Act
        Diary result = service.create(argument);

        // Assert
        Assertions.assertThat(result).isEqualTo(savedRecord);

        verify(repository).save(captor.capture());
        Diary captorValue = captor.getValue();

        softly.then(captorValue.getRecord()).isEqualTo(argument.getRecord());
        softly.then(captorValue.getTitle()).isEqualTo(argument.getTitle());
        softly.then(captorValue.getOwner()).isEqualTo(argument.getOwner());
        softly.then(captorValue.getRecordDate()).isEqualTo(argument.getRecordDate());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void createWhenWriterIdIsNull(){
        //Arrange
        CreateDiaryArgument argument = mock(CreateDiaryArgument.class);
        when(argument.getOwner()).thenReturn(null);

        //Act
        GuardCheck.guardCheck(() -> service.create(argument),

        //Assert
                              WSArgumentException.class,
                              DiaryErrorInfo.WRITER_ID_IS_MANDATORY);

        verifyNoInteractions(repository);
    }

    @Test
    void createWhenTitleIsNull(){
        //Arrange
        CreateDiaryArgument argument = mock(CreateDiaryArgument.class);
        when(argument.getOwner()).thenReturn("user1");
        when(argument.getTitle()).thenReturn(null);

        //Act
        GuardCheck.guardCheck(() -> service.create(argument),

                              //Assert
                              WSArgumentException.class,
                              DiaryErrorInfo.TITLE_IS_MANDATORY);

        verifyNoInteractions(repository);
    }

    @Test
    void getExisting() {
        //Arrange
        Diary diary = mock(Diary.class);
        when(repository.findById(id)).thenReturn(Optional.of(diary));

        //Act
        Diary result = service.getExisting(id);

        //Assert
        assertEquals(diary, result);
    }

    @Test
    void getByOwner() {
        //Arrange
        Diary diary = mock(Diary.class);
        when(repository.findByOwner("owner")).thenReturn(Optional.of(diary));

        //Act
        Diary result = service.getByOwner("owner");

        //Assert
        assertEquals(diary, result);
    }

    @Test
    void getAll() {
        //Arrange
        Pageable pageable = mock(Pageable.class);
        Page<Diary> page = mock(Page.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        //Act
        Page<Diary> result = service.getAll(pageable);

        //Assert
        Assertions.assertThat(result).isSameAs(page);
    }

    @Test
    void update() {
        //Arrange
        UpdateDiaryArgument argument = mock(UpdateDiaryArgument.class);
        when(argument.getRecord()).thenReturn("firstRecord");
        when(argument.getTitle()).thenReturn("1");
        when(argument.getRecordDate()).thenReturn(Timestamp.valueOf("2019-05-20 21:15:30.0"));

        Diary diary = mock(Diary.class);
        when(repository.findById(id)).thenReturn(Optional.of(diary));

        Diary savedRecord = mock(Diary.class);
        when(repository.save(diary)).thenReturn(savedRecord);

        //Act
        Diary result = service.update(id, argument);

        //Assert
        Assertions.assertThat(result).isEqualTo(savedRecord);
        verify(repository).save(captor.capture());

        verify(diary).setRecord(argument.getRecord());
        verify(diary).setTitle(argument.getTitle());
        verify(diary).setRecordDate(argument.getRecordDate());

        verifyNoMoreInteractions(diary);
    }

    @Test
    void updateWhenTitleIsNull(){
        //Arrange
        UpdateDiaryArgument argument = mock(UpdateDiaryArgument.class);
        when(argument.getTitle()).thenReturn(null);
        //Act
        GuardCheck.guardCheck(() -> service.update(id, argument),

                              //Assert
                              WSArgumentException.class,
                              DiaryErrorInfo.TITLE_IS_MANDATORY);

        verifyNoInteractions(repository);
    }

    @Test
    void delete() {
        //Arrange
        Diary diary = mock(Diary.class);
        when(repository.findById(id)).thenReturn(Optional.of(diary));

        //Act
        service.delete(id);

        //Assert
        verify(repository).delete(diary);
    }
}
