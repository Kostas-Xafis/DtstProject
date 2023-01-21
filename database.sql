CREATE DATABASE IF NOT EXISTS dtst;
USE dtst;

#===================DROP CONSTRAINTS===================
# ALTER TABLE Payment
#     DROP CONSTRAINT FK_Payment_Tax_id
# ;
# ALTER TABLE Tax_Declaration
#     DROP CONSTRAINT FK_Tax_Payment_id,
#     DROP CONSTRAINT FK_Tax_Estate_id
# ;
# ALTER TABLE Real_Estate
#     DROP CONSTRAINT FK_Real_Estate_Tax_id
# ;
#===================DROP TABLES===================

# DROP TABLE Tax_Declaration;
# DROP TABLE Payment;
# DROP TABLE Real_Estate;
# DROP TABLE User_Roles;
# DROP TABLE Role;
# DROP TABLE User;
# DROP DATABASE dtst;

#===================CREATE TABLES===================
# CREATE DATABASE dtst;

CREATE TABLE User (
    User_id int NOT NULL AUTO_INCREMENT,
    Username varchar(40) NOT NULL,
    Password varchar(120) NOT NULL,
    FirstName varchar(40),
    Lastname varchar(40),
    Email varchar(80) UNIQUE,
    CONSTRAINT PK_User PRIMARY KEY (User_id)
) AUTO_INCREMENT = 1;

CREATE TABLE Role (
    Role_id int NOT NULL,
    Name varchar(40) NOT NULL UNIQUE,
    CONSTRAINT PK_Role PRIMARY KEY (Role_id)
);

CREATE TABLE User_Roles (
    User_id int NOT NULL,
    Role_id int NOT NULL,
    CONSTRAINT PK_User_Role PRIMARY KEY (User_id, Role_id),
    CONSTRAINT FK_User_Role_User_id FOREIGN KEY (User_id) REFERENCES User(User_id),
    CONSTRAINT FK_User_Role_Role_id FOREIGN KEY (Role_id) REFERENCES Role(Role_id)
);

CREATE TABLE Real_Estate (
   Real_estate_id int NOT NULL AUTO_INCREMENT,
   Seller_id int NOT NULL,
   Tax_Declaration_id int,
   Address varchar(80),
   Road_number int,
   Area_code int,
   Area_size int,
   Description varchar(400),
   CONSTRAINT PK_Real_estate PRIMARY KEY (Real_estate_id),
   CONSTRAINT FK_Seller_id FOREIGN KEY (Seller_id) REFERENCES User(User_id)
) AUTO_INCREMENT = 1;

CREATE TABLE Payment(
    Payment_id int NOT NULL AUTO_INCREMENT,
    Payer_id int NOT NULL,
    Tax_id int NOT NULL,
    Amount int NOT NULL,
    Payed boolean DEFAULT false,
    CONSTRAINT PK_Payment_id PRIMARY KEY (Payment_id),
    CONSTRAINT FK_Payment_Payer_id FOREIGN KEY (Payer_id) REFERENCES User(User_id)
) AUTO_INCREMENT = 1;

CREATE TABLE Tax_Declaration (
     Declaration_id int NOT NULL AUTO_INCREMENT,
     Buyer_id int,
     Seller_id int NOT NULL,
     BuyerNotary_id int,
     SellerNotary_id int,
     Payment_id int,
     Real_estate_id int NOT NULL,
     Declaration blob,
     Accepted int DEFAULT 0,
     Completed boolean DEFAULT false,
     CONSTRAINT PK_Tax PRIMARY KEY (Declaration_id),
     CONSTRAINT FK_Tax_Buyer_id  FOREIGN KEY (Buyer_id) REFERENCES User(User_id),
     CONSTRAINT FK_Tax_Seller_id FOREIGN KEY (Seller_id) REFERENCES User(User_id),
     CONSTRAINT FK_Tax_Payment_id  FOREIGN KEY (Payment_id) REFERENCES Payment(Payment_id),
     CONSTRAINT FK_Tax_Estate_id FOREIGN KEY (Real_estate_id) REFERENCES Real_Estate(Real_estate_id),
     CONSTRAINT FK_Tax_SellerNotary_id FOREIGN KEY (SellerNotary_id) REFERENCES User(User_id),
     CONSTRAINT FK_Tax_BuyerNotary_id  FOREIGN KEY (BuyerNotary_id) REFERENCES User(User_id)
) AUTO_INCREMENT = 1;

