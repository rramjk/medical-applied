package tender.ma.medicalapplied.rest.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tender.ma.medicalapplied.dto.ai.AnswerDto;
import tender.ma.medicalapplied.rest.AiRestApi;
import tender.ma.medicalapplied.service.AiService;

@RestController
@RequiredArgsConstructor
@RequestMapping(AiRestApi.BASE_URL)
@Tag(name = "Контроллер ИИ-ассистента")
public class AiController implements AiRestApi {
    private final AiService service;

    @Operation(summary = "Получить медицинскую рекомендацию",
            description = """
                    Данный метод позволяет получить медицинскую рекомендацию от ИИ-ассистента.
                    (Доступен для верифицированных и заполнивших профиль здоровья пользователей)
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.TEXT_PLAIN_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Bad Request + Некорректный запрос",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @Override
    public String getRecommendation(@Parameter(description = "Название страны (EN)") String countryEn,
                                    @Parameter(description = "Симптом") String symptoms) {
        return service.getRecommendation(countryEn, symptoms);
    }

    @Operation(summary = "Спросить вопрос у ИИ-ассистента",
            description = """
                    Данный метод позволяет спросить вопрос у ИИ-ассистента.
                    (Доступен для верифицированных и заполнивших профиль здоровья пользователей)
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = AnswerDto.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Bad Request + Некорректный запрос",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @Override
    public String ask(AnswerDto dto) {
        return service.ask(dto);
    }
}
