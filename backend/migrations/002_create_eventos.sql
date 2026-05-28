-- Migración para crear tabla eventos y columna evento_id en reservaciones
-- Ejecutar manualmente en la base de datos (Postgres/Neon)

CREATE TABLE IF NOT EXISTS eventos (
  id SERIAL PRIMARY KEY,
  titulo VARCHAR(180) NOT NULL,
  tipo_evento VARCHAR(80) NOT NULL DEFAULT 'Evento general',
  fecha DATE NOT NULL,
  lugar VARCHAR(180) NOT NULL,
  descripcion TEXT,
  capacidad INTEGER,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Agregar columna evento_id a reservaciones (opcional para compatibilidad)
ALTER TABLE reservaciones
  ADD COLUMN IF NOT EXISTS evento_id INTEGER REFERENCES eventos(id);

CREATE INDEX IF NOT EXISTS ix_reservaciones_evento_id ON reservaciones (evento_id);

