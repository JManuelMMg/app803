from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import text
from sqlalchemy.orm import Session

from config.db import Base, SessionLocal, engine
from models.models import Usuario
from routers.auth import hash_password
from routers import auth, reservaciones

app = FastAPI(title="API de Eventos y Reservaciones")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(reservaciones.router)


def create_admin_if_needed(db: Session) -> None:
    admin = db.query(Usuario).filter(Usuario.correo == "admin@gmail.com").first()
    if admin is not None:
        return

    db.add(
        Usuario(
            nombre="Administrador",
            correo="admin@gmail.com",
            password=hash_password("123456"),
            rol="admin",
        )
    )
    db.commit()


def ensure_reservacion_columns(db: Session) -> None:
    statements = [
        (
            "CREATE TABLE IF NOT EXISTS eventos ("
            "id SERIAL PRIMARY KEY, "
            "titulo VARCHAR(180) NOT NULL, "
            "tipo_evento VARCHAR(80) NOT NULL DEFAULT 'Evento general', "
            "fecha DATE NOT NULL, "
            "lugar VARCHAR(180) NOT NULL, "
            "descripcion TEXT, "
            "capacidad INTEGER, "
            "created_at TIMESTAMP WITH TIME ZONE DEFAULT now()"
            ")"
        ),
        (
            "ALTER TABLE reservaciones "
            "ADD COLUMN IF NOT EXISTS tipo_evento VARCHAR(80) NOT NULL DEFAULT 'Evento general'"
        ),
        (
            "ALTER TABLE reservaciones "
            "ADD COLUMN IF NOT EXISTS evento_id INTEGER REFERENCES eventos(id)"
        ),
        (
            "CREATE INDEX IF NOT EXISTS ix_reservaciones_evento_id "
            "ON reservaciones (evento_id)"
        ),
        (
            "UPDATE reservaciones "
            "SET tipo_evento = 'Evento general' "
            "WHERE tipo_evento IS NULL OR btrim(tipo_evento) = ''"
        ),
        (
            "ALTER TABLE reservaciones "
            "ALTER COLUMN tipo_evento SET DEFAULT 'Evento general'"
        ),
        (
            "ALTER TABLE reservaciones "
            "ALTER COLUMN tipo_evento SET NOT NULL"
        ),
        (
            "CREATE INDEX IF NOT EXISTS ix_reservaciones_tipo_evento "
            "ON reservaciones (tipo_evento)"
        ),
        (
            "CREATE INDEX IF NOT EXISTS ix_reservaciones_tipo_fecha "
            "ON reservaciones (tipo_evento, fecha)"
        ),
    ]
    for statement in statements:
        db.execute(text(statement))
    db.commit()


@app.on_event("startup")
def on_startup() -> None:
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()
    try:
        ensure_reservacion_columns(db)
        create_admin_if_needed(db)
    finally:
        db.close()


@app.get("/")
def root():
    return {"status": "ok", "message": "API de reservaciones activa"}
