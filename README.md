# Proyecto01-Progra-III---lil-devs
## Primeros commits de Leria ^^

### Se implementan las primeras clases: 
Jugador con los primeros atributos para el nombre, la cantidad de saldo, la mano con la que está jugando y si se encuentra activo dentro de la ronda. 

Carta recibe Palo y declara el valor de una carta, entre 2 y 14, y un toString() para imprimir la info

Palo (tipo enum) que forma los palos del poker, los declara como simbolos

AccionPoker (tipo enum) representa las posibles acciones que tiene el jugador

Mazo es una clase genérica de elementos tipo T, permite mezclar, repartir y reiniciar el mazo. 

ResultadoRonda representa el resultado final de la ronda de poker, demostrando cuantas fichas gano, el ganador y la combinación con la que ganó

## Implementación de la lógica del póker - Kris

### Se implementan las clases encargadas de la lógica y reglas del juego:

-Jugable (tipo interface) establece las operaciones que debe implementar el juego de póker.

-JuegoPoker administra el flujo de la partida, incluyendo el manejo de jugadores, reparto de cartas, cartas comunitarias, pozo, apuestas, turnos y resultados de cada ronda.

-ManoPoker representa las cartas de un jugador y permite evaluar la mejor combinación de póker obtenida durante la partida.

-CombinacionPoker (tipo enum) define las diferentes combinaciones posibles de una mano de póker y permite clasificarlas según su valor.
