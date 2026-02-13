INSERT INTO data_source (id, kind, owner, base_url, auth_required, notes)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'MUNICIPALITY_HTML',
    'Oslo kommune',
    'https://www.oslo.kommune.no/natur-kultur-og-fritid/tur-og-friluftsliv/badeplasser-og-temperaturer/',
    false,
    'Oslo kommune bathing water quality page with HTML tables'
);

INSERT INTO municipality (id, name, code, county)
VALUES (
    'b0000000-0000-0000-0000-000000000001',
    'Oslo',
    '0301',
    'Oslo'
);

INSERT INTO scraper (id, name, source_id, version, enabled, schedule)
VALUES (
    'c0000000-0000-0000-0000-000000000001',
    'oslo-kommune',
    'a0000000-0000-0000-0000-000000000001',
    '1.0.0',
    true,
    '0 6 * * *'
);

INSERT INTO bathing_site (id, municipality_id, name, slug, water_type, lat, lon, is_active)
VALUES
    ('d0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Sørenga sjøbad', 'soerenga-sjoebad', 'SALT', 59.9013, 10.7522, true),
    ('d0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'Huk', 'huk', 'SALT', 59.8956, 10.6736, true),
    ('d0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 'Hvervenbukta', 'hvervenbukta', 'SALT', 59.8283, 10.7835, true),
    ('d0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000001', 'Langøyene', 'langoeyene', 'SALT', 59.8667, 10.7167, true),
    ('d0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000001', 'Sognsvann', 'sognsvann', 'FRESH', 59.9689, 10.7267, true),
    ('d0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000001', 'Bogstad', 'bogstad', 'FRESH', 59.9647, 10.6378, true),
    ('d0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000001', 'Hovedøya', 'hovedoeya', 'SALT', 59.8939, 10.7281, true),
    ('d0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000001', 'Tjuvholmen', 'tjuvholmen', 'SALT', 59.9069, 10.7208, true);
