-- 创建数据库（可选）
CREATE DATABASE IF NOT EXISTS note DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE note;

-- 删除所有表，按外键依赖顺序
DROP TABLE IF EXISTS note_tags;
DROP TABLE IF EXISTS run_results;
DROP TABLE IF EXISTS user_settings;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（唯一）',
    password CHAR(32) NOT NULL COMMENT 'MD5加密后的密码',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间'
);

CREATE UNIQUE INDEX idx_username ON users(username);

INSERT INTO users (username, password)
VALUES ('user', MD5('114514'));

-- 创建笔记表
CREATE TABLE notes (
    note_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记唯一标识',
    user_id INT NOT NULL COMMENT '关联用户ID',
    title VARCHAR(255) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '代码笔记内容',
    type ENUM('C', 'C++', 'Java', 'Python', 'Javascript', 'Rust', 'Html') COMMENT '编程语言类型',
    input TEXT NULL COMMENT '输入内容（可为空）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_user ON notes(user_id);
CREATE INDEX idx_updated ON notes(updated_at);

INSERT INTO notes (user_id, title, content, type)
VALUES (
    1,
    'FirstC',
    '#include<stdio.h>\n int main(){printf("hello world");return 0;}',
    'C'
);

-- 创建运行结果表
CREATE TABLE run_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    note_id INT NOT NULL,
    cur_title TEXT,
    cur_code TEXT,
    cur_input TEXT,
    cur_type TEXT,
    stdout TEXT,
    stderr TEXT,
    status VARCHAR(50),
    exit_status VARCHAR(10),
    time BIGINT,
    run_time BIGINT,
    memory BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES notes(note_id) ON DELETE CASCADE
);

-- 创建标签表
CREATE TABLE tags (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 插入标签
INSERT INTO tags (name) VALUES
    ('数学'), ('排序'), ('字符串'), ('二分'), ('树'), ('图'), ('DP'), ('贪心'), ('分治'),
    ('回溯'), ('KMP'), ('搜索'), ('几何'), ('线性搜索'), ('迪杰斯特拉'), ('弗洛伊德'), ('克鲁斯卡尔'),
    ('普里姆'), ('哈希'), ('网络流'), ('NP'), ('算法');

ALTER TABLE tags AUTO_INCREMENT = 100;

-- 创建笔记-标签关联表
CREATE TABLE note_tags (
    note_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (note_id, tag_id),
    FOREIGN KEY (note_id) REFERENCES notes(note_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- 可选：示例关联（仅当存在这些笔记时，否则注释掉）
-- INSERT INTO note_tags (note_id, tag_id) VALUES (1, 3), (1, 4);

-- 创建用户设置表
CREATE TABLE user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    font_size INT DEFAULT 14,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
