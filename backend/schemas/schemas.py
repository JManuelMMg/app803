from datetime import date, datetime
from typing import Any

from pydantic import BaseModel, ConfigDict, EmailStr


class LoginRequest(BaseModel):
    correo: EmailStr
    password: str


class RegisterRequest(BaseModel):
    nombre: str
    correo: EmailStr
    password: str


class Usuario(BaseModel):
    id: int
    nombre: str
    correo: EmailStr
    rol: str
    created_at: datetime | None = None

    model_config = ConfigDict(from_attributes=True)


class TokenData(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_in: int = 7200


class LoginResponse(BaseModel):
    token: TokenData
    usuario: Usuario
    message: str = "Login exitoso"


class ReservacionBase(BaseModel):
    tipo_evento: str = "Evento general"
    # Cuando se reserva desde el catálogo, usar event_id. Si no, proveer los campos abajo.
    event_id: int | None = None
    evento: str | None = None
    fecha: date | None = None
    lugar: str | None = None
    descripcion: str | None = None


class ReservacionCreate(ReservacionBase):
    # Validación adicional aplicada en el router: si no se proporciona event_id,
    # entonces 'evento', 'fecha' y 'lugar' deben enviarse.
    pass


class ReservacionUpdate(BaseModel):
    tipo_evento: str | None = None
    event_id: int | None = None
    evento: str | None = None
    fecha: date | None = None
    lugar: str | None = None
    descripcion: str | None = None


class Reservacion(ReservacionBase):
    id: int
    usuario_id: int | None = None
    usuario_nombre: str | None = None
    usuario_correo: str | None = None
    created_at: datetime | None = None
    updated_at: datetime | None = None

    model_config = ConfigDict(from_attributes=True)



class EventoBase(BaseModel):
    titulo: str
    tipo_evento: str = "Evento general"
    fecha: date
    lugar: str
    descripcion: str | None = None
    capacidad: int | None = None


class EventoCreate(EventoBase):
    pass


class Evento(EventoBase):
    id: int
    created_at: datetime | None = None

    model_config = ConfigDict(from_attributes=True)


class ReservacionListResponse(BaseModel):
    total: int
    reservaciones: list[Reservacion]


class DashboardResponse(BaseModel):
    total_reservaciones: int
    total_usuarios: int
    eventos_por_tipo: list[dict[str, Any]]
    reservaciones_por_mes: list[dict[str, Any]]
    eventos_mas_populares: list[dict[str, Any]]
    lugares_mas_reservados: list[dict[str, Any]]
    ocupacion_eventos: list[dict[str, Any]]
