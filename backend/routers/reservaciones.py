from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import func
from sqlalchemy.orm import Session, joinedload

from config.db import get_db
from models.models import Reservacion, Usuario
from routers.auth import get_current_user, require_admin
from schemas.schemas import (
    DashboardResponse,
    ReservacionCreate,
    ReservacionListResponse,
    Reservacion as ReservacionSchema,
    ReservacionUpdate,
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
    nueva = Reservacion(**reservacion.model_dump(), usuario_id=current_user.id)
    db.add(nueva)
    db.commit()
    db.refresh(nueva)
    return nueva


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

    for field, value in data.model_dump(exclude_unset=True).items():
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
