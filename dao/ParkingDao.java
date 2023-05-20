/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.Carro;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Usuario
 */
public class ParkingDao {

    private Queue<Carro> llegada = new LinkedList<>();
    private Queue<Carro> llegadaRetrasados = new LinkedList<>();
    private int capacidad = 10;

    public boolean entrada(LocalDateTime horaEntrada, int codigo) {
        if (llegada.size() < capacidad) {
            return llegada.add(new Carro(horaEntrada, codigo));
        } else {
            return false;
        }
    }

    public Queue<Carro> getLlegada() {
        return llegada;
    }

    public Queue<Carro> getLlegadaRetrasados() {
        return llegadaRetrasados;
    }

}
