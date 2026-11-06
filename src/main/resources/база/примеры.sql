INSERT INTO спектакли (название, длительность_минут, возрастное_ограничение)
VALUES ('Ревизор', 120, 12), ('Щелкунчик', 100, 6);

INSERT INTO залы (название, число_мест)
VALUES ('Большой зал', 100), ('Малый зал', 30);

INSERT INTO показы (спектакль_id, зал_id, начало, базовая_цена, статус)
VALUES
    ((SELECT id FROM спектакли WHERE название = 'Ревизор'),
     (SELECT id FROM залы WHERE название = 'Большой зал'),
     DATEADD('DAY', 7, CURRENT_TIMESTAMP), 500, 'ЗАПЛАНИРОВАН'),
    ((SELECT id FROM спектакли WHERE название = 'Щелкунчик'),
     (SELECT id FROM залы WHERE название = 'Малый зал'),
     DATEADD('DAY', -1, CURRENT_TIMESTAMP), 300, 'ЗАПЛАНИРОВАН');

INSERT INTO билеты (показ_id, номер_места, пользователь_id, статус, цена, сумма_возврата)
VALUES
    ((SELECT id FROM показы WHERE спектакль_id = (SELECT id FROM спектакли WHERE название = 'Ревизор')),
     1, (SELECT id FROM пользователи WHERE имя = 'Гришин Никита'), 'БРОНЬ', 0, 0),
    ((SELECT id FROM показы WHERE спектакль_id = (SELECT id FROM спектакли WHERE название = 'Ревизор')),
     2, (SELECT id FROM пользователи WHERE имя = 'Анна'), 'КУПЛЕН', 500, 0),
    ((SELECT id FROM показы WHERE спектакль_id = (SELECT id FROM спектакли WHERE название = 'Щелкунчик')),
     1, (SELECT id FROM пользователи WHERE имя = 'Анна'), 'КУПЛЕН', 300, 0);
