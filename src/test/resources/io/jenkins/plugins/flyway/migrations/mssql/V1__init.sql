CREATE TABLE test (
  id INT IDENTITY(1,1) PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

INSERT INTO test (name) VALUES ('test1');
INSERT INTO test (name) VALUES ('test2');
INSERT INTO test (name) VALUES ('test3');
