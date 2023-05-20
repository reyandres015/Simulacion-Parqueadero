/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import ColaNodosDinamica.Nodos;
import ColaNodosDinamica.TDAColas;
import dao.ParkingDao;
import dto.Carro;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import vista.UIVista;

/**
 *
 * @author reyan
 */
public class Controller implements ActionListener {

    Random random = new Random();
    private UIVista vista;
    private final JPanel[] cuadrosLlegada;
    private final JPanel[] cuadrosRetrasados;
    private ParkingDao modelo;
    private LocalDateTime horaInicioSimulacion;
    private Timer timer = new Timer();

    public Controller() {
        this.horaInicioSimulacion = LocalDateTime.now();
        this.vista = new UIVista();
        this.cuadrosLlegada = new JPanel[]{vista.jPanel0, vista.jPanel1, vista.jPanel2, vista.jPanel3, vista.jPanel4, vista.jPanel5, vista.jPanel6, vista.jPanel7, vista.jPanel8, vista.jPanel9};
        this.cuadrosRetrasados = new JPanel[]{vista.jRetrasados0, vista.jRetrasados1, vista.jRetrasados2, vista.jRetrasados3, vista.jRetrasados4, vista.jRetrasados5, vista.jRetrasados6, vista.jRetrasados7, vista.jRetrasados8, vista.jRetrasados9};
        vista.setVisible(true);
        this.vista.IniciarBtn.addActionListener(this);
    }

    public void simulacion(int tiempoEjecucion) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Encolar un nuevo carro cada 6 segundos
                simulacionEntrada(new Carro(LocalDateTime.now(), 100 + modelo.getLlegada().size()));

                // Verificar si algún carro supera los 15 segundos y moverlo a la cola de retrasados
                revisarTiempoVehiculos();
            }
        }, 0, 6000); // Ejecutar cada 6 segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Desencolar un carro aleatorio cada 15 segundos
                if (!modelo.getLlegada().isEmpty()) {
                    simulacionSalida(random.nextInt((100 + modelo.getLlegada().size()) - 101 + 1) + 101);
                    revisarTiempoVehiculos();
                }
            }
        }, 0, 15000); // Ejecutar cada 15 segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                revisarTiempoVehiculos();
            }
        }, 0, 1000); // Ejecutar cada 1 segundos
    }

    public void simulacionEntrada(Carro carro) {
        modelo.entrada(LocalDateTime.now(), 100 + modelo.getLlegada().size());
        pintaBoxes();
    }

    public void simulacionSalida(int codigo) {
        boolean validacion = modelo.getLlegada().removeIf(carro -> {
            if (carro.getCodigo() == codigo) {
                carro.setTiempo(LocalDateTime.now());
                JOptionPane.showMessageDialog(null, carro.toString());
                return true; // Se cumple la condición y se desencola el carro
            }
            return false; // No se cumple la condición y no se desencola el carro
        });

        if (!validacion) {
            simulacionSalida(codigo);
        } else {
            pintaBoxes();
        }

    }

    public void pintaBoxes() {
        int i = 0;
        Queue<Carro> llegada = modelo.getLlegada();
        Queue<Carro> llegadaRetrasados = modelo.getLlegadaRetrasados();

        for (int j = 0; j < cuadrosLlegada.length; j++) {
            if (i < llegada.size()) {
                cuadrosLlegada[j].setBackground(Color.green);
                i++;
            } else {
                cuadrosLlegada[j].setBackground(Color.white);
            }
        }

        i = 0;
        for (int j = 0; j < cuadrosRetrasados.length; j++) {
            if (i < llegadaRetrasados.size()) {
                cuadrosRetrasados[j].setBackground(Color.green);
                i++;
            } else {
                cuadrosRetrasados[j].setBackground(Color.white);
            }
        }
    }

    public void revisarTiempoVehiculos() {
        Iterator<Carro> iterator = modelo.getLlegada().iterator();
        while (iterator.hasNext()) {
            Carro carro = iterator.next();
            carro.setTiempo(LocalDateTime.now());
            if (carro.getTiempo() >= 15) {
                iterator.remove();
                JOptionPane.showMessageDialog(null, "Un carro excedio el tiempo limite");
                pintaBoxes();
                return; // Salir del método después de desencolar el elemento encontrado
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(vista.IniciarBtn)) {
            this.modelo = new ParkingDao(Integer.parseInt(vista.capacidadField.getText()));
            simulacion(Integer.parseInt(vista.tiempoEjecucionField.getText()));
        }
    }

}
