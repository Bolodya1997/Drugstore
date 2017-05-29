--  types

DROP TABLE types;
CREATE TABLE types(
    id_type         NUMBER(10)                      NOT NULL        PRIMARY KEY,
    name            VARCHAR(255)                    NOT NULL,
    inner_able      NUMBER(1)       DEFAULT 0       NOT NULL,
    outer_able      NUMBER(1)       DEFAULT 0       NOT NULL,
    mix_able        NUMBER(1)       DEFAULT 0       NOT NULL
);

DROP SEQUENCE seq_types;
CREATE SEQUENCE seq_types
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;

CREATE OR REPLACE TRIGGER ins_types
    BEFORE INSERT ON types
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_type := seq_types.nextval;
END;

--  medicines

DROP TABLE medicines;
CREATE TABLE medicines(
    id_medicine     NUMBER(10)                      NOT NULL        PRIMARY KEY,
    id_type         NUMBER(10)                      REFERENCES types(id_type),
    name            VARCHAR(255)                    NOT NULL,
    price           INTEGER                         NOT NULL,
    minimum         INTEGER                         NOT NULL,
    amount          INTEGER                         NOT NULL,
    is_complex      NUMBER(1)       DEFAULT 0       NOT NULL
);

DROP SEQUENCE seq_medicines;
CREATE SEQUENCE seq_medicines
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;

CREATE OR REPLACE TRIGGER ins_medicines
    BEFORE INSERT ON medicines
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_medicine := seq_medicines.nextval;
END;

--  schemas

DROP TABLE schemas;
CREATE TABLE schemas(
    id_schema       NUMBER(10)                      NOT NULL        PRIMARY KEY,
    id_medicine     NUMBER(10)                      REFERENCES medicines(id_medicine),
    description     VARCHAR(1024),
    time_           INTERVAL DAY TO SECOND          NOT NULL
);

DROP SEQUENCE seq_schemas;
CREATE SEQUENCE seq_schemas
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;


CREATE OR REPLACE TRIGGER ins_schemas
    BEFORE INSERT ON schemas
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_schema := seq_schemas.nextval;
END;

--  components

DROP TABLE components;
CREATE TABLE components(
    id_component    NUMBER(10)                      NOT NULL        PRIMARY KEY,
    id_schema       NUMBER(10)                      REFERENCES schemas(id_schema),
    id_medicine     NUMBER(10)                      REFERENCES medicines(id_medicine),
    amount          INTEGER                         NOT NULL,

    UNIQUE (id_schema, id_medicine)
);

DROP SEQUENCE seq_components;
CREATE SEQUENCE seq_components
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;


CREATE OR REPLACE TRIGGER ins_components
    BEFORE INSERT ON components
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_component := seq_components.nextval;
END;

--  customers

DROP TABLE customers;
CREATE TABLE customers(
    id_customer     NUMBER(10)                      NOT NULL        PRIMARY KEY,
    name            VARCHAR(255)                    NOT NULL,
    phone_number    VARCHAR(20)                     NOT NULL,
    address         VARCHAR(255)                    NOT NULL
);

DROP SEQUENCE seq_customers;
CREATE SEQUENCE seq_customers
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;


CREATE OR REPLACE TRIGGER ins_customers
    BEFORE INSERT ON customers
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_customer := seq_customers.nextval;
END;

--  orders

DROP TABLE orders;
CREATE TABLE orders(
    id_order        NUMBER(10)                      NOT NULL        PRIMARY KEY,
    id_customer     NUMBER(10)                      REFERENCES customers(id_customer),
    id_medicine     NUMBER(10)                      REFERENCES medicines(id_medicine),
    amount          INTEGER                         NOT NULL
);

DROP SEQUENCE seq_orders;
CREATE SEQUENCE seq_orders
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;


CREATE OR REPLACE TRIGGER ins_orders
    BEFORE INSERT ON orders
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_order := seq_orders.nextval;
END;

--  events

DROP TABLE events;
CREATE TABLE events(
    id_event        NUMBER(10)                      NOT NULL        PRIMARY KEY,
    id_order        NUMBER(10)                      REFERENCES orders(id_order),
    date_           DATE                            NOT NULL,
    type_           VARCHAR(255)                    NOT NULL
);

DROP SEQUENCE seq_events;
CREATE SEQUENCE seq_events
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;


CREATE OR REPLACE TRIGGER ins_events
    BEFORE INSERT ON events
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_event   := seq_events.nextval;
    :NEW.date_      := SYSDATE;
END;

--  notes

DROP TABLE notes;
CREATE TABLE notes(
    id_note         NUMBER(10)                      NOT NULL        PRIMARY KEY,
    date_           DATE                            NOT NULL
);

DROP SEQUENCE seq_notes;
CREATE SEQUENCE seq_notes
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE;


CREATE OR REPLACE TRIGGER ins_notes
    BEFORE INSERT ON notes
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    :NEW.id_note    := seq_notes.nextval;
    :NEW.date_      := SYSDATE;
END;

--  st_events

DROP TABLE st_events;
CREATE TABLE st_events(
    id_st_event     NUMBER(10)                      NOT NULL        PRIMARY KEY,
    id_note         NUMBER(10)                      REFERENCES notes(id_note),
    id_medicine     NUMBER(10)                      REFERENCES medicines(id_medicine),
    amount          INTEGER                         NOT NULL,
    type_           VARCHAR(255)                    NOT NULL,

    UNIQUE (id_note, id_medicine)
);

CREATE OR REPLACE TRIGGER st_load
    BEFORE INSERT ON st_events
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    IF :NEW.type_ = 'load' THEN
        UPDATE medicines
        SET amount = amount + :NEW.amount
        WHERE id_medicine = :NEW.id_medicine;
    END IF;
END;

CREATE OR REPLACE TRIGGER st_get
    BEFORE INSERT ON st_events
    REFERENCING NEW AS NEW
    FOR EACH ROW
BEGIN
    IF :NEW.type_ = 'get' THEN
        UPDATE medicines
        SET amount = amount - :NEW.amount
        WHERE id_medicine = :NEW.id_medicine;
    END IF;
END;