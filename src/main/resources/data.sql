INSERT INTO USERS (id, first_name, last_name, email, password, role) VALUES 
(1, 'admin', 'a', 'admin', '$2a$10$LAluMcekkx1SJMGJvHPrbeXjqK6fIo2rP6EY/cl3bSVuJcTxiwO.a', 'ADMIN'),
(2, 'user', 'b', 'user', '$2a$10$LAluMcekkx1SJMGJvHPrbeXjqK6fIo2rP6EY/cl3bSVuJcTxiwO.a', 'USER'),
(3, '2admin', 'b', '2admin', '$2a$10$LAluMcekkx1SJMGJvHPrbeXjqK6fIo2rP6EY/cl3bSVuJcTxiwO.a', 'ADMIN');

INSERT INTO EVENT (max_size, price, id, organiser_id, description, small_description, title, image_url, date) VALUES 
(10, 2, 1, 1, 'big description', 'small ', 'BIG DAY','https://images.pexels.com/photos/5383759/pexels-photo-5383759.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',"2023-12-14 20:44:00.000000"),
(12, 0, 2, 1, 'big', 'small', 'small day', 'https://images.pexels.com/photos/5383777/pexels-photo-5383777.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1',"2023-12-14 20:44:00.000000"),
(12, 0, 3, 1, 'ANOTHER BIG ONE', 'small', 'THIRD PARTY!', 'https://images.unsplash.com/photo-1696105538782-7815474f28e0?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', "2023-12-14 20:44:00.000000");
