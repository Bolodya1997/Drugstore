CREATE OR REPLACE PACKAGE procedures AS

    PROCEDURE get_best__schema_time(
        id_medicine             IN      NUMBER,
        amount_need             IN      NUMBER,
        id_schema               OUT     NUMBER,
        time_                   OUT     INTERVAL DAY TO SECOND
    );

END procedures;

CREATE OR REPLACE PACKAGE BODY procedures AS

    load_time               INTERVAL DAY TO SECOND := '1 0:0:0';
    max_recursion_depth     INTEGER := 10;

    PROCEDURE private__get_best__schema_time(
        var__recursion_depth    IN      INTEGER,

        var__id_medicine        IN      NUMBER,
        var__amount_need        IN      NUMBER,

        var__id_schema          OUT     NUMBER,
        var__time               OUT     INTERVAL DAY TO SECOND
    ) IS
        var__is_complex         BOOLEAN;
        var__is_in_storage      BOOLEAN;

        var__amount_have        NUMBER;

        var__schema_time        INTERVAL DAY TO SECOND;
        var__component_time     INTERVAL DAY TO SECOND;

        var__tmp                NUMBER;
    BEGIN
        IF var__recursion_depth > max_recursion_depth THEN
            RETURN;
        END IF;

        SELECT      is_complex,
                    amount
        INTO        var__tmp,
                    var__amount_have
        FROM        medicines
        WHERE       id_medicine = var__id_medicine;

        var__is_complex     := var__tmp = 1;
        var__is_in_storage  := var__amount_have >= var__amount_need;

        IF var__is_in_storage THEN
            var__time := INTERVAL '0 0:0:0' DAY TO SECOND;
            RETURN;
        END IF;

        IF NOT var__is_complex THEN
            var__time := load_time;
            RETURN;
        END IF;

        FOR schema IN (
            SELECT      id_schema,
                        time_
            FROM        schemas
            WHERE       id_medicine = var__id_medicine
            )
        LOOP
            var__schema_time := NULL;
            FOR component IN (
                SELECT      id_medicine,
                            amount
                FROM        components
                WHERE       id_schema = schema.id_schema
                )
            LOOP
                private__get_best__schema_time(
                    var__recursion_depth + 1,
                    component.id_medicine,
                    component.amount,
                    var__tmp,
                    var__component_time);

                CONTINUE WHEN var__component_time IS NULL;

                IF var__schema_time IS NULL OR var__component_time > var__schema_time THEN
                    var__schema_time := var__component_time;
                END IF;
            END LOOP;

            CONTINUE WHEN var__schema_time IS NULL;

            var__schema_time := var__schema_time + schema.time_;
            IF var__time IS NULL OR var__schema_time < var__time THEN
                var__time := var__schema_time;
            END IF;
        END LOOP;
    END private__get_best__schema_time;

    PROCEDURE get_best__schema_time(
        id_medicine             IN      NUMBER,
        amount_need             IN      NUMBER,
        id_schema               OUT     NUMBER,
        time_                   OUT     INTERVAL DAY TO SECOND
    ) IS
    BEGIN
        private__get_best__schema_time(
            0,
            id_medicine,
            amount_need,
            id_schema,
            time_
        );
    END get_best__schema_time;

END procedures;