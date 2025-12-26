package top.hazenix.auris.mapper;


import org.apache.ibatis.annotations.Mapper;
import top.hazenix.auris.annotation.AutoFill;
import top.hazenix.auris.entity.User;
import top.hazenix.auris.enumeration.OperationType;


@Mapper
public interface UserMapper {

    User getById(Long id);

    /**
     * 根据邮箱获取用户账号信息
     * @param email
     * @return
     */
    User selectByEmail(String email);

    /**
     * 插入用户信息
     * @param user
     */
    @AutoFill(OperationType.INSERT)
    void insert(User user);

    /**
     * 更新用户信息
     * @param user
     */
    @AutoFill(OperationType.UPDATE)
    void update(User user);

}
