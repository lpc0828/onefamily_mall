package com.onefamily.ucenter.dao;

import com.onefamily.ucenter.model.User;

import java.sql.SQLException;

public interface IUserDao {

    User insert(User user) throws SQLException;

    User queryByMobile(String mobile) throws SQLException;

    User queryByUnionid(String unionid) throws SQLException;

    User queryByPK(Long id) throws SQLException;
}
