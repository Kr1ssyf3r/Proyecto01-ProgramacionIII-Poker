package com.una.pokerdefichas.modelo;

import java.util.*;

/**
 * Implementación de una partida de Texas Hold'em.
 * Gestiona el flujo del juego: reparto, rondas de apuestas, bote y showdown.
 * Esta clase es el corazón de la lógica del póker.
 */
public class JuegoPoker implements Jugable {
    private final List<Jugador> jugadores;
    private Mazo<Carta> mazo;
    private List<Carta> cartasComunitarias;
    private int bote;
    private int apuestaActual;
    private int turnoActual;
    private Fase fase;
    private boolean rondaTerminada;
    private Map<Jugador, Integer> apuestasJugador; // cuánto ha apostado cada uno en la ronda actual
    private Set<Jugador> jugadoresRetirados;
    private Map<Jugador, List<Carta>> cartasPrivadas; // almacena las 2 cartas de cada jugador
    private Set<Jugador> yaActuaron; // Registro de quién ya actuó en esta ronda de apuestas

    // Enumeración interna para las fases del juego
    private enum Fase {
        PREFLOP, FLOP, TURN, RIVER, SHOWDOWN
    }

    /**
     * Constructor. Inicializa la partida con la lista de jugadores.
     * @param jugadores lista de jugadores (mínimo 2)
     * @throws IllegalArgumentException si hay menos de 2 jugadores
     */
    public JuegoPoker(List<Jugador> jugadores) {
        if (jugadores.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 jugadores");
        }
        this.jugadores = jugadores;
        this.mazo = Mazo.crearMazoPoker();
        this.cartasComunitarias = new ArrayList<>();
        this.bote = 0;
        this.apuestaActual = 0;
        this.turnoActual = 0;
        this.fase = Fase.PREFLOP;
        this.rondaTerminada = false;
        this.apuestasJugador = new HashMap<>();
        this.jugadoresRetirados = new HashSet<>();
        this.cartasPrivadas = new HashMap<>();
        this.yaActuaron = new HashSet<>();
    }

    // --- Métodos públicos de la interfaz Jugable ---

    @Override
    public void iniciarRonda() {
        // Reiniciar estado de todos los jugadores
        for (Jugador j : jugadores) {
            j.reiniciarParaNuevaRonda();
        }
        mazo.reiniciar();
        cartasComunitarias.clear();
        bote = 0;
        apuestaActual = 0;
        rondaTerminada = false;
        apuestasJugador.clear();
        jugadoresRetirados.clear();
        cartasPrivadas.clear();
        yaActuaron.clear();
        fase = Fase.PREFLOP;

        // Repartir 2 cartas privadas a cada jugador
        for (Jugador j : jugadores) {
            List<Carta> privadas = mazo.repartir(2);
            cartasPrivadas.put(j, privadas);
        }

        // El turno empieza por el primer jugador (índice 0)
        turnoActual = 0;
        siguienteTurno(); // Avanzar hasta que sea turno de un humano o termine la ronda
    }

    @Override
    public void procesarAccion(Jugador jugador, AccionPoker accion, int cantidadApuesta) {
        // Validar que sea el turno del jugador
        if (!jugador.equals(getJugadorActual())) {
            throw new IllegalStateException("No es el turno de " + jugador.getNombre());
        }
        if (jugadoresRetirados.contains(jugador)) {
            throw new IllegalStateException(jugador.getNombre() + " ya se retiró en esta ronda");
        }

        // Procesar según la acción
        switch (accion) {
            case CHECK:
                if (apuestaActual > 0 && apuestasJugador.getOrDefault(jugador, 0) < apuestaActual) {
                    throw new IllegalStateException("No puede hacer CHECK, debe igualar o retirarse");
                }
                break;

            case CALL:
                int aPagar = apuestaActual - apuestasJugador.getOrDefault(jugador, 0);
                if (aPagar > jugador.getSaldoFichas()) {
                    throw new IllegalStateException("Saldo insuficiente para CALL");
                }
                jugador.apostar(aPagar);
                bote += aPagar;
                apuestasJugador.put(jugador, apuestaActual);
                break;

            case FOLD:
                jugador.fold();
                jugadoresRetirados.add(jugador);
                break;

            case RAISE:
                int subida = cantidadApuesta;
                if (subida <= apuestaActual) {
                    throw new IllegalArgumentException("La subida debe ser mayor que la apuesta actual");
                }
                int totalApuesta = subida - apuestasJugador.getOrDefault(jugador, 0);
                if (totalApuesta > jugador.getSaldoFichas()) {
                    throw new IllegalStateException("Saldo insuficiente para RAISE");
                }
                jugador.apostar(totalApuesta);
                bote += totalApuesta;
                apuestaActual = subida;
                apuestasJugador.put(jugador, subida);
                // REABRE LA ACCIÓN: todos los demás deben volver a actuar
                yaActuaron.clear();
                yaActuaron.add(jugador);
                break;

            default:
                throw new IllegalArgumentException("Acción no soportada: " + accion);
        }

        // Si el jugador se retiró o se quedó sin fichas, lo marcamos
        if (!jugador.tieneFichas()) {
            jugadoresRetirados.add(jugador);
        }

        // Si no fue RAISE, añadir el jugador a los que ya actuaron
        if (accion != AccionPoker.RAISE) {
            yaActuaron.add(jugador);
        }

        // Avanzar al siguiente turno después de procesar la acción
        siguienteTurno();
    }

