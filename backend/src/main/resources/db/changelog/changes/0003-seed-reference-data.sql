--liquibase formatted sql

--changeset elie:0020-seed-teams
INSERT INTO teams (name) VALUES ('Engineering'), ('Sales'), ('Operations');

--changeset elie:0021-seed-accounts
INSERT INTO accounts (code, name, type, normal_side) VALUES
    ('1000', 'Cash',              'ASSET',     'DEBIT'),
    ('2000', 'Accounts Payable',  'LIABILITY', 'CREDIT'),
    ('5000', 'Travel Expense',    'EXPENSE',   'DEBIT'),
    ('5100', 'Meals Expense',     'EXPENSE',   'DEBIT'),
    ('5200', 'Software Expense',  'EXPENSE',   'DEBIT'),
    ('5300', 'Equipment Expense', 'EXPENSE',   'DEBIT'),
    ('5400', 'Office Expense',    'EXPENSE',   'DEBIT'),
    ('5900', 'Other Expense',     'EXPENSE',   'DEBIT');
