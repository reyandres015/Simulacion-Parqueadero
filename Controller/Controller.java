/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

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
    private final UIVista vista;
    private final JPanel[] cuadrosLlegada;
    private final JPanel[] cuadrosRetrasados;
    private ParkingDao modelo;
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
                simulacionEntrada(new Carro(LocalDateTime.now(), 100 + modelo.getLlegada().size()));
            }
        }, 0, tiempoLlegada*1000); // Ejecutar cada 6 segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Desencolar un carro aleatorio cada 15 segundos
                if (!modelo.getLlegada().isEmpty()) {
                    simulacionSalida(random.nextInt((100 + modelo.getLlegada().size()) - 101 + 1) + 101);
                }
            }
        }, 0, tiempoServicio*1000); // Ejecutar cada 15 segundos

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                revisarTiempoVehiculos();
            }
        }, 0, 1000); // Ejecutar cada 1 segundos
    }

    public void simulacionEntrada(Carro carro) {
        if(!modelo.entrada(LocalDateTime.now(), 100 + modelo.getLlegada().size())){
            JOptionPane.showMessageDialog(null, "Carril Lleno");
        }
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
         pintaBoxes();

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
            this.modelo = new ParkingDao();
            simulacion(Long.parseLong(vista.tiempoEjecucionField.getText()),Long.parseLong(vista.tiempoLlegadaField.getText()), Long.parseLong(vista.tiempoServicioField.getText()));
        }
    }

}
