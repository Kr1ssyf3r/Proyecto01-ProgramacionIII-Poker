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

## Implementacion de GUI y Validaciones - Kenan
### Se implementaron las siguientes Validaciones:
SaldoInsuficienteException (util) — excepción que se lanza cuando un jugador intenta apostar más fichas de las que tiene.

ApuestaInvalidaException (util) — excepción para montos de apuesta inválidos (negativos, cero, o que no superan la apuesta actual al subir).

Validaciones (util) — clase de métodos estáticos que revisa montos y saldos antes de dejar pasar una apuesta, lanzando las dos excepciones anteriores cuando algo no cuadra.

### Se implementaron las siguientes clases para la GUI:
PanelJugador (view) — el recuadro que muestra a un jugador en la mesa: nombre, fichas y sus 2 cartas (con imágenes reales; puede ocultarlas o revelarlas).

PanelMesa (view) — el centro de la mesa: las cartas comunitarias reveladas hasta el momento y el monto del pozo.

PanelControles (view) — los 4 botones de acción del jugador humano (Check, Call, Raise, Fold), con su propia validación antes de avisarle a la ventana principal.

VentanaPrincipal (view) — la ventana completa: arma todos los paneles, conecta los botones con JuegoPoker, maneja el turno, la narración de jugadas de los bots, y el diálogo de fin de ronda.

RegistroAccion (model) — un record simple que guarda "quién hizo qué acción y con cuánto monto", para que la ventana pueda narrar las jugadas de los bots después de que ocurren.

CartaImagenes (view) — convierte una carta (ej. As de picas) en el nombre de su archivo de imagen y la carga desde los recursos del proyecto, cacheándola para no releerla cada vez.
