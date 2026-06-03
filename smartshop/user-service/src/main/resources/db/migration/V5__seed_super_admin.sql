-- Platform super admin (email: superadmin@smartshop.local, password: Admin123!)
INSERT INTO users (id, email, password_hash, first_name, last_name, role, enabled, created_at, updated_at)
SELECT gen_random_uuid(),
       'superadmin@smartshop.local',
       '$2b$10$KZ7fq/.O8SySylPjSz.mz.hXbaL9XPu7.QuH6B4le5q5W9iCNXm.u',
       'Platform',
       'Super Admin',
       'SUPER_ADMIN',
       true,
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'superadmin@smartshop.local');
