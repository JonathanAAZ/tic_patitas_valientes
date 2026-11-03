#!/bin/sh
# rastrear.sh <término> — localiza el comportamiento real asociado a un término del dominio.
#
# Sirve para no tener que adivinar dónde vive una funcionalidad. Dado un término
# ("retirar", "vacuna", "contrato", "seguimiento"...) devuelve las pantallas, los métodos de
# controlador, las colecciones que esos archivos tocan y los estados relacionados.
#
# Es un punto de partida, no una respuesta: lo que encuentre hay que leerlo.
set -u

termino="${1:-}"
if [ -z "$termino" ]; then
    echo "uso: rastrear.sh <término del dominio>" >&2
    echo "ej:  rastrear.sh retirar" >&2
    exit 1
fi

raiz=$(git rev-parse --show-toplevel 2>/dev/null) || { echo "no es un repo git" >&2; exit 1; }
src="$raiz/app/src/main/java/com/example/tic_pv"
cd "$src" || { echo "no se encontró $src" >&2; exit 1; }

echo "=== Métodos de controlador que lo mencionan ==="
grep -rin "public .*${termino}" --include=*.java Controlador/ \
    | sed 's|^|  |' | head -20
echo ""

echo "=== Pantallas con el término en el nombre ==="
ls Vista/*.java Vista/Fragments/*.java 2>/dev/null \
    | grep -i "$termino" | sed 's|^|  |' | head -15
echo ""

echo "=== Archivos implicados y lo que escriben ==="
for f in $(grep -ril "$termino" --include=*.java . 2>/dev/null | head -14); do
    datos=$(grep -o 'collection("[A-Za-z]*")\|getReference("[a-zA-Z_]*")' "$f" 2>/dev/null \
        | sed 's|collection("||;s|getReference("||;s|")||' | sort -u | tr '\n' ' ')
    printf "  %-52s %s\n" "$(echo "$f" | sed 's|^\./||')" "$datos"
done
echo ""

echo "=== Estados relacionados en EstadosCuentas ==="
grep -io "[A-Z_]*${termino}[A-Z_]*" Modelo/EstadosCuentas.java 2>/dev/null \
    | sort -u | sed 's|^|  |'
echo ""

echo "=== Layouts ==="
ls "$raiz"/app/src/main/res/layout/*.xml 2>/dev/null \
    | sed 's|.*/||' | grep -i "$termino" | sed 's|^|  |' | head -10