#===================ADD CONSTRAINTS===================

ALTER TABLE Payment
    ADD CONSTRAINT FK_Payment_Tax_id FOREIGN KEY (Tax_id) REFERENCES Tax_Declaration(Declaration_id)
;

ALTER TABLE Real_Estate
    ADD CONSTRAINT FK_Real_Estate_Tax_id FOREIGN KEY (Tax_Declaration_id) REFERENCES Tax_Declaration(Declaration_id)
;
#===================DUMMY VALUES===================
INSERT INTO Role (Role_id, Name) VALUES (1, 'ROLE_USER');
INSERT INTO Role (Role_id, Name) VALUES (2, 'ROLE_ADMIN');

INSERT INTO User (Username, Password, FirstName, Lastname, Email)
VALUES ('Wackjon', '$2a$10$Nnua2EmpoyH1utde5w/GAudHsQctd2ldNir3eZ/2OpHoej0EiKtve', 'Jackson', 'Wagner', 'JustAnEmail@gmail.com');

INSERT INTO Real_Estate (Seller_id, Address, Road_number, Area_code, Area_size, Description)
VALUES (1, 'Longhill St.', 5, 921023, 521, 'Real estate description...');

INSERT INTO Tax_Declaration (Seller_id, Real_estate_id) VALUES (1, 1);

INSERT INTO User (Username, Password, FirstName, Lastname, Email)
VALUES ('Brandy' ,'$2a$10$Nnua2EmpoyH1utde5w/GAudHsQctd2ldNir3eZ/2OpHoej0EiKtve', 'Bob', 'Randy', 'BobsEmail@mail.com');

INSERT INTO User (Username, Password, FirstName, Lastname, Email)
VALUES ('Joker', '$2a$10$Nnua2EmpoyH1utde5w/GAudHsQctd2ldNir3eZ/2OpHoej0EiKtve', 'Jeremiah', 'Brooker', 'Jerry@mail.com');

INSERT INTO User (Username, Password, FirstName, Lastname, Email)
VALUES ('Tsu San', '$2a$10$Nnua2EmpoyH1utde5w/GAudHsQctd2ldNir3eZ/2OpHoej0EiKtve', 'Kamikato', 'Tsuseki', 'Japan4Evs❤️@mail.com');

INSERT INTO User (Username, Password, FirstName, Lastname, Email)
VALUES ('Chopper', '$2a$10$Nnua2EmpoyH1utde5w/GAudHsQctd2ldNir3eZ/2OpHoej0EiKtve', 'Jonathan', 'Hopper', 'Hopper@gmail.com');

INSERT INTO User (Username, Password, FirstName, Lastname, Email)
VALUES ('McJordans', '$2a$10$Nnua2EmpoyH1utde5w/GAudHsQctd2ldNir3eZ/2OpHoej0EiKtve', 'Jordan', 'McGregory', 'MacNotDonalds@gmail.com');


INSERT INTO User_Roles (User_id, Role_id) VALUES (1, 1);
INSERT INTO User_Roles (User_id, Role_id) VALUES (2, 2);
INSERT INTO User_Roles (User_id, Role_id) VALUES (3, 1);
INSERT INTO User_Roles (User_id, Role_id) VALUES (4, 1);
INSERT INTO User_Roles (User_id, Role_id) VALUES (5, 1);
INSERT INTO User_Roles (User_id, Role_id) VALUES (6, 1);

# SELECT * FROM User JOIN User_Roles UR on User.User_id = UR.User_id;
# SELECT * FROM Tax_Declaration;