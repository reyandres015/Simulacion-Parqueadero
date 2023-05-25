package dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Usuario
 */
public class Carro implements Serializable {

    private final int codigo;
    private final LocalDateTime horaEntrada;
    private int movimientos = 0;
    private long tiempo;
    private long valor;

    public Carro(LocalDateTime horaEntrada, int codigo) {
        this.codigo = codigo;
        this.horaEntrada = horaEntrada;
    }

    public void setMovimientos() {
        this.movimientos++;
    }

    public void setTiempo(LocalDateTime horaActual) {
        this.tiempo = horaEntrada.until(horaActual, ChronoUnit.SECONDS);
        setValor();
    }

    public void setValor() {
        this.valor = 252 * tiempo;
    }

    public void movimiento() {
        movimientos++;
    }

    public long getTiempo() {
        return tiempo;
    }

    public int getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return "Carro{ " + "Codigo=" + codigo
                + ", Hora de Entrada=" + horaEntrada.toLocalTime()
                + ", Movimientos=" + movimientos
                + ", Tiempo de Parqueo=" + tiempo
                + ", Valor a Pagar: $" + valor + '}';
    }
}
