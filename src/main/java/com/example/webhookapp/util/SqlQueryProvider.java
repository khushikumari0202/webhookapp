package com.example.webhookapp.util;

public class SqlQueryProvider {

    public static String getQuestion1Query() {
        return """
WITH filtered_payments AS (
    SELECT 
        p.EMP_ID,
        p.AMOUNT,
        e.DEPARTMENT,
        e.FIRST_NAME,
        e.LAST_NAME,
        e.DOB
    FROM PAYMENTS p
    JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
    WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) <> 1
),
ranked_data AS (
    SELECT
        fp.DEPARTMENT,
        fp.FIRST_NAME,
        fp.LAST_NAME,
        fp.AMOUNT AS SALARY,
        FLOOR(DATEDIFF(CURDATE(), fp.DOB) / 365) AS AGE,
        ROW_NUMBER() OVER (PARTITION BY fp.DEPARTMENT ORDER BY fp.AMOUNT DESC) AS rn
    FROM filtered_payments fp
)
SELECT 
    d.DEPARTMENT_NAME,
    rd.SALARY,
    CONCAT(rd.FIRST_NAME, ' ', rd.LAST_NAME) AS EMPLOYEE_NAME,
    rd.AGE
FROM ranked_data rd
JOIN DEPARTMENT d ON rd.DEPARTMENT = d.DEPARTMENT_ID
WHERE rd.rn = 1
ORDER BY d.DEPARTMENT_ID DESC;
""";
    }

    public static String getQuestion2Query() {
        return """
WITH salary_data AS (
    SELECT 
        e.EMP_ID,
        e.FIRST_NAME,
        e.LAST_NAME,
        e.DOB,
        e.DEPARTMENT,
        p.AMOUNT
    FROM PAYMENTS p
    JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
    WHERE p.AMOUNT > 70000
),
age_calc AS (
    SELECT
        sd.DEPARTMENT,
        sd.FIRST_NAME,
        sd.LAST_NAME,
        FLOOR(DATEDIFF(CURDATE(), sd.DOB) / 365) AS AGE
    FROM salary_data sd
),
grouped AS (
    SELECT
        a.DEPARTMENT,
        AVG(a.AGE) AS AVERAGE_AGE,
        GROUP_CONCAT(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME) 
                     ORDER BY a.FIRST_NAME, a.LAST_NAME 
                     SEPARATOR ', ' ) AS EMPLOYEE_LIST
    FROM age_calc a
    GROUP BY a.DEPARTMENT
)
SELECT 
    d.DEPARTMENT_NAME,
    g.AVERAGE_AGE,
    g.EMPLOYEE_LIST
FROM grouped g
JOIN DEPARTMENT d ON g.DEPARTMENT = d.DEPARTMENT_ID
ORDER BY d.DEPARTMENT_ID DESC;
""";
    }
}