CREATE TABLE "timeDeposits"
(
    id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "planType" VARCHAR(32) NOT NULL,
    days       INTEGER NOT NULL,
    balance    NUMERIC(19, 2) NOT NULL
);

CREATE TABLE withdrawals
(
    id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "timeDepositId" INTEGER NOT NULL,
    amount          NUMERIC(19, 2) NOT NULL,
    date            DATE NOT NULL,

    CONSTRAINT fk_withdrawal_time_deposit
        FOREIGN KEY ("timeDepositId")
            REFERENCES "timeDeposits" (id)
);

CREATE INDEX idx_withdrawals_time_deposit_id
    ON withdrawals ("timeDepositId");