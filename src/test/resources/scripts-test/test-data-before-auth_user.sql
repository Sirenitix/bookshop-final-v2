insert into users (id, hash, reg_time, balance, name) values (5005, 'e4e09ed6ee8ca9c5cc773081f7ef0736781d1e9805cb13a3f436a6a1d7bf051d', '2020-10-17 14:53:44', 9632, 'Rob Coast');

insert into user_contact (id, user_id, type, approved, code, code_trials, code_time, contact) values (425, 5005, 'EMAIL', 1, '$2a$10$2SeFvsYqj0tDTEMb/iHeNO5lCJOlTsmjgvnoqllytprtyp45vtDRi', 0, '2021-08-03 16:37:54', 'bogoke1616@eyeremind.com');
insert into user_contact (id, user_id, type, approved, code, code_trials, code_time, contact) values (426, 5005, 'PHONE', 1, '$2a$10$2SeFvsYqj0tDTEMb/iHeNO5lCJOlTsmjgvnoqllytprtyp45vtDRi', 0, '2021-08-03 16:37:56', '+7 (956) 545-85-69');

insert into user_role (user_id, roles) values (5005, 'USER');
insert into user_role (user_id, roles) values (5005, 'ADMIN');
