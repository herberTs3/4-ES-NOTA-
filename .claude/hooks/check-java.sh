#!/usr/bin/env bash
input=$(cat)

file=$(printf '%s' "$input" | sed -n 's/.*"file_path"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' | head -n 1)
file=$(printf '%s' "$file" | sed 's#\\\\#/#g')

case "$file" in
    *.java) ;;
    *) exit 0 ;;
esac

[ -f "$file" ] || exit 0

warnings=""

if grep -nE '^[[:space:]]*//|/\*' "$file" > /dev/null 2>&1; then
    warnings="${warnings}- Comentarios detectados: el código no lleva comentarios (code-style.md).\n"
fi

code_only=$(sed -e 's/"[^"]*"//g' "$file")
spanish='\b(caja|jugador|nivel|tablero|movimiento|pared|muro|destino|empuje|puntaje|deshacer|cerrojo|llave|fragil|hielo|sonido)|(Caja|Jugador|Nivel|Tablero|Movimiento|Pared|Muro|Destino|Empuje|Puntaje|Deshacer|Cerrojo|Llave|Fragil|Hielo|Sonido)'
if printf '%s' "$code_only" | grep -qE "$spanish"; then
    warnings="${warnings}- Identificadores en español: el código va en inglés (el español solo en textos visibles).\n"
fi

case "$file" in
    */model/*)
        if grep -nE '^import[[:space:]]+(javax\.swing|java\.awt)' "$file" > /dev/null 2>&1; then
            warnings="${warnings}- MVC: el modelo importa javax.swing/java.awt. El modelo no puede depender de la vista.\n"
        fi
        ;;
esac

if grep -nE 'System\.(out|err)\.print' "$file" > /dev/null 2>&1; then
    warnings="${warnings}- System.out/err de depuración: quitar antes de la entrega.\n"
fi

if [ -n "$warnings" ]; then
    printf 'Revisar %s:\n%b' "$file" "$warnings" >&2
    exit 2
fi

exit 0
