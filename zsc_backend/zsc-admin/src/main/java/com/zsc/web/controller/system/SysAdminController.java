package com.zsc.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsc.common.core.controller.BaseController;
import com.zsc.common.core.domain.AjaxResult;
import com.zsc.common.core.domain.entity.SysUser;
import com.zsc.module.domain.entity.BizBill;
import com.zsc.module.service.BizBillService;
import com.zsc.module.service.BizRegisterRequestService;
import com.zsc.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class SysAdminController extends BaseController {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private com.zsc.system.mapper.SysRoleMapper roleMapper;

    @Autowired
    private BizBillService billService;

    @Autowired
    private BizRegisterRequestService registerRequestService;

    @PreAuthorize("@ss.hasPermi('biz:admin:list')")
    @GetMapping("/stats")
    public AjaxResult stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userMapper.countUser());
        data.put("userCount", userMapper.countUserByRoleKey("user"));
        data.put("reviewerCount", userMapper.countUserByRoleKey("reviewer"));
        data.put("adminCount", userMapper.countUserByRoleKey("admin_user"));
        data.put("totalBills", billService.count());
        data.put("pendingBills", billService.count(
            new LambdaQueryWrapper<BizBill>().eq(BizBill::getStatus, "1")));
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/users")
    public AjaxResult users(@RequestParam(required = false) String userName,
                              @RequestParam(required = false) String roleKey,
                              @RequestParam(required = false) String status) {
        List<SysUser> users = userMapper.selectUserWithRoles(userName, roleKey, status);
        return success(users);
    }

    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/roles")
    public AjaxResult roles() {
        return success(roleMapper.selectRoleAll());
    }

    @PreAuthorize("@ss.hasPermi('biz:admin:list')")
    @GetMapping("/register-requests")
    public AjaxResult listRequests() {
        return success(registerRequestService.listPending());
    }

    @PreAuthorize("@ss.hasPermi('biz:admin:list')")
    @PostMapping("/register-requests/{id}/approve")
    public AjaxResult approveRequest(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.getOrDefault("comment", "") : "";
        return success(registerRequestService.approve(id, comment));
    }

    @PreAuthorize("@ss.hasPermi('biz:admin:list')")
    @PostMapping("/register-requests/{id}/reject")
    public AjaxResult rejectRequest(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.getOrDefault("comment", "") : "";
        registerRequestService.reject(id, comment);
        return success();
    }
}
