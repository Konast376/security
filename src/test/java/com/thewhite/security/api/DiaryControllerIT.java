package com.thewhite.security.api;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest;
import com.thewhite.security.dto.CreateDiaryDto;
import com.thewhite.security.dto.DiaryDto;
import com.thewhite.security.dto.UpdateDiaryDto;
import com.whitesoft.api.dto.CollectionDTO;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Konstantin Khudin
 */
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnablePostgresIntegrationTest
@ExtendWith(SoftAssertionsExtension.class)
public class DiaryControllerIT {

    @Autowired
    private WebTestClient client;

    private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    final DiaryDto expectedDto = DiaryDto.builder()
                                         .id(id)
                                         .title("1")
                                         .record("first")
                                         .writerId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                                         .recordDate(Timestamp.valueOf("2019-05-20 21:15:30.0"))
                                         .build();

    @Test
    @DataSet(value = "/datasets/diary/api/empty.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/create__expected.json")
    void create() throws Exception {
        // Arrange
        CreateDiaryDto diaryDto = CreateDiaryDto.builder()
                                                .id(id)
                                                .title("1")
                                                .record("first")
                                                .writerId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                                                .recordDate(Timestamp.valueOf("2019-05-20 21:15:30.0"))
                                                .build();
        // Act
        DiaryDto result = client.post()
                                .uri("/diary/create")
                                .bodyValue(diaryDto)
                                .exchange()

                                // Assert
                                .expectStatus()
                                .isCreated()
                                .expectBody(DiaryDto.class)
                                .returnResult()
                                .getResponseBody();

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
        DiaryDto result = client.post()
                                .uri("/diary/{id}/update", id)
                                .bodyValue(updateDto)
                                .exchange()

                                // Assert
                                .expectStatus()
                                .isOk()
                                .expectBody(DiaryDto.class)
                                .returnResult()
                                .getResponseBody();

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DataSet(value = "/datasets/diary/api/empty.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/empty.json")
    void getWhenNotExists() throws Exception {
        // Act
        client.get()
              .uri("/diary/{id}", id)
              .exchange()

              // Assert
              .expectStatus().isNotFound()
              .expectBody().json("{\"message\":\"Запись отсутствует\"}");
    }

    @Test
    @DataSet(value = "/datasets/diary/api/get.json", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet("/datasets/diary/api/get.json")
    void get() throws Exception {
        // Act
        DiaryDto result = client.get()
                                .uri("/diary/{id}", id)
                                .exchange()

                                // Assert
                                .expectStatus()
                                .isOk()
                                .expectBody(DiaryDto.class)
                                .returnResult()
                                .getResponseBody();
        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DataSet(value = "/datasets/diary/api/list.json", cleanBefore = true, cleanAfter = true)
    void list() throws Exception {
        // Act
        CollectionDTO<DiaryDto> result = client.get().uri(uriBuilder -> uriBuilder.path("/diary/list")
                                                                                  .queryParam("pageNo", 1)
                                                                                  .queryParam("pageSize", 1)
                                                                                  .queryParam("sortField", "id")
                                                                                  .queryParam("sortDirection", "DESC")
                                                                                  .build())

                                               .exchange()

                                               // Assert
                                               .expectStatus()
                                               .isOk()
                                               .expectBody(new ParameterizedTypeReference<CollectionDTO<DiaryDto>>() {})
                                               .returnResult().getResponseBody();
        Assertions.assertThat(result.getTotalCount()).isEqualTo(2);
        Assertions.assertThat(result.getItems().get(0)).isEqualToIgnoringGivenFields(expectedDto, "id");
    }

    @Test
    @DataSet(value = "/datasets/diary/api/delete.json", cleanBefore = true, cleanAfter = true)
    @ExpectedDataSet("/datasets/diary/api/empty.json")
    void delete() throws Exception {
        // Act
        client.post()
              .uri("/diary/{id}/delete", id)
              .exchange()

              // Assert
              .expectStatus()
              .isOk();
    }
}