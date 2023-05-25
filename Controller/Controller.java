/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import dao.ParkingDao;
import dto.Carro;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import vista.UIVista;

/**
 *
 * @author reyan
 */
public class Controller implements ActionListener {

    Random random = new Random();
    private final UIVista vista;
    private final JPanel[] cuadrosLlegada;
    private final JPanel[] cuadrosRetrasados;
    private ParkingDao modelo;
    private long cronometro;
    private LocalDateTime ultimaEntrada;
    private LocalDateTime ultimaSalida;
    private long tiempoLlegada;
    private long tiempoServicio;
    private List<String> notificaciones = new ArrayList<>();

    private LocalDateTime horaInicio;

    private Thread ejecucionTotalThread;
    private Thread llegadaThread;
    private Thread servicioThread;
    private Thread cronometroThread;

    public Controller() {
        this.vista = new UIVista();
        this.cuadrosLlegada = new JPanel[]{vista.jPanel0, vista.jPanel1, vista.jPanel2, vista.jPanel3, vista.jPanel4, vista.jPanel5, vista.jPanel6, vista.jPanel7, vista.jPanel8, vista.jPanel9};
        this.cuadrosRetrasados = new JPanel[]{vista.jRetrasados0, vista.jRetrasados1, vista.jRetrasados2, vista.jRetrasados3, vista.jRetrasados4, vista.jRetrasados5, vista.jRetrasados6, vista.jRetrasados7, vista.jRetrasados8, vista.jRetrasados9};
        vista.setVisible(true);
        this.vista.IniciarBtn.addActionListener(this);
    }

