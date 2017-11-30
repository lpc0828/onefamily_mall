package com.onefamily.ucenter.service.impl;

import com.onefamily.common.enums.ClientTypeEnum;
import com.onefamily.platform.dataformat.ResultVO;
import com.onefamily.platform.utils.HostIPTool;
import com.onefamily.platform.utils.JdbcUtils;
import com.onefamily.ucenter.dao.IUserDao;
import com.onefamily.ucenter.model.User;
import com.onefamily.ucenter.pojo.UserDto;
import com.onefamily.ucenter.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;

@Service
public class UserServiceImpl implements IUserService {

    private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private IUserDao userDao;

    @Override
    public ResultVO<User> queryByMobile(String mobile) {
        ResultVO<User> resultVO = new ResultVO<>();
        do {
            if (StringUtils.isBlank(mobile)) {
                resultVO.format(false, "手机号不能为空"); break;
            }
            try {
                JdbcUtils.loadReadConnection();
                User u = userDao.queryByMobile(mobile);
                resultVO.format(true, "查询成功", u); break;
            } catch (SQLException e) {
                log.error("error", e);
                resultVO.format(false, e.getMessage()); break;
            } finally {
                JdbcUtils.close();
            }
        } while (false);

        return resultVO;
    }

    @Override
    public ResultVO<User> queryByUnionid(String unionid) {
        ResultVO<User> resultVO = new ResultVO<>();
        do {
            try {
                if (StringUtils.isBlank(unionid)) {
                    resultVO.format(false, "unionid不能为空"); break;
                }
                JdbcUtils.loadReadConnection();
                User user = userDao.queryByUnionid(unionid);
                resultVO.format(true, "查询成功", user); break;
            } catch (SQLException e) {
                log.error("error", e);
                resultVO.format(false, e.getMessage()); break;
            } finally {
                JdbcUtils.close();
            }
        } while (false);

        return resultVO;
    }

    @Override
    public ResultVO<User> createMobileUser(String mobile, String unionid, String platform) {
        ResultVO<User> resultVO = new ResultVO<>();
        do {
            if (StringUtils.isBlank(mobile)) {
                resultVO.format(false, "手机号不能为空"); break;
            }
            // 校验用户是否存在
            User user = null;
            try {
                JdbcUtils.startTransaction();
                user = userDao.queryByMobile(mobile);
                if (user != null) {
                    resultVO.format(false, "用户已存在"); break;
                }
                if (StringUtils.isNotBlank(unionid)) {
                    user = userDao.queryByUnionid(unionid);
                }
            } catch (SQLException e) {
                log.error("error", e);
                resultVO.format(false, "系统异常:"+e.getMessage()); break;
            } finally {
                JdbcUtils.close();
            }
            if (user != null) {
                resultVO.format(false, "用户已存在"); break;
            }
            // 新用户,创建之
            user = new User();
            user.setMobile(mobile);
            user.setNickName(mobile);
            ClientTypeEnum clientType = ClientTypeEnum.convertFromPlatform(platform);
            if (clientType == ClientTypeEnum.Android || clientType == ClientTypeEnum.IOS) {
                user.setAppToken(genUtoken());
            } else {
                user.setWxToken(genUtoken());
            }
            try {
                JdbcUtils.startTransaction();
                userDao.insert(user);
                JdbcUtils.commit();
            } catch (SQLException e) {
                log.error("error", e);
                resultVO.format(false, "系统异常:"+e.getMessage()); break;
            } finally {
                JdbcUtils.close();
            }
            resultVO.format(true, "用户创建成功", user); break;
        } while (false);

        return resultVO;
    }



    static String genUtoken() {
        String localIP = StringUtils.replace(HostIPTool.getLocalIP(), ".", "");
        String UUID = java.util.UUID.randomUUID().toString();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return StringUtils.join(localIP, "-", pid, "-", UUID);
    }

    @Override
    public ResultVO<User> createUser(UserDto userDto) {
        return null;
    }
}
