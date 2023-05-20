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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        // Crear una cola temporal para almacenar los elementos que no se desencolarán
        TDAColas colaTemporal = new TDAColas();
        // Buscar el elemento y desencolarlo
        while (!modelo.getLlegada().isEmpty()) {
            Carro elemento = modelo.getLlegada().dequeue();
            if (elemento.getCodigo() == codigo) {
                // Elemento encontrado, no se vuelve a encolar
                elemento.setTiempo(LocalDateTime.now());
                JOptionPane.showMessageDialog(null, elemento.toString());
            } else {
                // Elemento no buscado, se vuelve a encolar en la cola temporal
                colaTemporal.enqueue(elemento);
            }
        }

        // Volver a encolar los elementos restantes desde la cola temporal a la cola original
        for (int i = 0; i < colaTemporal.size(); i++) {
            modelo.getLlegada().enqueue(colaTemporal.dequeue());
        }

        pintaBoxes();
    }

    public void pintaBoxes() {
        Nodos temp = modelo.getLlegada().peekFront();
        int i = 0;
        while (temp != null) {
            cuadrosLlegada[i].setBackground(Color.green);
            temp = temp.getSiguiente();
            i++;
        }
        for (int j = i; j < cuadrosLlegada.length; j++) {
            cuadrosLlegada[j].setBackground(Color.white);
        }
        temp = modelo.getLlegadaRetrasados().peekFront();
        i = 0;
        while (temp != null) {
            cuadrosRetrasados[i].setBackground(Color.green);
            temp = temp.getSiguiente();
            i++;
        }

        for (int j = i; j < cuadrosRetrasados.length; j++) {
            cuadrosRetrasados[j].setBackground(Color.white);
        }
    }

    public void revisarTiempoVehiculos() {

        for (Vehiculo vehiculo : parqueadero) {
            if (System.currentTimeMillis() - vehiculo.getTiempoIngreso() >= 15) {
                vehiculosDesencolados.add(vehiculo);
            }
        }

        while (!vehiculosDesencolados.isEmpty()) {
            Vehiculo vehiculo = vehiculosDesencolados.poll();
            parqueadero.remove(vehiculo);
            System.out.println("El vehículo " + vehiculo.getPlaca() + " ha superado el tiempo límite y fue desencolado.");
        }
        pintaBoxes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(vista.IniciarBtn)) {
            this.modelo = new ParkingDao(Integer.parseInt(vista.capacidadField.getText()));
            simulacion(Integer.parseInt(vista.tiempoEjecucionField.getText()));
        }
    }

}
