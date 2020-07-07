package com.thewhite.security.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest;
import com.jupiter.tools.spring.test.web.annotation.EnableRestTest;
import com.thewhite.security.dto.CreateDiaryDto;
import com.thewhite.security.dto.DiaryDto;
import com.thewhite.security.dto.UpdateDiaryDto;
import com.thewhite.security.service.AuthService;
import com.thewhite.util.test.mvc.MvcRequester;
import com.whitesoft.api.dto.CollectionDTO;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Konstantin Khudin
 */
@EnableRestTest
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnablePostgresIntegrationTest
@ExtendWith(SoftAssertionsExtension.class)
public class DiaryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @Value("user")
    protected String name;

    @Value("password")
    protected String password;

    private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    final DiaryDto expectedDto = DiaryDto.builder()
                                         .id(id)
                                         .title("1")
                                         .record("first")
                                         .owner("user")
                                         .recordDate(Timestamp.valueOf("2019-05-20 21:15:30.0"))
                                         .build();

    @Test
    @DataSet(value = "/datasets/diary/api/empty.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/create__expected.json")
    void create() throws Exception {
        // Arrange
        CreateDiaryDto diaryDto = CreateDiaryDto.builder()
                                                .title("1")
                                                .record("first")
                                                .owner("user")
                                                .recordDate(Timestamp.valueOf("2019-05-20 21:15:30.0"))
                                                .build();
        // Act
        DiaryDto result = MvcRequester.on(mockMvc)
                                      .to("/diary/create")
                                      .withBasicAuth(name, password)
                                      .post(diaryDto)


                                      // Assert
                                      .doExpect(status().isCreated())
                                      .doReturn(new TypeReference<DiaryDto>() {});

        Assertions.assertThat(result).isEqualToIgnoringGivenFields(expectedDto, "id");
    }

    @Test
    @DataSet(value = "/datasets/diary/api/update.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/update__expected.json")
    void update() throws Exception {
        // Arrange
        UpdateDiaryDto updateDto = UpdateDiaryDto.builder()
                                                 .title("1")
                                                 .record("first")
                                                 .recordDate(Timestamp.valueOf("2019-05-20 21:15:30.0"))
                                                 .build();

        // Act
        DiaryDto result = MvcRequester.on(mockMvc)
                                      .to("/diary/{id}/update", id)
                                      .withBasicAuth(name, password)
                                      .post(updateDto)

                                      // Assert
                                      .doExpect(status().isOk())
                                      .doReturn(new TypeReference<DiaryDto>() {});

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DataSet(value = "/datasets/diary/api/empty.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/empty.json")
    void getWhenNotExists() throws Exception {
        // Act
        MvcRequester.on(mockMvc)
                    .to("/diary/{id}", id)
                    .withBasicAuth(name, password)
                    .get()

                    // Assert
                    .doExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "/datasets/diary/api/get.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/get.json")
    void get() throws Exception {
        // Act
        DiaryDto result = MvcRequester.on(mockMvc)
                                      .to("/diary/{id}", id)
                                      .withBasicAuth(name, password)
                                      .get()

                                      // Assert
                                      .doExpect(status().isOk())
                                      .doReturn(new TypeReference<DiaryDto>() {});

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DataSet(value = "/datasets/diary/api/list.json", cleanBefore = true, cleanAfter = true)
    void list() throws Exception {
        // Act
        CollectionDTO<DiaryDto> result = MvcRequester.on(mockMvc).to("/diary/list")
                                                     .withParam("pageNo", 1)
                                                     .withParam("pageSize", 1)
                                                     .withParam("sortField", "id")
                                                     .withParam("sortDirection", "DESC")
                                                     .withBasicAuth(name, password)
                                                     .get()

                                                     // Assert
                                                     .doExpect(status().isOk())
                                                     .doReturn(new TypeReference<CollectionDTO<DiaryDto>>() {});

        Assertions.assertThat(result.getTotalCount()).isEqualTo(2);
        Assertions.assertThat(result.getItems().get(0)).isEqualToIgnoringGivenFields(expectedDto, "id");
    }

    @Test
    @DataSet(value = "/datasets/diary/api/delete.json", cleanBefore = true, cleanAfter = true)
    @ExpectedDataSet("/datasets/diary/api/empty.json")
    void delete() throws Exception {
        // Act
        MvcRequester.on(mockMvc)
                    .to("/diary/{id}/delete", id)
                    .withBasicAuth(name, password)
                    .post()

                    // Assert
                    .doExpect(status().isOk());
    }

    @Test
    @DataSet(value = "/datasets/diary/api/get_by_owner.json", cleanAfter = true, cleanBefore = true)
    void getByOwner() throws Exception {
        //Arrange
        Mockito.when(authService.getAuthorizedOwnerName()).thenReturn("user");

        // Act
        List<DiaryDto> result = MvcRequester.on(mockMvc)
                                            .to("/diary/owner")
                                            .withBasicAuth(name, password)
                                            .get()

                                            // Assert
                                            .doExpect(status().isOk())
                                            .doReturn(new TypeReference<List<DiaryDto>>() {});

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0)).isEqualToComparingFieldByField(expectedDto);
    }
}