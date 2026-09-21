"""
Contrasta ManoPoker con un evaluador de póker escrito aparte.

Uso (desde la carpeta PokerDeFichas):
    java -cp out GeneradorManos 100000 | python3 verificacion/comparar_evaluador.py

Por cada par de manos de 7 cartas comprueba que la combinación de cada mano y el ganador
(incluyendo desempates y la escalera A-2-3-4-5) coincidan con los del evaluador independiente.
"""
import collections
import itertools
import sys

NOMBRES = ["CARTA_ALTA", "PAR", "DOS_PARES", "TRIO", "ESCALERA", "COLOR", "FULL",
           "POKER", "ESCALERA_COLOR", "ESCALERA_REAL"]


def rango_de_5(cartas):
    """Devuelve (nivel, desempate) de una mano de 5 cartas; una tupla mayor es una mano mejor."""
    valores = sorted((v for v, _ in cartas), reverse=True)
    es_color = len({p for _, p in cartas}) == 1
    distintos = sorted(set(valores), reverse=True)
    alta_escalera = None
    if len(distintos) == 5:
        if distintos[0] - distintos[4] == 4:
            alta_escalera = distintos[0]
        elif distintos == [14, 5, 4, 3, 2]:
            alta_escalera = 5  # el As cuenta como 1
    grupos = sorted(collections.Counter(valores).items(), key=lambda kv: (-kv[1], -kv[0]))
    forma = [c for _, c in grupos]
    orden = [v for v, _ in grupos]
    if alta_escalera and es_color:
        return (9 if alta_escalera == 14 else 8, [alta_escalera])
    if forma == [4, 1]:
        return (7, orden)
    if forma == [3, 2]:
        return (6, orden)
    if es_color:
        return (5, valores)
    if alta_escalera:
        return (4, [alta_escalera])
    if forma == [3, 1, 1]:
        return (3, orden)
    if forma == [2, 2, 1]:
        return (2, orden)
    if forma == [2, 1, 1, 1]:
        return (1, orden)
    return (0, valores)


def mejor_de_7(cartas):
    """Devuelve el mejor rango entre las 21 combinaciones de 5 cartas."""
    return max(rango_de_5(c) for c in itertools.combinations(cartas, 5))


def leer_cartas(texto):
    """Convierte '14P 7T ...' en [(14, 'P'), (7, 'T'), ...]."""
    return [(int(t[:-1]), t[-1]) for t in texto.split()]


def main():
    total = mal_tipo = mal_ganador = 0
    ejemplos = []
    for linea in sys.stdin:
        a, b, resultado = [p.strip() for p in linea.split("|")]
        tipo_a, tipo_b, signo = resultado.split()
        ra, rb = mejor_de_7(leer_cartas(a)), mejor_de_7(leer_cartas(b))
        total += 1
        if NOMBRES[ra[0]] != tipo_a or NOMBRES[rb[0]] != tipo_b:
            mal_tipo += 1
            ejemplos.append(linea.strip())
        if (ra > rb) - (ra < rb) != int(signo):
            mal_ganador += 1
            ejemplos.append(linea.strip())
    print(f"Pares de manos comparados: {total}")
    print(f"Combinaciones mal clasificadas: {mal_tipo}")
    print(f"Ganadores distintos al evaluador independiente: {mal_ganador}")
    for e in ejemplos[:5]:
        print("  ejemplo:", e)
    print("RESULTADO: OK" if mal_tipo == 0 and mal_ganador == 0 else "RESULTADO: FALLO")
    sys.exit(0 if mal_tipo == 0 and mal_ganador == 0 else 1)


main()
