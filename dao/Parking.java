/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import ColaNodosDinamica.TDAColas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import vista.UIVista;

/**
 *
 * @author Usuario
 */
public class Parking implements ActionListener {

    private LocalDateTime horaInicioSimulacion;
    private UIVista vista;
    private TDAColas llegada = new TDAColas();

    public Parking() {
        this.horaInicioSimulacion = LocalDateTime.now();
        this.vista = new UIVista();
        vista.setVisible(true);
        this.vista.IniciarBtn.addActionListener(this);
    }

    public void simulacion(int tiempoEjecucion) {
        while (tiempoEjecucion != (horaInicioSimulacion.until(LocalDateTime.now(), ChronoUnit.SECONDS))) {
            if (horaInicioSimulacion.until(LocalDateTime.now(), ChronoUnit.SECONDS) == 6) {
                entrada();
            }

        }
    }

    public void entrada(){
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(vista.IniciarBtn)) {
            simulacion(Integer.valueOf(vista.tiempoEjecucionField.getText()));
        }

    }
