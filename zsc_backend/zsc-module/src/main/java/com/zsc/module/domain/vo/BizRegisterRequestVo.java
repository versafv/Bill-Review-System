package com.zsc.module.domain.vo;

import lombok.Data;
import java.util.Date;

@Data
public class BizRegisterRequestVo {
    private Long id;
    private String email;
    private String note;
    private String roleKey;
    private String status;
    private String generatedUsername;
    private String generatedPassword;
    private String reviewBy;
    private Date reviewTime;
    private String reviewComment;
    private Date createTime;
}