    @Override
    public void siguienteTurno() {
        // Si la ronda ya terminó, no hacer nada
        if (rondaTerminada) return;

        // Verificar si la ronda de apuestas debe terminar
        if (rondaApuestasTerminada()) {
            rondaTerminada = true;
            avanzarFase();
            return;
        }

        // Primero, verificar si solo queda un jugador activo (no retirado)
        long activos = jugadores.stream()
                .filter(Jugador::isActivo)
                .filter(j -> !jugadoresRetirados.contains(j))
                .count();

        if (activos <= 1) {
            // Terminar la ronda y repartir el bote al único activo
            rondaTerminada = true;
            Jugador ganador = jugadores.stream()
                    .filter(Jugador::isActivo)
                    .filter(j -> !jugadoresRetirados.contains(j))
                    .findFirst().orElse(null);
            if (ganador != null) {
                ganador.agregarFichas(bote);
                bote = 0;
            }
            return;
        }

        // Encontrar el siguiente jugador activo que no se haya retirado
        // y que aún no haya actuado en esta ronda
        Jugador siguiente = null;
        int contador = 0;
        do {
            turnoActual = (turnoActual + 1) % jugadores.size();
            contador++;
            if (contador > jugadores.size()) {
                // Si todos los activos ya actuaron, la ronda debería terminar
                // pero ya lo verificamos con rondaApuestasTerminada()
                rondaTerminada = true;
                avanzarFase();
                return;
            }
            Jugador candidato = jugadores.get(turnoActual);
            if (candidato.isActivo() && !jugadoresRetirados.contains(candidato)) {
                // Si aún no ha actuado en esta ronda, es el siguiente
                if (!yaActuaron.contains(candidato)) {
                    siguiente = candidato;
                    break;
                }
            }
        } while (siguiente == null);

        if (siguiente == null) {
            // No debería ocurrir, pero por seguridad
            rondaTerminada = true;
            avanzarFase();
            return;
        }

        // Si el jugador actual es un bot, tomar decisión automática
        if (siguiente instanceof JugadorBot) {
            AccionPoker accion = siguiente.decidirAccion(apuestaActual, bote);
            int cantidad = 0;
            if (accion == AccionPoker.RAISE) {
                // Subida simple: doblar la apuesta actual (o mínimo)
                cantidad = apuestaActual * 2;
                if (cantidad > siguiente.getSaldoFichas()) cantidad = siguiente.getSaldoFichas();
                if (cantidad <= apuestaActual) cantidad = apuestaActual + 10; // fallback
            }
            // Procesar la acción del bot (esto llamará a siguienteTurno nuevamente)
            procesarAccion(siguiente, accion, cantidad);
            // Nota: procesarAccion llama a siguienteTurno(), por lo que los bots seguirán actuando
            // hasta que toque a un humano o termine la ronda.
        }
        // Si es humano, el método se detiene y el controlador debe llamar a procesarAccion cuando el usuario decida.
        // No hacemos nada más.
    }

