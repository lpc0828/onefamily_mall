[返回首页](README.md)

# 短信验证相关
  - 当用户注册或者登录需要使用短信验证码作为身份校验;
  - 当需要通过手机号验证用户身份;
  - 用户通过手机号获取短信验证码, 再通过提交验证码和手机号, 来校验用户身份的合法性;
  
**获取短信验证码**
  
  - 调用示例
  ```
      curl -d'mobile=18600249476&trackId=3fb79a6618701bfb67b9e2fdec54a224' 'http://deverp.daling.com:8080/mall/uc/verifyCode/sendCode.do'
      // 请求 Method: Get / Post
      // 请求参数:
          // mobile 用户手机号;
          // trackId 用于标识唯一请求的追踪码;    
  ```
  
  - 返回结果
  ```
      {
          "version": "1.0", 
          "timestamp": "171128 204452.120",
          "status": 0,
          "errorMsg": "验证码发送成功! 281057",
          "elapsed": 20,
          "trackId": "3fb79a6618701bfb67b9e2fdec54a224",
          "data": "281057"
      }
      // 首先应关注status 当为0时代表处理成功,此时需要关注 data信息; 当status不为0,调用方应关注errorMsg信息
  ```
  
**验证短信验证码**
  
  - 调用示例
  ```
      curl -d'mobile=18600249476&verifyCode=281057&trackId=3fb79a6618701bfb67b9e2fdec54a224' 'http://deverp.daling.com:8080/mall/uc/verifyCode/verifyCode.do'
      // 请求 Method: Get / Post
      // 请求参数:
          // mobile 用户手机号;
          // verifyCode 用户收到的短信验证码;
          // trackId 用于标识唯一请求的追踪码;    
  ```
  
  - 返回结果
  ```
      {
          "version": "1.0",
          "timestamp": "171128 203130.265",
          "status": 0,
          "errorMsg": "验证通过!",
          "elapsed": 6,
          "trackId": "3fb79a6618701bfb67b9e2fdec54a224",
          "data": null
      }
      // 首先应关注status 当为0时代表处理成功; 当status不为0,调用方应关注errorMsg信息
  ```  
  
**短信注册接口**
  
  - 调用示例
  ```
    curl -H'platform:android' -d'mobile=18600249476&verifyCode=293580&trackId=3fb79a6618701bfb67b9e2fdec54a224' 'http://deverp.daling.com:8080/mall/uc/login/smsRegister.do'
  ```
  
  - 返回结果
  ```
      {
          "version": "1.0",
          "timestamp": "171130 193832.996",
          "status": 0,
          "errorMsg": "用户创建成功",
          "elapsed": 351,
          "trackId": "3fb79a6618701bfb67b9e2fdec54a224",
          "data": {
              "uid": 10001,
              "nickName": "18600249476",
              "headimgurl": null,
              "utoken": "127011-8368-ddfb0382-d8e1-4216-b3e9-6f25aa930302",
              "ctime": 1512041913265,
              "certYn": "N"
          }
      }
  ```
  
**短信登录接口**

  - 调用示例
  ```
    curl -H'platform:android' -d'mobile=18600249476&verifyCode=735055&trackId=3fb79a6618701bfb67b9e2fdec54a224' 'http://deverp.daling.com:8080/mall/uc/login/smsLogin.do'
  ```  
  
  - 返回结果
  ```
    {
        "version": "1.0",
        "timestamp": "171130 194025.758",
        "status": 0,
        "errorMsg": "查询成功",
        "elapsed": 35,
        "trackId": "3fb79a6618701bfb67b9e2fdec54a224",
        "data": {
            "uid": 10001,
            "nickName": "18600249476",
            "headimgurl": null,
            "utoken": "127011-8368-ddfb0382-d8e1-4216-b3e9-6f25aa930302",
            "ctime": 1512041913265,
            "certYn": "N"
        }
    }
  ```
