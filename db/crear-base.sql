-- Ejecutar como administrador MySQL local; no usar la base anterior.
CREATE DATABASE IF NOT EXISTS gaselvolcan_v2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Crear el usuario con una contraseña propia y otorgar permisos solo sobre gaselvolcan_v2.
-- CREATE USER 'gaselvolcan'@'localhost' IDENTIFIED BY 'REEMPLAZAR_CON_CLAVE_PROPIA';
-- GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON gaselvolcan_v2.* TO 'gaselvolcan'@'localhost';
-- Spring Data JPA crea las cinco tablas al iniciar (ddl-auto=update).
