#!/bin/bash

# Script para limpiar logs antiguos
# Uso: ./clean-logs.sh [días] [directorio]

# Configuración por defecto
DAYS_TO_KEEP=${1:-30}
LOG_DIR=${2:-"./logs"}

echo "Limpiando logs más antiguos de $DAYS_TO_KEEP días en $LOG_DIR"

# Crear directorio si no existe
mkdir -p "$LOG_DIR"

# Limpiar archivos de log principales
find "$LOG_DIR" -name "*.log" -type f -mtime +$DAYS_TO_KEEP -delete

# Limpiar archivos de log archivados
find "$LOG_DIR/archived" -name "*.log" -type f -mtime +$DAYS_TO_KEEP -delete

# Mostrar estadísticas
echo "Archivos de log restantes:"
find "$LOG_DIR" -name "*.log" -type f | wc -l

echo "Tamaño total de logs:"
du -sh "$LOG_DIR"

echo "Limpieza completada!" 