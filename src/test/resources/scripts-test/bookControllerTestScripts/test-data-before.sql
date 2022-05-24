insert into books (id, pub_date, is_bestseller, slug, title, image, description, price, discount) values (4001, '2021-06-03', 1, 'book-uni-888', 'Southern Comfort', 'http://dummyimage.com/265x289.png/5fa2dd/ffffff', 'Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh. Quisque id justo sit amet sapien dignissim vestibulum. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue.', 388, 0.19);

insert into authors (id, photo, slug, name, description) values (4002, 'http://dummyimage.com/605x175.png/ff4444/ffffff', 'author-ceh-777', 'Chrysa Allender', 'Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh. Quisque id justo sit amet sapien dignissim vestibulum. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat. In congue. Etiam justo. Etiam pretium iaculis justo. In hac habitasse platea dictumst. Etiam faucibus cursus urna.');

insert into book2author (id, book_id, author_id, sort_index) values (4003, 4001, 4002, 0);

insert into book2user (id, time, type_id, book_id, user_id) values (132548, '2020-11-28 12:57:44', 1, 4001, 5005);

insert into book_review (id, text, time, book_id, user_id) values (4004, 'Cras non velit nec nisi vulputate nonummy. Maecenas tincidunt lacus at velit. Vivamus vel nulla eget eros elementum pellentesque. Quisque porta volutpat erat. Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.', '2021-01-25 12:50:23', 4001, 5005);

insert into book_review_like (id, time, value, review_id, user_id) values (12031, '2021-01-01 18:06:36', 0, 4004, 54);
insert into book_review_like (id, time, value, review_id, user_id) values (12032, '2021-05-10 18:11:55', 0, 4004, 27);
insert into book_review_like (id, time, value, review_id, user_id) values (12033, '2021-02-01 15:01:07', 0, 4004, 72);
insert into book_review_like (id, time, value, review_id, user_id) values (12034, '2021-03-09 13:36:34', 1, 4004, 94);
insert into book_review_like (id, time, value, review_id, user_id) values (12035, '2020-12-01 19:01:06', 0, 4004, 20);
insert into book_review_like (id, time, value, review_id, user_id) values (12036, '2020-09-12 16:34:09', -1, 4004, 84);
insert into book_review_like (id, time, value, review_id, user_id) values (12037, '2020-11-24 11:25:12', -1, 4004, 29);

insert into book_rating (id, value, book_id, user_id) values (33521, 1, 4001, 81);
insert into book_rating (id, value, book_id, user_id) values (33522, 4, 4001, 20);
insert into book_rating (id, value, book_id, user_id) values (33523, 5, 4001, 22);
insert into book_rating (id, value, book_id, user_id) values (33524, 2, 4001, 99);