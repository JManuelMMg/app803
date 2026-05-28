from sqlalchemy import Date, DateTime, ForeignKey, String, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from config.db import Base


class Usuario(Base):
    __tablename__ = "usuarios"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    nombre: Mapped[str] = mapped_column(String(120), nullable=False)
    correo: Mapped[str] = mapped_column(String(180), unique=True, index=True, nullable=False)
    password: Mapped[str] = mapped_column(String(255), nullable=False)
    rol: Mapped[str] = mapped_column(String(20), nullable=False, default="normal")
    created_at: Mapped[str] = mapped_column(DateTime(timezone=True), server_default=func.now())

    reservaciones: Mapped[list["Reservacion"]] = relationship(
        back_populates="usuario",
        cascade="all, delete-orphan",
    )


class Reservacion(Base):
    __tablename__ = "reservaciones"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    tipo_evento: Mapped[str] = mapped_column(String(80), nullable=False, default="Evento general")
    evento: Mapped[str] = mapped_column(String(180), nullable=False)
    fecha: Mapped[str] = mapped_column(Date, nullable=False)
    lugar: Mapped[str] = mapped_column(String(180), nullable=False)
    descripcion: Mapped[str | None] = mapped_column(String(500), nullable=True)
    usuario_id: Mapped[int | None] = mapped_column(ForeignKey("usuarios.id"), nullable=True)
    # Nuevo: referencia al evento en catálogo (opcional para compatibilidad)
    evento_id: Mapped[int | None] = mapped_column(ForeignKey("eventos.id"), nullable=True, index=True)
    created_at: Mapped[str] = mapped_column(DateTime(timezone=True), server_default=func.now())
    updated_at: Mapped[str] = mapped_column(
        DateTime(timezone=True),
        server_default=func.now(),
        onupdate=func.now(),
    )

    usuario: Mapped[Usuario | None] = relationship(back_populates="reservaciones")

    # relacion opcional al evento del catálogo
    evento_rel: Mapped["Evento" | None] = relationship(back_populates="reservaciones")

    @property
    def usuario_nombre(self) -> str | None:
        return self.usuario.nombre if self.usuario else None

    @property
    def usuario_correo(self) -> str | None:
        return self.usuario.correo if self.usuario else None



class Evento(Base):
    __tablename__ = "eventos"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    titulo: Mapped[str] = mapped_column(String(180), nullable=False)
    tipo_evento: Mapped[str] = mapped_column(String(80), nullable=False, default="Evento general")
    fecha: Mapped[str] = mapped_column(Date, nullable=False)
    lugar: Mapped[str] = mapped_column(String(180), nullable=False)
    descripcion: Mapped[str | None] = mapped_column(String(1000), nullable=True)
    capacidad: Mapped[int | None] = mapped_column(nullable=True)
    created_at: Mapped[str] = mapped_column(DateTime(timezone=True), server_default=func.now())

    reservaciones: Mapped[list[Reservacion]] = relationship(back_populates="evento_rel", cascade="all, delete-orphan")

