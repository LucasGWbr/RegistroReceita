TRUNCATE TABLE receitas RESTART IDENTITY CASCADE;

INSERT INTO recipes (name, description, price, recipe_type) VALUES
                                                                ('Bolo de Chocolate',      'Bolo fofinho com cobertura de ganache',           25.00, 'Sobremesa'),
                                                                ('Frango Grelhado',        'Peito de frango temperado na chapa com ervas',    18.50, 'Prato Principal'),
                                                                ('Sopa de Legumes',        'Sopa cremosa com cenoura, batata e chuchu',       12.00, 'Sopa'),
                                                                ('Pão de Queijo',          'Pão de queijo mineiro com queijo meia cura',       8.00, 'Lanche'),
                                                                ('Lasanha Bolonhesa',      'Lasanha com molho de carne e bechamel',           32.00, 'Prato Principal'),
                                                                ('Vitamina de Banana',     'Vitamina cremosa com banana, leite e mel',         9.50, 'Bebida'),
                                                                ('Salada Caesar',          'Salada com alface, croutons e molho caesar',      15.00, 'Entrada'),
                                                                ('Arroz de Forno',         'Arroz gratinado com frango desfiado e requeijão', 22.00, 'Prato Principal'),
                                                                ('Mousse de Maracujá',     'Mousse leve e cremoso de maracujá',               14.00, 'Sobremesa'),
                                                                ('Omelete de Legumes',     'Omelete com abobrinha, tomate e queijo',          13.50, 'Lanche');