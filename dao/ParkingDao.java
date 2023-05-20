/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import ColaNodosDinamica.TDAColas;
import dto.Carro;
import java.time.LocalDateTime;

/**
 *
 * @author Usuario
 */
public class ParkingDao {

    private TDAColas llegada = new TDAColas();
    private TDAColas llegadaRetrasados = new TDAColas();
    private final int capacidad;

    public ParkingDao(int capacidad) {
        this.capacidad = capacidad;
    }

    public boolean entrada(LocalDateTime horaEntrada, int codigo) {
        if (llegada.size() < capacidad) {
            return llegada.enqueue(new Carro(horaEntrada, codigo));
        } else {
            return false;
        }
    }

    public TDAColas getLlegada() {
        return llegada;
    }

    public TDAColas getLlegadaRetrasados() {
        return llegadaRetrasados;
    }

}
