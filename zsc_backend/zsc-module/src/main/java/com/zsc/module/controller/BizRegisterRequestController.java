package com.zsc.module.controller;

import com.zsc.module.common.response.ResultVo;
import com.zsc.module.domain.dto.BizRegisterRequestDto;
import com.zsc.module.service.BizRegisterRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "注册申请")
@RestController
@RequestMapping("/api")
public class BizRegisterRequestController {

    @Autowired
    private BizRegisterRequestService registerRequestService;

    @Operation(summary = "提交注册申请")
    @PostMapping("/register-request")
    public ResultVo submit(@Valid @RequestBody BizRegisterRequestDto dto) {
        registerRequestService.submit(dto);
        return ResultVo.ok("申请已提交，请等待管理员审核");
    }
}
