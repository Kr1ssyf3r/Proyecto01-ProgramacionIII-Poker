import juego.BotAgresivo;
import juego.BotConservador;
import juego.JuegoPoker;
import model.AccionPoker;
import model.JugadorBot;
import model.JugadorHumano;
import model.ResultadoRonda;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Simula miles de rondas completas contra el motor (sin interfaz gráfica), imitando el flujo de
 * VentanaPrincipal con un jugador humano que elige acciones al azar. Comprueba que no haya
 * excepciones, que las fichas se conserven y que el bote siempre se reparta.
 */
public class SimulacionPartidas {

    private static final Map<String, Integer> fallos = new TreeMap<>();
    private static int subidasRechazadasPorTope = 0;

    /**
     * Ejecuta la simulación e imprime el resultado.
     * @param args semilla (por defecto 1), cantidad de partidas (por defecto 300) y, opcionalmente,
     *             la palabra "allin" para que el humano apueste todas sus fichas con frecuencia.
     */
    public static void main(String[] args) {
        long semilla = args.length > 0 ? Long.parseLong(args[0]) : 1;
        int partidas = args.length > 1 ? Integer.parseInt(args[1]) : 300;
        boolean allIn = args.length > 2 && args[2].equals("allin");
        Random azar = new Random(semilla);
        int rondas = 0;

        for (int p = 0; p < partidas; p++) {
            JugadorHumano humano = new JugadorHumano("Humano", 500);
            JugadorBot conservador = new JugadorBot("Bot Conservador", 500, new BotConservador());
            JugadorBot agresivo = new JugadorBot("Bot Agresivo", 500, new BotAgresivo());
            JuegoPoker juego = new JuegoPoker(List.of(humano, conservador, agresivo));

            for (int r = 0; r < 80; r++) {
                int total = humano.getSaldoFichas() + conservador.getSaldoFichas() + agresivo.getSaldoFichas();
                rondas++;
                try {
                    juego.iniciarRonda();
                    int pasos = 0;
                    while (!juego.isRondaTerminada()) {
                        if (++pasos > 200) { fallo("la ronda no termina (bucle)"); break; }
                        if (juego.getJugadorActual() != humano) {
                            fallo("la ronda sigue abierta pero no es el turno del humano");
                            break;
                        }
                        jugarComoHumano(juego, humano, azar, allIn);
                    }
                    if ("SHOWDOWN".equals(juego.getFaseActual())) {
                        ResultadoRonda resultado = juego.resolverRonda();
                        if (resultado == null) fallo("resolverRonda devolvió null (el bote se pierde)");
                    }
                    int despues = humano.getSaldoFichas() + conservador.getSaldoFichas() + agresivo.getSaldoFichas();
                    if (despues != total) fallo("las fichas no se conservan");
                    if (juego.getBote() != 0) fallo("el bote no queda en 0 al terminar la ronda");
                } catch (RuntimeException e) {
                    fallo("excepción no controlada: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    break;
                }
            }
        }

        System.out.println("Rondas simuladas: " + rondas);
        System.out.println("Subidas del humano rechazadas por el tope (comportamiento esperado): " + subidasRechazadasPorTope);
        if (fallos.isEmpty()) {
            System.out.println("RESULTADO: OK, sin fallos");
        } else {
            fallos.forEach((mensaje, veces) -> System.out.println("FALLO (" + veces + " veces): " + mensaje));
            System.exit(1);
        }
    }

    /**
     * Elige y ejecuta una acción del humano respetando las mismas validaciones que PanelControles.
     * @param juego partida en curso.
     * @param humano jugador humano.
     * @param azar generador de números aleatorios.
     * @param allIn si es true, el humano apuesta todas sus fichas la mitad de las veces.
     */
    private static void jugarComoHumano(JuegoPoker juego, JugadorHumano humano, Random azar, boolean allIn) {
        int apuesta = juego.getApuestaActual();
        int saldo = humano.getSaldoFichas();
        double x = azar.nextDouble();
        AccionPoker accion;
        int monto = 0;

        if (allIn && saldo > 0 && x < 0.5) {
            accion = AccionPoker.RAISE;
            monto = saldo;
        } else if (x < 0.55) {
            accion = apuesta == 0 ? AccionPoker.CHECK : AccionPoker.CALL;
        } else if (x < 0.80) {
            accion = AccionPoker.RAISE;
            monto = apuesta + 1 + azar.nextInt(Math.max(1, saldo));
        } else {
            accion = AccionPoker.FOLD;
        }
        // validaciones de la interfaz
        if (accion == AccionPoker.RAISE && (monto <= 0 || monto > saldo || monto <= apuesta)) {
            accion = apuesta == 0 ? AccionPoker.CHECK : AccionPoker.FOLD;
            monto = 0;
        }
        if (accion == AccionPoker.CALL && apuesta > saldo) accion = AccionPoker.FOLD;

        try {
            juego.procesarAccion(humano, accion, monto);
        } catch (IllegalArgumentException e) {
            // la subida superó lo que puede igualar el jugador con menos fichas: la interfaz muestra el error
            subidasRechazadasPorTope++;
            juego.procesarAccion(humano, AccionPoker.FOLD, 0);
        }
    }

    /**
     * Registra un fallo para mostrarlo al final.
     * @param mensaje descripción del fallo (los números se agrupan para no repetir mensajes).
     */
    private static void fallo(String mensaje) {
        fallos.merge(mensaje, 1, Integer::sum);
    }
}
