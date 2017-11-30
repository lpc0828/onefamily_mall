[返回首页](README.md)

# 鉴权接口
  - 前台或者后台系统需要校验用户的合法性时;
  
**鉴权auth**
  
  - 调用示例
  ```
    curl -H'platform:android' -H'uid:10001' -H'utoken:127011-8368-ddfb0382-d8e1-4216-b3e9-6f25aa930302' 'http://deverp.daling.com:8080/mall/uc/auth.do'
  ```
  
  - 返回结果
  ```
    {
        "version": "1.0",
        "timestamp": "171130 202819.715",
        "status": 0,
        "errorMsg": "鉴权成功",
        "elapsed": 459,
        "trackId": null,
        "data": {
            "uid": 10001,
            "nickName": "18600249476",
            "headimgurl": "http://www.toysandco.com/media/products/large/HEE3500-L.jpg",
            "utoken": "127011-8368-ddfb0382-d8e1-4216-b3e9-6f25aa930302",
            "ctime": 1512041913265,
            "certYn": "N"
        }
    }
  ```