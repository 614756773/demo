### 运行前准备
- 在windows环境下使用1.2.1版本的hadoop时有个bug
    - 因为windows环境下的文件目录和linux的不一样，所以bug会导致无法读取到输入目录
    - 解决方法,把org.apache.hadoop.fs.FileUtil类复制出来,然后注释掉checkReturnValue方法里的内容
- 在项目根目录下`与src目录同级`新建input文件夹,并且在其中存放一些文本文件