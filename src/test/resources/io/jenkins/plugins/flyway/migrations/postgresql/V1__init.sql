CREATE TABLE test (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

INSERT INTO test (name) VALUES ('test1');
INSERT INTO test (name) VALUES ('test2');
INSERT INTO test (name) VALUES ('test3');
