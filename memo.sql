INSERT INTO users (id,username, email, password, line_user_id, line_notify, role, created_at)
VALUES 
  (gen_random_uuid(),'shoki', 'shoki@example.com', 'password_shoki', 'lineuseridshoki', true, 'ADMIN', NOW()),
  (gen_random_uuid(),'riko', 'riko@example.com', 'password_riko','lineuseridriko' , true, 'USER', NOW());

INSERT INTO tasks (id,title, description, status, end_date, remind_time, created_at,user_id)
VALUES 
  (gen_random_uuid(),'洗濯', '2回回す', '未完了', '2025-03-31', '2025-03-30 09:00:00', NOW(),'b73322bf-0b58-4f9b-87db-55ad54a379d4'),
  (gen_random_uuid(),'ゴミ捨て', 'ダンボールを捨てる', '未完了', '2025-03-25', '2025-03-25 08:00:00', NOW(),'7f763f23-3690-40e0-8db6-1f5c1e301cd7');


SELECT id FROM users WHERE username = 'shoki';
SELECT id FROM users WHERE username = 'riko';
SELECT id FROM tasks WHERE title = '洗濯';
SELECT id FROM tasks WHERE title = 'ゴミ捨て';

INSERT INTO assignments (id, task_id, user_id)
VALUES 
  (gen_random_uuid(), 'a5a5ca4d-8620-4324-bf0f-ee2a7d54a631', 'd58aa588-dfec-4085-b38e-5327adad2aed'),
  (gen_random_uuid(), 'e458e608-2b9b-4a2b-a500-570f8822e8e0', 'e6ae8f87-9db0-498a-aca7-699df64a95d0');


-- ユーザー、タスク、アサインメントテーブルから「ユーザー名」「タスクタイトル」「タスクステータス」「タスク期限」を取得する。
SELECT u.username, t.title, t.status, t.end_date
FROM assignments a
JOIN users u ON a.user_id = u.id
JOIN tasks t ON a.task_id = t.id;

UPDATE users
SET role = 'ROLE_ADMIN'
WHERE username = 'admin';