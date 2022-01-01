DROP TABLE IF EXISTS `user`;
CREATE TABLE user (
  user_id INTEGER NOT NULL AUTO_INCREMENT,
  email VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(50) NOT NULL,
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  wallet DECIMAL,
  PRIMARY KEY (user_id)
);