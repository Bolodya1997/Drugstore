--  1, 2

CREATE OR REPLACE FUNCTION order_status(var__id_order NUMBER)
RETURN NUMBER AS
    var__id_event   NUMBER(10);
BEGIN
    FOR event IN (
        SELECT id_event,
               date_
        FROM events
        WHERE var__id_order = id_order
        ORDER BY date_
        )
    LOOP
        var__id_event := event.id_event;
    END LOOP;

    RETURN var__id_event;
END;

SELECT customers.name,
       customers.phone_number,
       medicines.name AS medicine,
       orders.amount,
       date_ AS ready_date
FROM orders
    JOIN events
        USING (id_order)
    JOIN medicines
        USING (id_medicine)
    JOIN customers
        USING (id_customer)
WHERE
    id_event = order_status(id_order) AND
    type_ = 'ready';    -- = 'wait';

--  3

SELECT *
FROM (  SELECT name AS medicine,
               SUM(st_events.amount) AS usage
        FROM medicines
            JOIN st_events
                USING (id_medicine)
        WHERE type_ = 'get'
        GROUP BY name
        ORDER BY SUM(st_events.amount) DESC
        )
WHERE rownum <= 10;

--  4

SELECT name AS medicine,
       SUM(st_events.amount) AS usage
FROM medicines
    JOIN st_events
        USING (id_medicine)
    JOIN notes
        USING (id_note)
WHERE type_ = 'get' AND
      date_ >= TO_DATE('2017-05-20', 'yyyy-mm-dd') AND  --  from
      date_ <= TO_DATE('2017-06-20', 'yyyy-mm-dd')      --  to
GROUP BY name;

--  5

SELECT customers.name,
       medicines.name AS medicine,
       date_ AS date_open
FROM customers
    JOIN orders
        USING (id_customer)
    JOIN medicines
        USING (id_medicine)
    JOIN events
        USING (id_order)
WHERE type_ = 'open' AND
      date_ >= TO_DATE('2017-05-20', 'yyyy-mm-dd') AND  --  from
      date_ <= TO_DATE('2017-06-20', 'yyyy-mm-dd');     --  to

--  6

SELECT name AS medicine,
       amount,
       minimum
FROM medicines
WHERE amount <= minimum;