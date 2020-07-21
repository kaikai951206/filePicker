## filePicker

**使用步骤**

  1.跳转页面
  ```java
    Intent intent=new Intent(MainActivity.this, LocalUpdateActivity.class);
    intent.putExtra("maxNum",5);//设置最大选择数
    startActivityForResult(intent, Constants.UPLOAD_FILE_REQUEST);
  ```
  
  2.接收回调
   ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.UPLOAD_FILE_REQUEST && resultCode == Constants.UPLOAD_FILE_RESULT){
            List<String> list = data.getStringArrayListExtra("pathList");
            for(String path:list){
                Log.d("地址：",path);
            }
        }
    }
  ```
  
  
  **添加到项目中**
  
  Via Gradle:
    ```java
        implementation 'com.sky.filePicker:filePicker:1.0.2'
    ```
    
  Via Maven:
    ```java
        <dependency>
          <groupId>com.sky</groupId>
          <artifactId>filePicker</artifactId>
          <version>1.0.2</version>
        </dependency>
    ```
