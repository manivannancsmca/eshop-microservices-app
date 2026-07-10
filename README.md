CREATE USER 'product_write_app'@'%' IDENTIFIED BY 'StrongPassword123!';

GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'product_write_app'@'%';
FLUSH PRIVILEGES;

GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'appuser'@'%';
FLUSH PRIVILEGES;


CREATE USER 'debezium'@'%' IDENTIFIED BY 'debezium';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium'@'%';
FLUSH PRIVILEGES;


curl -X POST -H "Content-Type: application/json" --data @infrastructure/debezium-connector.json http://localhost:8083/connectors

http://localhost:8083/

docker logs debezium-connect-cluster -f

docker compose restart debezium-connect-cluster
