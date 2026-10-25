DROP DATABASE IF EXISTS POKERFICHAS;

CREATE DATABASE IF NOT EXISTS poker CHARACTER SET utf8mb4;
USE poker;

CREATE TABLE jugador (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(40) NOT NULL UNIQUE,
  tipo ENUM('HUMANO','BOT') NOT NULL DEFAULT 'HUMANO',
  fichas INT NOT NULL DEFAULT 500 CHECK (fichas >= 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mano (
  id INT AUTO_INCREMENT PRIMARY KEY,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  bote INT NOT NULL,
  combinacion_ganadora VARCHAR(30) NOT NULL,
  ganador_id INT NOT NULL,
  FOREIGN KEY (ganador_id) REFERENCES jugador(id)
);

CREATE TABLE participacion (
  mano_id INT NOT NULL,
  jugador_id INT NOT NULL,
  fichas_antes INT NOT NULL,
  fichas_despues INT NOT NULL,
  PRIMARY KEY (mano_id, jugador_id),
  FOREIGN KEY (mano_id) REFERENCES mano(id) ON DELETE CASCADE,
  FOREIGN KEY (jugador_id) REFERENCES jugador(id)
);

CREATE TABLE accion (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mano_id INT NOT NULL,
  jugador_id INT NOT NULL,
  orden INT NOT NULL,
  tipo ENUM('CHECK','CALL','FOLD','RAISE') NOT NULL,
  monto INT NOT NULL DEFAULT 0,
  FOREIGN KEY (mano_id) REFERENCES mano(id) ON DELETE CASCADE,
  FOREIGN KEY (jugador_id) REFERENCES jugador(id)
);

INSERT INTO jugador (nombre, tipo) VALUES ('Bot Conservador','BOT'), ('Bot Agresivo','BOT');

SELECT * FROM jugador;
