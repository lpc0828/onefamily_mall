package com.onefamily.ucenter.service;

import com.onefamily.platform.dataformat.ResultVO;
import com.onefamily.ucenter.model.User;
import com.onefamily.ucenter.pojo.UserDto;


public interface IUserService {

    ResultVO<User> createMobileUser(String mobile, String unionid, String platform);

    ResultVO<User> createUser(UserDto userDto);

    ResultVO<User> queryByMobile(String mobile);

    ResultVO<User> queryByUnionid(String unionid);
}
