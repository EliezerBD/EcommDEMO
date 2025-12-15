-- Script de inicialización de productos para demo
-- Se ejecuta automáticamente al inicio de la aplicación (solo en desarrollo)

-- Limpiar tabla antes de insertar (evita duplicados)
DELETE FROM productos;

-- Electrónica
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('Laptop Gaming MSI', 'Laptop de alto rendimiento con procesador Intel i9, 32GB RAM, RTX 4070, pantalla 17" 144Hz', 1899.99, 15, 1, 'LAP-MSI-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPhone 15 Pro Max', 'Smartphone Apple con chip A17 Pro, cámara de 48MP, pantalla Super Retina XDR de 6.7", 256GB', 1299.99, 25, 1, 'PHN-APPL-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Samsung Galaxy S24 Ultra', 'Smartphone premium con S Pen, cámara de 200MP, pantalla AMOLED 6.8", 512GB', 1199.99, 20, 1, 'PHN-SAMS-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MacBook Pro M3 14"', 'Laptop profesional Apple con chip M3, 16GB RAM, SSD 512GB, pantalla Liquid Retina XDR', 2199.99, 10, 1, 'LAP-APPL-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sony WH-1000XM5', 'Audífonos inalámbricos con cancelación de ruido líder en la industria, 30hrs batería', 399.99, 50, 1, 'AUD-SONY-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPad Air M2', 'Tablet Apple con chip M2, pantalla Liquid Retina 11", Apple Pencil compatible, 128GB', 749.99, 30, 1, 'TAB-APPL-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Nintendo Switch OLED', 'Consola de videojuegos híbrida con pantalla OLED de 7", 64GB almacenamiento', 349.99, 40, 1, 'GAM-NINT-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PlayStation 5 Slim', 'Consola de nueva generación con SSD 1TB, ray tracing, 4K 120fps', 499.99, 8, 1, 'GAM-SONY-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Ropa y Moda
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('Zapatillas Nike Air Max 270', 'Zapatillas deportivas con tecnología Air Max, suela de espuma, diseño moderno', 159.99, 60, 2, 'ZAP-NIKE-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jeans Levi''s 501 Original', 'Jeans clásicos de corte recto, 100% algodón, disponibles en varios colores', 89.99, 100, 2, 'JEA-LEVI-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chaqueta North Face', 'Chaqueta impermeable para exteriores, tecnología DryVent, capucha ajustable', 249.99, 35, 2, 'CHA-NORT-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vestido Zara Elegante', 'Vestido de noche elegante, tela premium, diseño contemporáneo, talla S-XL', 79.99, 45, 2, 'VES-ZARA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sudadera Adidas Original', 'Sudadera deportiva con capucha, logo bordado, 80% algodón 20% poliéster', 69.99, 80, 2, 'SUD-ADID-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Camisa Tommy Hilfiger', 'Camisa casual de manga larga, 100% algodón, corte slim fit', 85.99, 55, 2, 'CAM-TOMM-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Hogar y Cocina
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('Cafetera Nespresso Vertuo', 'Máquina de café expreso y americano, sistema de cápsulas, 5 tamaños de taza', 179.99, 25, 3, 'CAF-NESP-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Licuadora Vitamix E310', 'Licuadora profesional de alto rendimiento, 10 velocidades, motor 2HP', 349.99, 18, 3, 'LIC-VITA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Set de Sartenes Tefal', 'Juego de 5 sartenes antiadherentes, aptas para inducción, libre de PFOA', 129.99, 30, 3, 'SAR-TEFA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Aspiradora Robot Roomba', 'Robot de limpieza inteligente, mapeo inteligente, compatible con app móvil', 499.99, 15, 3, 'ASP-ROOM-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Juego de Sábanas King', 'Sábanas de algodón egipcio 600 hilos, juego completo para cama king size', 149.99, 40, 3, 'SAB-KING-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lámpara LED Inteligente', 'Lámpara de escritorio LED regulable, 16 millones de colores, control por app', 59.99, 65, 3, 'LAM-SMART-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Deportes y Aire Libre
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('Bicicleta de Montaña Trek', 'Mountain bike con suspensión completa, 27 velocidades, frenos de disco hidráulicos', 1299.99, 12, 4, 'BIC-TREK-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tienda de Campaña Coleman', 'Tienda para 4 personas, impermeable, fácil instalación, bolsa de transporte incluida', 199.99, 22, 4, 'TIE-COLE-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pelota de Fútbol Adidas', 'Balón oficial de competición, costura térmica, tamaño 5', 39.99, 150, 4, 'PEL-ADID-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tabla de Surf Quiksilver', 'Tabla de surf profesional 7''6", epoxy, acabado brillante, incluye quillas', 549.99, 8, 4, 'TAB-QUIK-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Set de Pesas Bowflex', 'Set de mancuernas ajustables 2-24kg, compactas, ideal para gimnasio en casa', 399.99, 20, 4, 'PES-BOWF-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Esterilla de Yoga Premium', 'Tapete de yoga antideslizante, 6mm grosor, material eco-friendly, incluye correa', 49.99, 75, 4, 'EST-YOGA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Libros y Multimedia
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('El Quijote - Edición Ilustrada', 'Edición de lujo con ilustraciones originales, tapa dura, papel premium', 45.99, 50, 5, 'LIB-QUIJ-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Curso Completo de Python', 'Libro técnico con ejercicios prácticos, desde principiante a avanzado', 59.99, 40, 5, 'LIB-PYTH-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vinilo The Beatles - Abbey Road', 'Disco de vinilo remasterizado, 180g, incluye póster y notas de producción', 34.99, 30, 5, 'VIN-BEAT-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Box Set Harry Potter', 'Colección completa de 7 libros en caja especial, edición de coleccionista', 129.99, 25, 5, 'LIB-HARR-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kindle Paperwhite', 'E-reader con pantalla antirreflejos, 16GB, resistente al agua, batería semanas', 139.99, 35, 5, 'ERE-KIND-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Juguetes y Bebés
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('LEGO Star Wars Millennium Falcon', 'Set de construcción con 7541 piezas, edición de coleccionista, minifiguras incluidas', 849.99, 10, 6, 'JUG-LEGO-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cochecito Bebé Bugaboo', 'Carreola premium convertible, estructura de aluminio, incluye capazo y silla', 999.99, 15, 6, 'BEB-BUGA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Muñeca Barbie Dreamhouse', 'Casa de muñecas de 3 pisos con 8 habitaciones, luces y sonidos, más de 70 piezas', 199.99, 28, 6, 'JUG-BARB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pañales Pampers Talla 3', 'Paquete económico de 156 pañales, tecnología de absorción avanzada', 49.99, 200, 6, 'BEB-PAMP-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Monitor Bebé con Cámara', 'Monitor de video HD con visión nocturna, comunicación bidireccional, sensor de temperatura', 149.99, 32, 6, 'BEB-MONI-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Belleza y Cuidado Personal
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('Perfume Chanel No. 5', 'Fragancia icónica femenina, eau de parfum 100ml, notas florales elegantes', 159.99, 45, 7, 'PER-CHAN-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Crema Facial La Mer', 'Crema hidratante de lujo, tecnología Miracle Broth, 60ml', 380.99, 20, 7, 'CRE-LAME-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Set de Maquillaje MAC', 'Kit profesional con 12 sombras, 2 rubores, 3 labiales, espejo y brochas', 129.99, 35, 7, 'MAQ-MAC-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Afeitadora Eléctrica Philips', 'Afeitadora de 3 cabezales, húmeda y seca, 1 hora de autonomía', 119.99, 40, 7, 'AFE-PHIL-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Secadora de Pelo Dyson', 'Secador supersónico con tecnología Air Multiplier, control inteligente de calor', 429.99, 18, 7, 'SEC-DYSO-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kit Cuidado de Barba', 'Set completo: aceite, bálsamo, cepillo, peine y tijeras en estuche de madera', 69.99, 55, 7, 'KIT-BARB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Alimentos y Bebidas
INSERT INTO productos (name, description, price, stock, category_id, sku, active, created_at, updated_at) VALUES
('Café Premium Arábica 1kg', 'Café en grano de altura, tueste medio, notas de chocolate y caramelo', 24.99, 120, 8, 'CAF-ARAB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Aceite de Oliva Extra Virgen', 'AOVE premium de primera extracción en frío, botella de vidrio 1L', 18.99, 85, 8, 'ACE-OLIV-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chocolate Lindt Excellence', 'Tableta de chocolate negro 85% cacao, 100g, origen Suiza', 6.99, 200, 8, 'CHO-LIND-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vino Tinto Reserva', 'Vino tinto crianza D.O. Rioja, 75cl, 14% vol, añada 2018', 29.99, 60, 8, 'VIN-RESER-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Miel Orgánica Pura', 'Miel de flores silvestres 100% natural, frasco de vidrio 500g', 15.99, 95, 8, 'MIE-ORGA-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Té Verde Matcha Premium', 'Polvo de té matcha ceremonial japonés, 100g, rico en antioxidantes', 34.99, 48, 8, 'TE-MATC-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
