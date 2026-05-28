from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import func
from sqlalchemy.orm import Session, joinedload

from config.db import get_db
from models.models import Evento as EventoModel
from models.models import Reservacion, Usuario
from routers.auth import get_current_user, require_admin
from schemas.schemas import (
    DashboardResponse,
    ReservacionCreate,
    ReservacionListResponse,
    Reservacion as ReservacionSchema,
    ReservacionUpdate,
    Evento as EventoSchema,
    EventoCreate,
)

router = APIRouter(prefix="/api", tags=["reservaciones"])


@router.get("/reservaciones", response_model=ReservacionListResponse)
def listar_reservaciones(
    skip: int = 0,
    limit: int = 50,
    db: Session = Depends(get_db),
    current_user: Usuario = Depends(get_current_user),
):
    query = db.query(Reservacion).options(joinedload(Reservacion.usuario))
    if current_user.rol != "admin":
        query = query.filter(Reservacion.usuario_id == current_user.id)

    total = query.count()
    reservaciones = (
        query
        .order_by(Reservacion.fecha.asc(), Reservacion.id.asc())
        .offset(skip)
        .limit(limit)
        .all()
    )
    return {"total": total, "reservaciones": reservaciones}


@router.post("/reservaciones", response_model=ReservacionSchema, status_code=status.HTTP_201_CREATED)
def crear_reservacion(
    reservacion: ReservacionCreate,
    db: Session = Depends(get_db),
    current_user: Usuario = Depends(get_current_user),
):
    data = reservacion.model_dump()

    # Si viene event_id, validar que exista y tomar los datos del evento
    event_id = data.get("event_id")
    if event_id is not None:
        evento_obj = db.get(EventoModel, event_id)
        if evento_obj is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Evento no encontrado")

        reservacion_existente = (
            db.query(Reservacion)
            .filter(
                Reservacion.evento_id == event_id,
                Reservacion.usuario_id == current_user.id,
            )
            .first()
        )
        if reservacion_existente is not None:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Ya tienes una reservación para este evento",
            )

        if evento_obj.capacidad is not None:
            total_evento = db.query(Reservacion).filter(Reservacion.evento_id == event_id).count()
            if total_evento >= evento_obj.capacidad:
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="El evento ya no tiene cupo disponible",
                )

        # Sobrescribimos los campos relevantes con los del catálogo para mantener consistencia
        data["evento"] = evento_obj.titulo
        data["tipo_evento"] = evento_obj.tipo_evento
        data["fecha"] = evento_obj.fecha
        data["lugar"] = evento_obj.lugar
        data["evento_id"] = event_id
        data.pop("event_id", None)

    # Si no hay event_id, requerimos los campos mínimos y limpiamos la clave pública.
    if event_id is None:
        if not data.get("evento") or not data.get("fecha") or not data.get("lugar"):
            raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail="Debe proveer event_id o los campos evento, fecha y lugar")
        data.pop("event_id", None)

    nueva = Reservacion(**data, usuario_id=current_user.id)
    db.add(nueva)
    db.commit()
    db.refresh(nueva)
    return nueva


# Endpoints para catálogo de eventos
@router.get("/events", response_model=list[EventoSchema])
def listar_eventos_publicos(db: Session = Depends(get_db)):
    eventos = db.query(EventoModel).order_by(EventoModel.fecha.asc()).all()
    return eventos


@router.post("/events", response_model=EventoSchema, status_code=status.HTTP_201_CREATED)
def crear_evento(evento: EventoCreate, db: Session = Depends(get_db), current_user: Usuario = Depends(require_admin)):
    nueva = EventoModel(**evento.model_dump())
    db.add(nueva)
    db.commit()
    db.refresh(nueva)
    return nueva


@router.get("/events/{event_id}", response_model=EventoSchema)
def obtener_evento(event_id: int, db: Session = Depends(get_db)):
    evento = db.get(EventoModel, event_id)
    if evento is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Evento no encontrado")
    return evento


@router.get("/reservaciones/{reservacion_id}", response_model=ReservacionSchema)
def obtener_reservacion(
    reservacion_id: int,
    db: Session = Depends(get_db),
    current_user: Usuario = Depends(get_current_user),
):
    reservacion = db.get(Reservacion, reservacion_id)
    if reservacion is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Reservación no encontrada")
    if current_user.rol != "admin" and reservacion.usuario_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="No puedes ver esta reservación")
    return reservacion


