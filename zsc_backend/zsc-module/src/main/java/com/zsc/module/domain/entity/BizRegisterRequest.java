package com.zsc.module.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("biz_register_request")
public class BizRegisterRequest {
    @TableId(type = IdType.AUTO)
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