    public void simulacion(long tiempoEjecucion, long tiempoLlegada, long tiempoServicio) {
        this.cronometro = 0;
        this.horaInicio = LocalDateTime.now();
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoServicio = tiempoServicio;
        this.ultimaEntrada = LocalDateTime.now();
        this.ultimaSalida = LocalDateTime.now();

        ejecucionTotalThread = new Thread(() -> {
            try {
                Thread.sleep(tiempoEjecucion * 1000);
                cancelarSimulacion();
                mostrarMensaje("Fin de la Simulacion");
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Fallo Finalizacion Programa");
            }
        });

        llegadaThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!(cronometro == 0)) {
                    simulacionEntrada(new Carro(LocalDateTime.now(), 100 + modelo.getLlegada().size()));
                }
                try {
                    Thread.sleep(tiempoLlegada * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Fallo Llegada");
                    Thread.currentThread().interrupt();
                }
            }
        });

        servicioThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!modelo.getLlegada().isEmpty() || !modelo.getLlegadaRetrasados().isEmpty()) {
                    int codigo = random.nextInt(((modelo.getLlegada().size()) - 0 + 1)) + 0;
                    System.out.println(codigo);
                    if (!simulacionSalida(codigo, modelo.getLlegada())) {
                        if(!simulacionSalida(codigo, modelo.getLlegadaRetrasados())){
                            System.out.println("No encontrado");
                        }
                    }
                }
                try {
                    Thread.sleep(tiempoServicio * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Fallo Salida");
                    Thread.currentThread().interrupt();
                }
            }
        });

        cronometroThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                conteoRegresivo();
                revisarTiempoVehiculos();
                setLabelTiempoVehiculos();
                this.cronometro = this.horaInicio.until(LocalDateTime.now(), ChronoUnit.SECONDS);
                vista.timpoEjecucionTotalLabel.setText(String.valueOf(cronometro));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Fallo Cronometro");
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });

        ejecucionTotalThread.start();
        llegadaThread.start();
        servicioThread.start();
        cronometroThread.start();
    }

    private void cancelarSimulacion() {
        ejecucionTotalThread.interrupt();
        llegadaThread.interrupt();
        servicioThread.interrupt();
        cronometroThread.interrupt();
    }

    private void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, mensaje);
        });
    }

    public void simulacionEntrada(Carro carro) {
        if (!modelo.entrada(LocalDateTime.now(), 100 + modelo.getLlegada().size())) {
            JOptionPane.showMessageDialog(null, "Carril Lleno");
        } else {
            notificaciones.add("Entrada");
            actualizarNotificaciones();

            ultimaEntrada = LocalDateTime.now();
        }
        pintaBoxes();
    }

    public boolean simulacionSalida(int codigo, Queue<Carro> parqueo) {
        List<Carro> carros = (parqueo.stream())
                .filter(carro -> carro.getCodigo() == codigo)
                .collect(Collectors.toList());

        Carro carro = null;
        if (!carros.isEmpty()) {
            System.out.println("Salida");
            carro = carros.get(0);
            notificaciones.add(carro.toString());
            actualizarNotificaciones();
            pintaBoxes();
            ultimaSalida = LocalDateTime.now();
            // Eliminar el carro de la pila correspondiente
            parqueo.remove(carro);
            return true;
        } else {
            return false;
        }

    }

    public void conteoRegresivo() {
        long tiempoEntrada, tiempoSalida;
        tiempoEntrada = tiempoLlegada - (ultimaEntrada.until(LocalDateTime.now(), ChronoUnit.SECONDS));
        tiempoSalida = tiempoServicio - (ultimaSalida.until(LocalDateTime.now(), ChronoUnit.SECONDS));
        vista.cuentaEntradaLabel.setText(String.valueOf(tiempoEntrada));
        vista.cuentaSalidaLabel.setText(String.valueOf(tiempoSalida));

    }

    public void actualizarNotificaciones() {
        String[] not = notificaciones.toArray(new String[notificaciones.size()]);
        vista.notificationArea.setListData(not);
    }

    public void pintaBoxes() {
        int i = 0;
        Queue<Carro> llegada = modelo.getLlegada();
        Queue<Carro> llegadaRetrasados = modelo.getLlegadaRetrasados();

        for (JPanel cuadrosLlegada1 : cuadrosLlegada) {
            if (i < llegada.size()) {
                cuadrosLlegada1.setBackground(Color.green);
                i++;
            } else {
                cuadrosLlegada1.setBackground(Color.white);
                JPanel jPanel = cuadrosLlegada1;
                Component[] components = jPanel.getComponents();

                for (Component component : components) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        label.setText(String.valueOf(""));
                    }
                }
            }
        }

        i = 0;
        for (JPanel cuadrosRetrasado : cuadrosRetrasados) {
            if (i < llegadaRetrasados.size()) {
                cuadrosRetrasado.setBackground(Color.green);
                i++;
            } else {
                cuadrosRetrasado.setBackground(Color.white);
                JPanel jPanel = cuadrosRetrasado;
                Component[] components = jPanel.getComponents();

                for (Component component : components) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        label.setText(String.valueOf(""));
                    }
                }
            }
        }
    }

    public void setLabelTiempoVehiculos() {
        int i = 0;
        Iterator<Carro> iterator = modelo.getLlegada().iterator();
        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            if (i < cuadrosLlegada.length) {
                JPanel jPanel = cuadrosLlegada[i];
                Component[] components = jPanel.getComponents();

                for (Component component : components) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        carro.setTiempo(LocalDateTime.now());
                        long tiempoCarro = carro.getTiempo();
                        label.setText(String.valueOf(tiempoCarro));
                    }
                }
            }
            i++;
        }
        i = 0;
        iterator = modelo.getLlegadaRetrasados().iterator();
        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            if (i < cuadrosRetrasados.length) {
                JPanel jPanel = cuadrosRetrasados[i];
                Component[] components = jPanel.getComponents();

                for (Component component : components) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        carro.setTiempo(LocalDateTime.now());
                        long tiempoCarro = carro.getTiempo();
                        label.setText(String.valueOf(tiempoCarro));
                    }
                }
            }
            i++;
        }

    }

    public void revisarTiempoVehiculos() {
        List<Carro> nuevaLista = (List<Carro>) modelo.getLlegada();

        Iterator<Carro> iterator = nuevaLista.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            carro.setTiempo(LocalDateTime.now());
            if (carro.getTiempo() >= 15) {
                iterator.remove();
                modelo.getLlegadaRetrasados().add(carro);
                notificaciones.add("Un carro excedio el tiempo limite");
                pintaBoxes();
                return; // Salir del método después de desencolar el elemento encontrado
            } else {
                i++;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(vista.IniciarBtn)) {
            this.modelo = new ParkingDao();
            simulacion(Long.parseLong(vista.tiempoEjecucionField.getText()), Long.parseLong(vista.tiempoLlegadaField.getText()), Long.parseLong(vista.tiempoServicioField.getText()));
        }
    }

}
