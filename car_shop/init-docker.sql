-- ============================================
-- ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ (выполняется 1 раз)
-- ============================================

-- 1. Создаём схему (если используем не public)
CREATE SCHEMA IF NOT EXISTS car_shop;

-- 2. Устанавливаем владельца
ALTER SCHEMA car_shop OWNER TO postgres;

-- 3. Создаём расширения
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA car_shop;

-- 4. Даём права
GRANT ALL PRIVILEGES ON SCHEMA car_shop TO postgres;
GRANT USAGE ON SCHEMA car_shop TO postgres;

-- 5. Устанавливаем схему по умолчанию
SET search_path TO car_shop, public;