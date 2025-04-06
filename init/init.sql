-- Crear la base de datos
CREATE DATABASE banco_service_db;

-- Cambiar a esa base para crear los schemas
\connect banco_service_db;

-- Schema del microservicio de persona
CREATE SCHEMA IF NOT EXISTS cliente;

-- Schema del microservicio de cuenta
CREATE SCHEMA IF NOT EXISTS cuenta_movimiento;