@router.put("/reservaciones/{reservacion_id}", response_model=ReservacionSchema)
def actualizar_reservacion(
    reservacion_id: int,
    data: ReservacionUpdate,
    db: Session = Depends(get_db),
    current_user: Usuario = Depends(require_admin),
):
    reservacion = db.get(Reservacion, reservacion_id)
    if reservacion is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Reservación no encontrada")

    update_data = data.model_dump(exclude_unset=True)
    if "event_id" in update_data:
        event_id = update_data.pop("event_id")
        if event_id is not None:
            evento_obj = db.get(EventoModel, event_id)
            if evento_obj is None:
                raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Evento no encontrado")
            update_data["evento_id"] = event_id
            update_data["evento"] = evento_obj.titulo
            update_data["tipo_evento"] = evento_obj.tipo_evento
            update_data["fecha"] = evento_obj.fecha
            update_data["lugar"] = evento_obj.lugar

    for field, value in update_data.items():
        setattr(reservacion, field, value)

    db.commit()
    db.refresh(reservacion)
    return reservacion


@router.delete("/reservaciones/{reservacion_id}", status_code=status.HTTP_204_NO_CONTENT)
def eliminar_reservacion(
    reservacion_id: int,
    db: Session = Depends(get_db),
    current_user: Usuario = Depends(require_admin),
):
    reservacion = db.get(Reservacion, reservacion_id)
    if reservacion is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Reservación no encontrada")

    db.delete(reservacion)
    db.commit()
    return None


@router.get("/admin/dashboard", response_model=DashboardResponse)
def dashboard(
    db: Session = Depends(get_db),
    current_user: Usuario = Depends(require_admin),
):
    total_reservaciones = db.query(Reservacion).count()
    total_usuarios = db.query(Usuario).count()

    reservaciones_por_mes = [
        {"mes": row.mes, "cantidad": row.cantidad}
        for row in (
            db.query(
                func.to_char(Reservacion.fecha, "YYYY-MM").label("mes"),
                func.count(Reservacion.id).label("cantidad"),
            )
            .group_by("mes")
            .order_by("mes")
            .all()
        )
    ]

    eventos_por_tipo = [
        {"tipo_evento": row.tipo_evento or "Evento general", "cantidad": row.cantidad}
        for row in (
            db.query(
                Reservacion.tipo_evento,
                func.count(Reservacion.id).label("cantidad"),
            )
            .group_by(Reservacion.tipo_evento)
            .order_by(func.count(Reservacion.id).desc())
            .all()
        )
    ]

    eventos_mas_populares = [
        {
            "evento": row.evento,
            "tipo_evento": row.tipo_evento or "Evento general",
            "cantidad": row.cantidad,
        }
        for row in (
            db.query(
                Reservacion.evento,
                Reservacion.tipo_evento,
                func.count(Reservacion.id).label("cantidad"),
            )
            .group_by(Reservacion.evento, Reservacion.tipo_evento)
            .order_by(func.count(Reservacion.id).desc())
            .limit(5)
            .all()
        )
    ]

    lugares_mas_reservados = [
        {"lugar": row.lugar, "cantidad": row.cantidad}
        for row in (
            db.query(Reservacion.lugar, func.count(Reservacion.id).label("cantidad"))
            .group_by(Reservacion.lugar)
            .order_by(func.count(Reservacion.id).desc())
            .limit(5)
            .all()
        )
    ]

    ocupacion_eventos = [
        {
            "evento": item["evento"],
            "tipo_evento": item["tipo_evento"],
            "reservaciones": item["cantidad"],
            "cantidad": item["cantidad"],
            "porcentaje": round((item["cantidad"] / total_reservaciones) * 100, 2)
            if total_reservaciones
            else 0,
        }
        for item in eventos_mas_populares
    ]

    return {
        "total_reservaciones": total_reservaciones,
        "total_usuarios": total_usuarios,
        "eventos_por_tipo": eventos_por_tipo,
        "reservaciones_por_mes": reservaciones_por_mes,
        "eventos_mas_populares": eventos_mas_populares,
        "lugares_mas_reservados": lugares_mas_reservados,
        "ocupacion_eventos": ocupacion_eventos,
    }
