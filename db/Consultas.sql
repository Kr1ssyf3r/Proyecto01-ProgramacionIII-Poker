
-- Historial de manos
SELECT m.id, m.fecha, m.bote, m.combinacion_ganadora, j.nombre AS ganador
FROM mano m JOIN jugador j ON j.id = m.ganador_id
ORDER BY m.fecha DESC;

-- Detalle de acciones de una mano
SELECT a.orden, j.nombre, a.tipo, a.monto
FROM accion a JOIN jugador j ON j.id = a.jugador_id
WHERE a.mano_id = ? ORDER BY a.orden;

-- Ranking de victorias
SELECT j.nombre, COUNT(*) AS victorias
FROM mano m JOIN jugador j ON j.id = m.ganador_id
GROUP BY j.id, j.nombre ORDER BY victorias DESC;

-- Mayor bote
SELECT m.id, m.bote, j.nombre
FROM mano m JOIN jugador j ON j.id = m.ganador_id
ORDER BY m.bote DESC LIMIT 1;

-- Bot con más victorias
SELECT j.nombre, COUNT(*) AS victorias
FROM mano m JOIN jugador j ON j.id = m.ganador_id
WHERE j.tipo = 'BOT'
GROUP BY j.id, j.nombre ORDER BY victorias DESC LIMIT 1;