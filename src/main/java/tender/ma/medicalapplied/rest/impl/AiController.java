package tender.ma.medicalapplied.rest.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public String getRecommendation(String countryEn, String symptoms) {
        return service.getRecommendation(countryEn, symptoms);
    }
}
