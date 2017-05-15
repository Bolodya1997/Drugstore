DROP TABLE types;
CREATE TABLE types(
    id_type         NUMBER(10)                  NOT NULL    PRIMARY KEY,
    name            VARCHAR(255)                NOT NULL,
    inner_able      NUMBER(1)   DEFAULT 0       NOT NULL,
    outer_able      NUMBER(1)   DEFAULT 0       NOT NULL,
    mix_able        NUMBER(1)   DEFAULT 0       NOT NULL
);

DROP TABLE medicines;
CREATE TABLE medicines(
    id_medicine     NUMBER(10)                  NOT NULL    PRIMARY KEY,
    id_type         NUMBER(10)                  REFERENCES types(id_type),
    name            VARCHAR(255)                NOT NULL,
    price           INTEGER                     NOT NULL,
    minimum         INTEGER                     NOT NULL,
    amount          INTEGER                     NOT NULL,
    is_complex      NUMBER(1)   DEFAULT 0       NOT NULL
);

DROP TABLE schemas;
CREATE TABLE schemas(
    id_schema       NUMBER(10)                  NOT NULL    PRIMARY KEY,
    id_medicine     NUMBER(10)                  REFERENCES medicines(id_medicine),
    description     VARCHAR(1024),
    time_           INTERVAL DAY TO SECOND      NOT NULL
);

DROP TABLE components;
CREATE TABLE components(
    id_schema       NUMBER(10)                  REFERENCES schemas(id_schema),
    id_medicine     NUMBER(10)                  REFERENCES medicines(id_medicine),
    amount          INTEGER                     NOT NULL,

    UNIQUE (id_schema, id_medicine)
);

DROP TABLE customers;
CREATE TABLE customers(
    id_customer     NUMBER(10)                  NOT NULL    PRIMARY KEY,
    name            VARCHAR(255)                NOT NULL,
    phone_number    VARCHAR(20)                 NOT NULL,
    address         VARCHAR(255)                NOT NULL
);

DROP TABLE orders;
CREATE TABLE orders(
    id_order        NUMBER(10)                  NOT NULL    PRIMARY KEY,
    id_customer     NUMBER(10)                  REFERENCES customers(id_customer),
    id_medicine     NUMBER(10)                  REFERENCES medicines(id_medicine)
);

DROP TABLE events;
CREATE TABLE events(
    id_event        NUMBER(10)                  NOT NULL    PRIMARY KEY,
    id_order        NUMBER(10)                  REFERENCES orders(id_order),
    date_           DATE                        NOT NULL,
    type_           VARCHAR(255)                NOT NULL
);