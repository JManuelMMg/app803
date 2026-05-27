-- Migracion para clasificar reservaciones por tipo de evento.
-- Ejecutar en Postgres/Neon si se quiere aplicar manualmente.

ALTER TABLE reservaciones
ADD COLUMN IF NOT EXISTS tipo_evento VARCHAR(80) NOT NULL DEFAULT 'Evento general';

UPDATE reservaciones
SET tipo_evento = 'Evento general'
WHERE tipo_evento IS NULL OR btrim(tipo_evento) = '';

ALTER TABLE reservaciones
ALTER COLUMN tipo_evento SET DEFAULT 'Evento general';

ALTER TABLE reservaciones
ALTER COLUMN tipo_evento SET NOT NULL;

CREATE INDEX IF NOT EXISTS ix_reservaciones_tipo_evento
ON reservaciones (tipo_evento);

CREATE INDEX IF NOT EXISTS ix_reservaciones_tipo_fecha
ON reservaciones (tipo_evento, fecha);
