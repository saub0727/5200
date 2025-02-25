# CarGenie 项目

## 项目说明
这是一个基于二手车数据的数据库项目，使用MySQL实现。

## 如何开始

### 1. 克隆项目
```bash
git clone https://github.com/saub0727/5200.git
```

### 2. 数据准备
- 从 [Kaggle Used Cars Dataset](https://www.kaggle.com/datasets/austinreese/craigslist-carstrucks-data) 下载 `vehicles.csv` 文件
- 将下载的文件放在项目根目录下

### 3. 数据库设置
- 安装MySQL 8.0（不要使用9.x版本，可能会出现兼容性问题）
- 启动MySQL服务：
  ```bash
  # Mac
  brew services start mysql@8.0
  
  # Windows
  net start mysql80
  
  # Linux
  sudo systemctl start mysql
  ```
- 使用DataGrip或其他MySQL客户端连接到数据库
- 按顺序执行以下操作：
  1. 执行 `PM3_1.sql` 创建数据库和表结构
  2. 运行 `import_csv.py` 导入数据到临时表
  3. 执行 `PM3_2.sql` 将数据从临时表分发到各个正式表中
  4. `PM3_3.sql` 为本次作业内容（待完成）

### 4. 参与开发
如果您想参与项目开发：
1. 在群里发送您的GitHub账号
2. 等待管理员添加您为协作者
3. 创建新的分支进行开发（不要直接在master分支上工作）
   ```bash
   git checkout -b your-feature-branch
   ```
4. 完成修改后提交代码：
   ```bash
   git add .
   git commit -m "你的提交信息"
   git push origin your-feature-branch
   ```
5. 在GitHub上创建Pull Request，等待审核

## 注意事项
- master分支已启用保护，所有更改必须通过Pull Request提交
- 请确保在提交代码前已经在本地测试通过
- 重要更改请在群里讨论后再实施