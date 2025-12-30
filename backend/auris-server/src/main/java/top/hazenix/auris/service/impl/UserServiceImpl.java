package top.hazenix.auris.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.hazenix.auris.constant.JwtClaimsConstant;
import top.hazenix.auris.constant.MessageConstant;
import top.hazenix.auris.context.BaseContext;
import top.hazenix.auris.dto.UserDTO;
import top.hazenix.auris.entity.User;
import top.hazenix.auris.mapper.UserMapper;
import top.hazenix.auris.properties.JwtProperties;
import top.hazenix.auris.query.UserLoginQuery;
import top.hazenix.auris.service.IUserService;
import top.hazenix.auris.utils.JwtUtil;
import top.hazenix.auris.vo.UserLoginVO;
import top.hazenix.auris.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final JwtProperties jwtProperties;
    private final RedisTemplate redisTemplate;
    private final UserMapper userMapper;

    /**
     * 完成用户登录功能的相关逻辑
     * @param userLoginQuery
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginQuery userLoginQuery) {
        User user = userMapper.selectByEmail(userLoginQuery.getEmail());
        if (user == null) {
            throw new RuntimeException(MessageConstant.CURRENT_EMAIL_NOT_REGISTERD);
        }
        //【数据库的密码是加密过的】
        String processedPassword = DigestUtils.md5DigestAsHex(userLoginQuery.getPassword().getBytes());
        if(!processedPassword.equals(user.getPassword())){
            throw new RuntimeException(MessageConstant.EMAIL_OR_PASSWORD_ERROR);
        }
        if(user.getStatus()!=null && !user.getStatus()){
            throw new RuntimeException(MessageConstant.CURRENT_USER_IS_ILLEGAL);
        }
        user.setPassword("*");

        //更新lastLoginTime字段
//        User userUse = User.builder()
//                .id(user.getId())
//                .lastLoginTime(LocalDateTime.now())
//                .build();
//        userMapper.updateLastLoginTime(userUse);

        //生成JWT，组装返回对象
        HashMap<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .token(token)
                .build();
        return userLoginVO;
    }

    /**
     * 完成用户注册的相关逻辑
     * @param userLoginQuery
     * @return
     */
    @Override
    public UserLoginVO register(UserLoginQuery userLoginQuery) {

        if (userMapper.selectByEmail(userLoginQuery.getEmail()) != null){
            throw new RuntimeException(MessageConstant.CURRENT_EMAIL_HAS_REGISTERED);
        }
        //!!【密码要先加密才能插入数据库】
        String processedPassword = DigestUtils.md5DigestAsHex(userLoginQuery.getPassword().getBytes());


        //插入user表
        User user = User.builder()
                .username(userLoginQuery.getUsername())
                .email(userLoginQuery.getEmail())
                .password(processedPassword)
                .role(2)//默认普通用户
                .status(true)//默认正常状态，false表示禁用
//                .lastLoginTime(LocalDateTime.now())
                .build();
        userMapper.insert(user);

        //生成JWT
        HashMap<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);
        //组装对象
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .token(token)
                .build();

        return userLoginVO;
    }

    /**
     * 完成用户退出登录的逻辑
     */
    @Override
    public void logout(HttpServletRequest request) {
        // 清除当前用户的认证信息
        // 如果使用Spring Security:
        // SecurityContextHolder.clearContext();



        // 可选：调用Google API撤销token
        // revokeGoogleToken(accessToken);
//        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
//        GenericUrl url = new GenericUrl("https://oauth2.googleapis.com/revoke");
//        url.put("token", accessToken);
//        HttpRequest request = requestFactory.buildGetRequest(url);
//        HttpResponse response = request.execute();
//        if (response.getStatusCode() == 200) {
//            // Token成功撤销
//            log.info("Google access token revoked successfully");
//        }

        // 获取当前JWT token

        String token = request.getHeader(jwtProperties.getUserTokenName());
        if(token == null){
            throw new RuntimeException("当前用户还未登录");
        }
        if (StringUtils.isNotBlank(token)) {
            // 将token加入黑名单，设置过期时间
            String key = "jwt:blacklist:" + getTokenSignature(token);
            redisTemplate.opsForValue().set(
                    key,
                    "invalid",
                    jwtProperties.getUserTtl(),
                    TimeUnit.MILLISECONDS
            );
        }



    }

    /**
     * 获取当前用户信息
     * @return
     */
    @Override
    public UserVO getUserInfo() {
        Long  userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new RuntimeException(MessageConstant.USER_NOT_LOGIN);
        }
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .email(user.getEmail())
                .build();
    }

    /**
     * 修改用户信息
     * @param userDTO
     * @return
     */
    @Override
    public UserVO updateProfile(UserDTO userDTO) {
        //验证邮箱格式
        if (userDTO.getEmail() != null && !userDTO.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException(MessageConstant.EMAIL_FOEMAT_ERROR);
        }

        //验证昵称长度不能超过个字30个字符
        if (userDTO.getUsername() != null && userDTO.getUsername().length() > 30) {
            throw new RuntimeException(MessageConstant.USERNAME_TOO_LONG);
        }


        Long  userId = BaseContext.getCurrentId();
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        user.setId(userId);
        userMapper.update(user);
        //如果某个字段为空，是不会更新的
        User userNow = userMapper.getById(userId);
        UserVO userVO = UserVO.builder()
                .id(userNow.getId())
                .username(userNow.getUsername())
                .avatar(userNow.getAvatar())
                .gender(userNow.getGender())
                .email(userNow.getEmail())
                .build();
        return userVO;
    }


    // 在JWT验证时增加黑名单检查
    private boolean isTokenInBlacklist(String token) {
        String key = "jwt:blacklist:" + getTokenSignature(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    private String getTokenSignature(String token) {
        if (org.apache.commons.lang.StringUtils.isBlank(token)) {
            return null;
        }
        String[] chunks = token.split("\\.");
        if (chunks.length > 2) {
            return chunks[2]; // signature part
        }
        return null;
    }
}
