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
    private int cronometro;
    private Timer timer = new Timer();

    public Controller() {
        this.vista = new UIVista();
        this.cuadrosLlegada = new JPanel[]{vista.jPanel0, vista.jPanel1, vista.jPanel2, vista.jPanel3, vista.jPanel4, vista.jPanel5, vista.jPanel6, vista.jPanel7, vista.jPanel8, vista.jPanel9};
        this.cuadrosRetrasados = new JPanel[]{vista.jRetrasados0, vista.jRetrasados1, vista.jRetrasados2, vista.jRetrasados3, vista.jRetrasados4, vista.jRetrasados5, vista.jRetrasados6, vista.jRetrasados7, vista.jRetrasados8, vista.jRetrasados9};
        vista.setVisible(true);
        this.vista.IniciarBtn.addActionListener(this);
    }

    public void simulacion(long tiempoEjecucion, long tiempoLlegada, long tiempoServicio) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel(); // Cancelar todas las tareas del temporizador, es decir finaliza la simulación.
            }
        }, tiempoEjecucion * 1000); // Cancelar el temporizador después de 'tiempoEjecucion' segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Encolar un nuevo carro cada 6 segundos
                if (!(cronometro == 0)) {
                    simulacionEntrada(new Carro(LocalDateTime.now(), 100 + modelo.getLlegada().size()));
                }
            }
        }, 0, tiempoLlegada * 1000); // Ejecutar cada 6 segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Desencolar un carro aleatorio cada 15 segundos
                if (!modelo.getLlegada().isEmpty() || cronometro == 0) {
                    simulacionSalida(random.nextInt((100 + modelo.getLlegada().size()) - 100 + 1) + 100);
                }
            }
        }, 0, tiempoServicio * 1000); // Ejecutar cada 15 segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                revisarTiempoVehiculos();
                setLabelTiempoVehiculos();
                cronometro++;
                vista.timpoEjecucionTotalLabel.setText(String.valueOf(cronometro));
            }
        }, 0, 1000); // Ejecutar cada 1 segundos
    }

    public void simulacionEntrada(Carro carro) {
        if (!modelo.entrada(LocalDateTime.now(), 100 + modelo.getLlegada().size())) {
            JOptionPane.showMessageDialog(null, "Carril Lleno");
        }
        pintaBoxes();
    }

    public void simulacionSalida(int codigo) {
        Iterator<Carro> iterator = modelo.getLlegada().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            if (carro.getCodigo() == codigo) {
                iterator.remove();
                seleccionarCarros(i);
                JOptionPane.showMessageDialog(null, carro.toString());
                pintaBoxes();
                return; // Salir del método después de retirar el carro
            } else {
                carro.setMovimientos();
                i++;
            }
        }

        iterator = modelo.getLlegadaRetrasados().iterator();
        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            if (carro.getCodigo() == codigo) {
                iterator.remove();
                JOptionPane.showMessageDialog(null, carro.toString());
                pintaBoxes();
                return; // Salir del método después de retirar el carro
            }
        }
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
            }
        }

        i = 0;
        for (JPanel cuadrosRetrasado : cuadrosRetrasados) {
            if (i < llegadaRetrasados.size()) {
                cuadrosRetrasado.setBackground(Color.green);
                i++;
            } else {
                cuadrosRetrasado.setBackground(Color.white);
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

    }

    public void revisarTiempoVehiculos() {
        Iterator<Carro> iterator = modelo.getLlegada().iterator();
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
