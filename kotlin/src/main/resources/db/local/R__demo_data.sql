INSERT INTO "timeDeposits" ("planType", balance, days)
SELECT *
FROM (VALUES
          ('basic',   12000.00, 31),
          ('student',  5000.00, 365),
          ('premium', 20000.00, 45),
          ('premium', 15000.00, 60)
     ) AS seed("planType", balance, days)
WHERE NOT EXISTS (SELECT 1 FROM "timeDeposits");


INSERT INTO withdrawals ("timeDepositId", amount, date)
SELECT d.id, w.amount, w.date
FROM (VALUES
          ('basic',   31,  250.00, DATE '2026-10-01'),
          ('basic',   31,  100.00, DATE '2026-10-10'),
          ('student', 365, 500.00, DATE '2026-09-20')
     ) AS w(plan_type, days, amount, date)
         JOIN "timeDeposits" d
              ON d."planType" = w.plan_type AND d.days = w.days
WHERE NOT EXISTS (SELECT 1 FROM withdrawals);