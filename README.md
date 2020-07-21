## filePicker

**使用步骤**
1.跳转页面

```java
Intent intent=new Intent(MainActivity.this, LocalUpdateActivity.class);
intent.putExtra("maxNum",5);//设置最大选择数
startActivityForResult(intent, Constants.UPLOAD_FILE_REQUEST);
```
