package top.hazenix.auris.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import top.hazenix.auris.dto.UserDTO;
import top.hazenix.auris.query.UserLoginQuery;
import top.hazenix.auris.vo.UserLoginVO;
import top.hazenix.auris.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IUserService {

    UserLoginVO login(UserLoginQuery userLoginQuery);

    UserLoginVO register(UserLoginQuery userLoginQuery);

    void logout(HttpServletRequest request);

    UserVO getUserInfo();

    UserVO updateProfile(UserDTO userDTO);





}
