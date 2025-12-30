package top.hazenix.auris.query;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginQuery implements Serializable {

    private String username;
    private String email;
    private String password;
    //idToken：（前端google登录方案用到）
    private String idToken;

}
