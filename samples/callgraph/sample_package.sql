CREATE OR REPLACE PACKAGE BODY emp_pkg AS

    PROCEDURE log_action(p_action VARCHAR2) IS
    BEGIN
        INSERT INTO audit_log(action, log_date) VALUES (p_action, SYSDATE);
    END log_action;

    FUNCTION get_employee(p_id NUMBER) RETURN VARCHAR2 IS
        v_name VARCHAR2(100);
    BEGIN
        SELECT first_name || ' ' || last_name INTO v_name
        FROM employees WHERE employee_id = p_id;
        RETURN v_name;
    END get_employee;

    PROCEDURE process_order(p_order_id NUMBER) IS
        v_total NUMBER;
        v_emp_name VARCHAR2(100);
    BEGIN
        SELECT total_amount INTO v_total
        FROM orders WHERE order_id = p_order_id;

        IF v_total > 1000 THEN
            apply_discount(p_order_id, v_total);
        END IF;

        v_emp_name := get_employee(1);
        log_action('PROCESS_ORDER: ' || p_order_id);

        UPDATE orders SET status = 'PROCESSED', processed_by = v_emp_name
        WHERE order_id = p_order_id;
    END process_order;

    PROCEDURE apply_discount(p_order_id NUMBER, p_total NUMBER) IS
        v_discount NUMBER;
    BEGIN
        v_discount := p_total * 0.1;
        UPDATE orders SET discount = v_discount WHERE order_id = p_order_id;
        log_action('DISCOUNT_APPLIED: ' || p_order_id);
        DBMS_OUTPUT.PUT_LINE('Discount applied: ' || v_discount);
    END apply_discount;

END emp_pkg;
/
