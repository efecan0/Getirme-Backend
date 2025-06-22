#!/usr/bin/env bash
set -euo pipefail

DATA_DIR="data"
mkdir -p "$DATA_DIR"

# 1) Türkiye PBF’ini indir (≈ 1.4 GB)
cd "$DATA_DIR"
wget -N https://download.geofabrik.de/europe/turkey-latest.osm.pbf

# 2) İzmir BBox kes (26.9,37.0,28.5,38.9)  → izmir.pbf
osmium extract -b 26.9,37.0,28.5,38.9 \
  -o izmir.pbf --overwrite turkey-latest.osm.pbf        # :contentReference[oaicite:2]{index=2}

# 3) Yalnızca “highway=*” yolları bırak  → roads.pbf
osmium tags-filter izmir.pbf w/highway \
  -o roads.pbf --overwrite                               # :contentReference[oaicite:3]{index=3}
