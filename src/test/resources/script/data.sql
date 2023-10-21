INSERT INTO account (id, name, pin, balance)
VALUES (1, 'first', '$2a$12$iMwtmceRgXPRKdxHGxQhVuLM4IY/NKPd633Fy0RMiOU9.XL2rWUQu', '1000'),
       (2, 'second', '$2a$12$/7xmnZ6AXSlGvpyItyDSseQYCZsdg/aTVkRRB4z06sWg.Ttlb.lEy', '500');
ALTER TABLE account
    ALTER COLUMN id RESTART WITH 3;

