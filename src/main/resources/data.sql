INSERT INTO pinball.pinball_machines
(model_name, manufacturer, rarity_tier, image_url, historical_summary, release_year, units_produced, restoration_cost_usd, condition_rating, is_fully_functional, has_multiball)
VALUES
    ('Twilight Zone', 'Bally', 'Leyenda', 'https://www.bahatech.cl/pinball/galaxy.svg', 'Basada en la mítica serie de televisión. Destacada por contener la bola de cerámica Powerball y una rampa con imanes de alta potencia.', 1993, 15235, 1450.00, 4.9, true, true),
    ('The Addams Family', 'Bally', 'Edición Limitada', 'https://www.bahatech.cl/pinball/hauntedmadness.svg', 'La máquina de pinball más vendida de la historia. Diseñada por Pat Lawlor, incluye la famosa mano Thing que atrapa la bola.', 1992, 20270, 2100.50, 4.7, true, true)
    ON CONFLICT (model_name) DO NOTHING;