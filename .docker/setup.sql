-- create the databases
CREATE DATABASE IF NOT EXISTS carsharingdb;

-- create the users for database
CREATE USER 'USERCAR'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON carsharingdb.* TO 'USERCAR'@'%';
FLUSH PRIVILEGES;