    /**
     * Verifica si la ronda de apuestas actual ha terminado.
     * Condiciones:
     * 1. Todos los jugadores activos han actuado (están en yaActuaron).
     * 2. Todos los activos tienen su apuesta igual a apuestaActual (o apuestaActual == 0).
     */
    private boolean rondaApuestasTerminada() {
        List<Jugador> activos = jugadores.stream()
                .filter(Jugador::isActivo)
                .filter(j -> !jugadoresRetirados.contains(j))
                .toList();

        if (activos.isEmpty()) return true;

        // Verificar que todos los activos hayan actuado
        boolean todosActuaron = yaActuaron.containsAll(activos);
        if (!todosActuaron) return false;

        // Verificar que todos los activos tengan la apuesta igual a la actual
        if (apuestaActual == 0) {
            // Si no hay apuesta, con que todos hayan actuado (hicieron CHECK) basta
            return true;
        } else {
            // Todos deben haber igualado la apuestaActual
            for (Jugador j : activos) {
                int apostado = apuestasJugador.getOrDefault(j, 0);
                if (apostado != apuestaActual) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public Jugador getJugadorActual() {
        if (rondaTerminada) return null;
        if (turnoActual < 0 || turnoActual >= jugadores.size()) return null;
        Jugador j = jugadores.get(turnoActual);
        if (!j.isActivo() || jugadoresRetirados.contains(j)) return null;
        // Si el jugador ya actuó, no es su turno (por seguridad)
        if (yaActuaron.contains(j)) return null;
        return j;
    }

    @Override
    public List<Carta> getCartasComunitarias() {
        return new ArrayList<>(cartasComunitarias);
    }

    @Override
    public int getBote() {
        return bote;
    }

    @Override
    public int getApuestaActual() {
        return apuestaActual;
    }

    @Override
    public boolean isRondaTerminada() {
        return rondaTerminada;
    }

    @Override
    public ResultadoRonda resolverRonda() {
        // Si la ronda no ha terminado, forzar término
        if (!rondaTerminada) {
            rondaTerminada = true;
        }

        // Obtener jugadores activos
        List<Jugador> activos = jugadores.stream()
                .filter(Jugador::isActivo)
                .filter(j -> !jugadoresRetirados.contains(j))
                .toList();

        if (activos.isEmpty()) {
            // Todos se retiraron (caso raro)
            return null;
        }

        if (activos.size() == 1) {
            Jugador ganador = activos.get(0);
            ganador.agregarFichas(bote);
            ResultadoRonda res = new ResultadoRonda(ganador, bote, CombinacionPoker.CARTA_ALTA);
            bote = 0;
            return res;
        }

        // Showdown: evaluar manos de los activos
        Jugador ganador = null;
        ManoPoker mejorMano = null;
        for (Jugador j : activos) {
            ManoPoker mano = getMejorManoJugador(j);
            if (ganador == null || mano.compararCon(mejorMano) > 0) {
                ganador = j;
                mejorMano = mano;
            }
        }

        if (ganador != null) {
            ganador.agregarFichas(bote);
            ResultadoRonda res = new ResultadoRonda(ganador, bote, mejorMano.evaluar());
            bote = 0;
            return res;
        }
        return null;
    }

    @Override
    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    @Override
    public String getFaseActual() {
        return fase.name();
    }

    // --- Métodos auxiliares privados ---

    /**
     * Avanza a la siguiente fase del juego (flop, turn, river, showdown).
     * Se llama automáticamente cuando termina una ronda de apuestas.
     */
    private void avanzarFase() {
        if (fase == Fase.PREFLOP) {
            fase = Fase.FLOP;
            repartirComunitarias(3);
            reiniciarApuestas();
        } else if (fase == Fase.FLOP) {
            fase = Fase.TURN;
            repartirComunitarias(1);
            reiniciarApuestas();
        } else if (fase == Fase.TURN) {
            fase = Fase.RIVER;
            repartirComunitarias(1);
            reiniciarApuestas();
        } else if (fase == Fase.RIVER) {
            fase = Fase.SHOWDOWN;
            rondaTerminada = true;
            // No se reparten más cartas
        }
        // Si no es SHOWDOWN, comenzar nueva ronda de apuestas
        if (fase != Fase.SHOWDOWN) {
            rondaTerminada = false;
            yaActuaron.clear();
            // Buscar el primer jugador activo para comenzar la siguiente ronda
            for (int i = 0; i < jugadores.size(); i++) {
                if (jugadores.get(i).isActivo() && !jugadoresRetirados.contains(jugadores.get(i))) {
                    turnoActual = i;
                    break;
                }
            }
            siguienteTurno(); // Iniciar la nueva ronda
        } else {
            // Si es showdown, ya no hay más acciones
            // El controlador debe llamar a resolverRonda() para obtener el ganador
        }
    }

    /**
     * Reparte cartas comunitarias (flop, turn, river).
     * @param cantidad número de cartas a repartir (3 para flop, 1 para turn/river)
     */
    private void repartirComunitarias(int cantidad) {
        List<Carta> nuevas = mazo.repartir(cantidad);
        cartasComunitarias.addAll(nuevas);
    }

    /**
     * Reinicia las apuestas de la ronda para la siguiente fase.
     * Mantiene el bote acumulado, pero resetea la apuesta actual y las apuestas de cada jugador.
     */
    private void reiniciarApuestas() {
        apuestaActual = 0;
        apuestasJugador.clear();
        yaActuaron.clear();
        // Los jugadores que no se retiraron siguen activos
        // Los retirados permanecen en el set
    }

    /**
     * Obtiene la mejor mano de 5 cartas para un jugador combinando sus privadas y las comunitarias.
     * @param jugador el jugador
     * @return ManoPoker con la mejor combinación
     */
    private ManoPoker getMejorManoJugador(Jugador jugador) {
        List<Carta> todas = new ArrayList<>(cartasPrivadas.get(jugador));
        todas.addAll(cartasComunitarias);
        return new ManoPoker(todas, true);
    }
}
