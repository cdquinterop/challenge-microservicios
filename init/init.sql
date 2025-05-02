-- Create the database
CREATE DATABASE bank_service_db;

-- Connect to the database
\connect bank_service_db;

-- Schema for the person microservice
CREATE SCHEMA IF NOT EXISTS customer;

-- Schema for the account microservice
CREATE SCHEMA IF NOT EXISTS account_transaction;

