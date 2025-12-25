package top.hazenix.auris.controller.user;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.hazenix.auris.dto.UserDTO;
import top.hazenix.auris.query.UserLoginQuery;
import top.hazenix.auris.result.Result;
import top.hazenix.auris.service.IUserService;
import top.hazenix.auris.vo.UserLoginVO;
import top.hazenix.auris.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: 用户管理相关接口
 * @author: Hazenix
 * @version: 1.0.0
 * @date: 2025/12/25
 * @return
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api("用户管理相关接口")
@Slf4j
public class UserController {

    private final IUserService userService;

    /**
     * @description: 登录接口
     * @param: userLoginQuery
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登录接口")
    public Result<UserLoginVO> login(@RequestBody UserLoginQuery userLoginQuery){
        log.info("用户登录:{}",userLoginQuery);
        UserLoginVO userLoginVO = userService.login(userLoginQuery);
        return Result.success(userLoginVO);
    }

    /**
     * @description: 注册接口
     * @param: userLoginQuery
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("注册接口")
    public Result<UserLoginVO> register(@RequestBody UserLoginQuery userLoginQuery){
        log.info("用户注册:{}",userLoginQuery);
        UserLoginVO userLoginVO = userService.register(userLoginQuery);
        return Result.success(userLoginVO);
    }

    /**
     * @description: 获取当前用户信息
     * @param:
     * @version: 1.0.0
     * @return
     */
    @GetMapping("/userinfo")
    @ApiOperation("获取当前用户信息")
    public Result getUserInfo(){
        log.info("获取用户信息");
        UserVO userVO = userService.getUserInfo();
        return Result.success(userVO);
    }

    /**
     * @description: 退出登录
     * @param:
     * @version: 1.0.0
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public Result logout(HttpServletRequest request){
        log.info("用户退出登录");
        userService.logout(request);
        return Result.success();
    }


    /**
     * @description: 修改用户信息
     * @param userDTO
     * @version: 1.0.0
     * @return
     */
    @PutMapping("/profile")
    @ApiOperation("修改用户信息")
    public Result updateProfile(@RequestBody UserDTO userDTO){
        log.info("更新用户信息:{}",userDTO);
        UserVO userVO = userService.updateProfile(userDTO);
        return Result.success(userVO);
    }

}
