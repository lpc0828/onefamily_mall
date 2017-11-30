package com.onefamily.ucenter.dao.impl;

import com.onefamily.common.dao.base.impl.BaseDaoImpl;
import com.onefamily.platform.sqls.Query;
import com.onefamily.ucenter.dao.IUserDao;
import com.onefamily.ucenter.model.User;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;

@Service
public class UserDaoImpl extends BaseDaoImpl implements IUserDao {

    @Override
    public User insert(User user) throws SQLException {
        if (user.getCreatedDate() == null) {
            user.setCreatedDate(new Date());
        }
        Long id = super.insert(user);
        user.setId(id);
        return user;
    }

    @Override
    public User queryByMobile(String mobile) throws SQLException {
        Query query = new Query(User.class);
        query.eq("mobile", mobile);
        return query.singleResult();
    }

    @Override
    public User queryByUnionid(String unionid) throws SQLException {
        Query query = new Query(User.class);
        query.eq("unionid", unionid);
        return query.singleResult();
    }
}
