from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import Optional, List
import databases
import sqlalchemy
from sqlalchemy import create_engine
from geoalchemy2 import Geometry
from sqlalchemy.sql import func
import uuid
from datetime import datetime
import os
from dotenv import load_dotenv
from mangum import Mangum
import json

load_dotenv()

db_url_env = os.getenv("DATABASE_URL")

if db_url_env is None:
    raise RuntimeError("DATABASE_URL environment variable not set")

try:
    db_config = json.loads(db_url_env)
    DATABASE_URL = db_config.get("DATABASE_URL")
    if DATABASE_URL is None:
         raise RuntimeError("'DATABASE_URL' key not found in the secret JSON")
except json.JSONDecodeError:
    DATABASE_URL = db_url_env

database = databases.Database(DATABASE_URL)
metadata = sqlalchemy.MetaData()

# Define the 'reports' table
reports = sqlalchemy.Table(
    "reports",
    metadata,
    sqlalchemy.Column("id", sqlalchemy.dialects.postgresql.UUID(as_uuid=True), primary_key=True, default=uuid.uuid4),
    sqlalchemy.Column("user_id", sqlalchemy.dialects.postgresql.UUID(as_uuid=True), nullable=True),
    sqlalchemy.Column("occurred_at", sqlalchemy.DateTime(timezone=True), server_default=func.now()),
    sqlalchemy.Column("volume", sqlalchemy.Integer),
    sqlalchemy.Column("notes", sqlalchemy.Text, nullable=True),
    sqlalchemy.Column("location", Geometry(geometry_type='POINT', srid=4326), nullable=False),
    sqlalchemy.Column("accuracy_m", sqlalchemy.Float, nullable=True),
    sqlalchemy.Column("source", sqlalchemy.Text, default='app'),
)

engine = create_engine(DATABASE_URL)
metadata.create_all(engine)

class FireworkEventIn(BaseModel):
    user_id: Optional[uuid.UUID] = None
    volume: int = Field(..., ge=0, le=100)
    notes: Optional[str] = None
    latitude: float
    longitude: float
    accuracy_m: Optional[float] = None
    source: Optional[str] = 'app'

class FireworkEventOut(BaseModel):
    id: uuid.UUID
    user_id: Optional[uuid.UUID] = None
    occurred_at: datetime
    volume: int
    notes: Optional[str] = None
    latitude: float
    longitude: float
    accuracy_m: Optional[float] = None
    source: str

app = FastAPI(title="Fireworks Tracker API")

@app.on_event("startup")
async def startup():
    await database.connect()

@app.on_event("shutdown")
async def shutdown():
    await database.disconnect()

# --- API Endpoints ---
@app.post("/reports/", response_model=FireworkEventOut, status_code=201)
async def create_report(event: FireworkEventIn):
    point = f'SRID=4326;POINT({event.longitude} {event.latitude})'
    query = reports.insert().values(
        volume=event.volume,
        notes=event.notes,
        location=point,
        accuracy_m=event.accuracy_m,
        source=event.source
    )
    last_record_id = await database.execute(query)
    
    query = reports.select().where(reports.c.id == last_record_id)
    created_report = await database.fetch_one(query)
    
    return {
        "id": created_report["id"], "user_id": created_report["user_id"],
        "occurred_at": created_report["occurred_at"], "volume": created_report["volume"],
        "notes": created_report["notes"], "latitude": event.latitude,
        "longitude": event.longitude, "accuracy_m": created_report["accuracy_m"],
        "source": created_report["source"]
    }

@app.get("/reports/", response_model=List[FireworkEventOut])
async def get_reports():
    query = """
        SELECT
            id,
            user_id,
            occurred_at,
            volume,
            notes,
            ST_X(location::geometry) as longitude,
            ST_Y(location::geometry) as latitude,
            accuracy_m,
            source
        FROM reports
    """
    results = await database.fetch_all(query)
    return results

@app.delete("/reports/{report_id}", status_code=204)
async def delete_report(report_id: uuid.UUID):
    query = reports.delete().where(reports.c.id == report_id)
    result = await database.execute(query)
    if not result:
        raise HTTPException(status_code=404, detail="Report not found")
    return {}

handler = Mangum(app)
