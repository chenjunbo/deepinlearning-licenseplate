###### 车牌识别

1. 请求地址

|              地址               | 请求方式 |
| :-----------------------------: | :------: |
| localhost:16666/plate/recognise |   POST   |

2. 请求参数

| 参数名 | 类型 |   介绍   |
| :----: | :--: | :------: |
| image  | file | 车牌图片 |

3. 返回结果

```json
{
    "msg": "Success!",
    "code": 200,
    "obj": {
        "id": 4,
        "fileName": "2310236.jpg",
        "filePath": "/Users/jackiechan/Documents/deepinlearning-licenseplate/uploads/2310236.jpg",
        "fileType": "jpg",
        "fileLength": null,
        "plate": "冀RF1Y37",
        "plateColor": "蓝牌",
        "lastRecoTime": null,
        "tempPath": "/Users/jackiechan/Downloads/PlateDetect/temp/241012145455547/",
        "recoPlate": "[<冀RF1Y37,蓝牌>]",
        "recoColor": null,
        "recoCorrect": null
    },
    "success": true
}
```

