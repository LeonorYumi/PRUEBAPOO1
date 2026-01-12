use bt2unnanzjj0wfsyfd2e;


-- 1. TABLA: ROLES 
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL -- 'ADMIN', 'ANALISTA'
);

-- 2. TABLA: USUARIOS 
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cedula VARCHAR(10) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL, 
    password_hash VARCHAR(255) NOT NULL, -- SHA-256
    rol_id INT NOT NULL, -- Relación con tabla roles
    activo TINYINT(1) DEFAULT 1, -- 1 para activo, 0 para inactivo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- 3. TABLA: SOLICITANTES 
CREATE TABLE IF NOT EXISTS solicitantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(10) UNIQUE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    tipo_licencia VARCHAR(20),
    fecha_solicitud DATE,
    created_by INT,
    fecha_creacion DATE,
    FOREIGN KEY (created_by) REFERENCES usuarios(id)
);

-- 4. TABLA: TRAMITES 
CREATE TABLE IF NOT EXISTS tramites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    solicitante_id INT NOT NULL,
    tipo_licencia VARCHAR(20) NOT NULL, 
    fecha_solicitud DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL, 
    created_by INT NULL,
    created_at DATETIME NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (solicitante_id) REFERENCES solicitantes(id),
    FOREIGN KEY (created_by) REFERENCES usuarios(id)
);

-- 5. TABLA: EXAMENES 
CREATE TABLE IF NOT EXISTS examenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tramite_id INT NOT NULL,
    nota_teorica DECIMAL(4,2) NOT NULL,
    nota_practica DECIMAL(4,2) NOT NULL,
    aprobado TINYINT(1) NOT NULL,
    created_by INT NULL,
    FOREIGN KEY (tramite_id) REFERENCES tramites(id)
);

-- 6. TABLA: REQUISITOS 
CREATE TABLE IF NOT EXISTS requisitos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tramite_id INT NOT NULL,
    certificado_medico TINYINT(1) NOT NULL,
    pago TINYINT(1) NOT NULL,
    multas TINYINT(1) NOT NULL,
    observaciones TEXT,
    created_by INT NULL,
    FOREIGN KEY (tramite_id) REFERENCES tramites(id)
);

-- 7. TABLA: LICENCIAS 
CREATE TABLE IF NOT EXISTS licencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL, 
    tramite_id INT NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    created_by INT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (tramite_id) REFERENCES tramites(id)
);

SET FOREIGN_KEY_CHECKS = 1;

-- INSERTS DE CONFIGURACIÓN INICIAL
INSERT INTO roles (id, nombre) VALUES (1, 'ADMIN'), (2, 'ANALISTA');

-- Usuarios DEMO
INSERT INTO usuarios (nombre, cedula, username, password_hash, rol_id, activo) VALUES 
('Administrador General', '0000000000', 'admin', SHA2('admin123', 256), 1, 1),
('Analista Uno', '1111111111', 'analista', SHA2('analista123', 256), 2, 1);

select *from solicitantes;
truncate table solicitantes ; 
select *from tramites ;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE solicitantes;
SET FOREIGN_KEY_CHECKS = 1;
select *from reportes;
show tables;
select *from usuarios;
select *from solicitantes;
select *from tramites;
select *from requisitos;
select *from examenes;
select *from roles;
show  tables;
SET FOREIGN_KEY_CHECKS = 0;