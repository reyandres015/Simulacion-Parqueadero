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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
                JOptionPane.showMessageDialog(null, "Fin de la Simulacion");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        llegadaThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!(cronometro == 0)) {
                    System.out.println("Entrada");
                    simulacionEntrada(new Carro(LocalDateTime.now(), 100 + modelo.getLlegada().size()));
                }
                try {
                    Thread.sleep(tiempoLlegada * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        servicioThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!modelo.getLlegada().isEmpty() || !modelo.getLlegadaRetrasados().isEmpty()) {
                    System.out.println("Salida");
                    simulacionSalida(random.nextInt((100 + modelo.getLlegada().size()) - 100 + 1) + 100);
                }
                try {
                    Thread.sleep(tiempoServicio * 1000);
                } catch (InterruptedException e) {
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

    public void simulacionEntrada(Carro carro) {
        if (!modelo.entrada(LocalDateTime.now(), 100 + modelo.getLlegada().size())) {
            JOptionPane.showMessageDialog(null, "Carril Lleno");
        } else {
            ultimaEntrada = LocalDateTime.now();
        }
        pintaBoxes();
    }

    public void simulacionSalida(int codigo) {
        Iterator<Carro> iterator = modelo.getLlegada().iterator();
        int i = 0;
        boolean validacion = false;

        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            if (carro.getCodigo() == codigo) {
                iterator.remove();
                seleccionarCarros(i);
                JOptionPane.showMessageDialog(null, carro.toString());
                pintaBoxes();
                validacion = true;
                ultimaSalida = LocalDateTime.now();
                return;
            } else {
                carro.setMovimientos();
                i++;
            }
        }

        if (!validacion) {
            System.out.println("Entre");
            iterator = modelo.getLlegadaRetrasados().iterator();
            while (iterator.hasNext()) {
                Carro carro = iterator.next();
                if (carro.getCodigo() == codigo) {
                    iterator.remove();
                    JOptionPane.showMessageDialog(null, carro.toString());
                    pintaBoxes();
                    ultimaSalida = LocalDateTime.now();
                    return; // Salir del método después de retirar el carro
                }
            }
        }
    }

    public void conteoRegresivo() {
        long tiempoEntrada, tiempoSalida;
        tiempoEntrada = tiempoLlegada - (ultimaEntrada.until(LocalDateTime.now(), ChronoUnit.SECONDS));
        tiempoSalida = tiempoServicio - (ultimaSalida.until(LocalDateTime.now(), ChronoUnit.SECONDS));
        vista.cuentaEntradaLabel.setText(String.valueOf(tiempoEntrada));
        vista.cuentaSalidaLabel.setText(String.valueOf(tiempoSalida));

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

    private void seleccionarCarros(int index) {
        if (cuadrosLlegada.length > index) {
            cuadrosLlegada[index].setBackground(Color.red);
        } else if (cuadrosRetrasados.length > index) {
            cuadrosRetrasados[index].setBackground(Color.red);
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
                seleccionarCarros(i);
                JOptionPane.showMessageDialog(null, "Un carro excedio el tiempo limite");
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
