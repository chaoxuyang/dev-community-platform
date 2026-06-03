package com.devcommunity.platform.web.openapi.rest;

import com.devcommunity.platform.api.model.openapi.user.OpenApiUserDTO;
import com.devcommunity.platform.api.model.vo.ResVo;
import com.devcommunity.platform.api.model.vo.constants.StatusEnum;
import com.devcommunity.platform.service.openapi.oauth.OpenApiSilentLoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YiHui
 * @date 2025/9/15
 */
@RestController
@RequestMapping(path = "/openapi/login")
public class SilentLoginOpenApi {

    private final OpenApiSilentLoginService openApiSilentLoginService;

    public SilentLoginOpenApi(OpenApiSilentLoginService openApiSilentLoginService) {
        this.openApiSilentLoginService = openApiSilentLoginService;
    }

    @GetMapping(path = "loginByToken")
    public ResVo<OpenApiUserDTO> loginByToken(String token) {
        OpenApiUserDTO userInfo = openApiSilentLoginService.silentLogin(token);
        if (userInfo == null) {
            return ResVo.fail(StatusEnum.FORBID_NOTLOGIN);
        }

        return ResVo.ok(userInfo);
    }
}
