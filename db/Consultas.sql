-- Consultas del historial y las estadísticas.
-- Ejecutar en Workbench para probarlas. En el código Java se usan con PreparedStatement.
USE poker;

-- Historial de manos
SELECT m.id, m.fecha, m.bote, m.combinacion_ganadora, j.nombre AS ganador
FROM mano m JOIN jugador j ON j.id = m.ganador_id
ORDER BY m.fecha DESC;

-- Detalle de acciones de una mano
-- (en Java: WHERE a.mano_id = ? ; aquí se usa 1 solo para probar en Workbench)
SELECT a.orden, j.nombre, a.tipo, a.monto
FROM accion a JOIN jugador j ON j.id = a.jugador_id
WHERE a.mano_id = 1
ORDER BY a.orden;

-- Ranking de victorias
SELECT j.nombre, COUNT(*) AS victorias
FROM mano m JOIN jugador j ON j.id = m.ganador_id
GROUP BY j.id, j.nombre
ORDER BY victorias DESC;

-- Mayor bote
SELECT m.id, m.bote, j.nombre
FROM mano m JOIN jugador j ON j.id = m.ganador_id
ORDER BY m.bote DESC
LIMIT 1;

-- Bot con más victorias
SELECT j.nombre, COUNT(*) AS victorias
FROM mano m JOIN jugador j ON j.id = m.ganador_id
WHERE j.tipo = 'BOT'
GROUP BY j.id, j.nombre
ORDER BY victorias DESC
LIMIT 1;
