package com.zsc.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsc.module.domain.dto.BizRegisterRequestDto;
import com.zsc.module.domain.entity.BizRegisterRequest;
import com.zsc.module.domain.vo.BizRegisterRequestVo;

import java.util.List;

public interface BizRegisterRequestService extends IService<BizRegisterRequest> {

    void submit(BizRegisterRequestDto dto);

    List<BizRegisterRequestVo> listPending();

    BizRegisterRequestVo approve(Long id, String comment);

    void reject(Long id, String comment);
}
